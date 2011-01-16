package com.modestmaps;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.util.math.Plane;
import org.mt4j.util.math.Ray;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GLTextureSettings;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTexture.EXPANSION_FILTER;
import org.mt4j.util.opengl.GLTexture.SHRINKAGE_FILTER;
import org.mt4j.util.opengl.GLTexture.WRAP_MODE;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix2D;
import processing.opengl.PGraphicsOpenGL;

import com.modestmaps.core.Coordinate;
import com.modestmaps.core.Point2f;
import com.modestmaps.geo.Location;
import com.modestmaps.providers.AbstractMapProvider;
import com.modestmaps.providers.Microsoft;

public class TestInteractiveMap extends AbstractVisibleComponent implements PConstants {
	// I have made the dumb mistake of getting these wrong before...
	// it's REALLY unlikely you'll want to change them:
	public int TILE_WIDTH = 256;
	public int TILE_HEIGHT = 256;
//	public int TILE_WIDTH = 512;
//	public int TILE_HEIGHT = 512;

	// unavoidable right now, for loadImage and float maths
	public PApplet p;

	// pan and zoom
	public double tx = -TILE_WIDTH/2; // half the world width, at zoom 0
	public double ty = -TILE_HEIGHT/2; // half the world height, at zoom 0
	public double sc = 1;

	// limit simultaneous calls to loadImage
	public int MAX_PENDING = 4;

	// limit tiles in memory
	// 256 would be 64 MB, you may want to lower this quite a bit for your app
	public int MAX_IMAGES_TO_KEEP = 128;

	// upping this can help appearances when zooming out, but also loads many more tiles
	public int GRID_PADDING = 1;

	// what kinda maps?
	public AbstractMapProvider provider;

	// how big?
	public float width, height;

	// loading tiles
	public Hashtable pending; // coord -> TileLoader
	// loaded tiles
	public Hashtable images;  // coord -> PImage
	// coords waiting to load
	public Vector queue;
	// a list of the most recent MAX_IMAGES_TO_KEEP PImages we've seen
	public Vector recentImages;

	// for sorting coordinates by zoom
	public ZoomComparator zoomComparator;

	// for loading tiles from the inside first
	public QueueSorter queueSorter;
	
	//For fading in tiles
	private HashMap<GLTexture, Integer> opacity;
	private float [] fades = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};

	/** default to Microsoft Hybrid */
	public TestInteractiveMap(PApplet p) {
		this(p, new Microsoft.HybridProvider());
	}

	/** new map using applet width and height, and given provider */
	public TestInteractiveMap(PApplet p, AbstractMapProvider provider) {
		this(p, provider, p.width, p.height);
	}

	/** make a new interactive map, using the given provider, of the given width and height */
	public TestInteractiveMap(PApplet p, AbstractMapProvider provider, float width, float height) {
		super(p);
		
		this.p 			= p;
		this.provider 	= provider;
		this.width 		= width;
		this.height 	= height;
		
		TILE_WIDTH  = provider.tileWidth();
		TILE_HEIGHT = provider.tileHeight();

		// pan and zoom
		tx = -TILE_WIDTH/2; // half the world width, at zoom 0
		ty = -TILE_HEIGHT/2; // half the world height, at zoom 0
		
		pending 		= new Hashtable();
		images 			= new Hashtable();
		queue 			= new Vector();
		recentImages 	= new Vector();
		zoomComparator 	= new ZoomComparator();
		queueSorter 	= new QueueSorter();
		
		// fit to screen
		sc = p.ceil(p.min(height/(float)TILE_WIDTH, width/(float)TILE_HEIGHT));
		
		model 	= DoubleBuffer.allocate(16);
		proj 	= DoubleBuffer.allocate(16);
		view 	= IntBuffer.allocate(4);
		winPos 	= DoubleBuffer.allocate(3);
		
		activeOglTextureIDs = new ArrayList<Integer>(MAX_IMAGES_TO_KEEP);
//		activeOglTextureIDs = new Vector(); 
		
//		plane = new Plane( new Vector3D(0,0,0), new Vector3D(0,0,1));
		plane = Plane.XY;
		
		visibleKeys = new Vector();
		
		opacity = new HashMap<GLTexture, Integer>();
	}

	///////////////
	private DoubleBuffer model;
	private DoubleBuffer proj;
	private IntBuffer view;
	private DoubleBuffer winPos;
	private ArrayList<Integer> activeOglTextureIDs;
	
	private Plane plane;
	
	private Vector visibleKeys ;
	///////////////////////
	
	@Override
	public void drawComponent(PGraphics g){
		PGraphicsOpenGL pgl = ((PGraphicsOpenGL)p.g);
		GL gl = pgl.beginGL();
		GLU glu = pgl.glu;
		
		gl.glPushMatrix();
			//Draw!
			drawPureGL(gl, glu);  
		gl.glPopMatrix();
		pgl.endGL();
	}
	
	@Override
	public Vector3D getIntersectionLocal(Ray ray){
		return plane.getIntersectionLocal(ray);
//		return ToolsIntersection.getRayPlaneIntersection(ray, new Vector3D(0,0,1), new Vector3D(0,0,0));
	}
	
	@Override
	protected boolean componentContainsPointLocal(Vector3D testPoint){
		int c = plane.classifyPoint(testPoint);
		switch (c) {
		case Plane.ON_PLANE:
			return true;
		default:
			break;
		}
		return false;
	}
	
	/** draw the map on the given PApplet 
	 * @param gl
	 * @param glu
	 */
	public void drawPureGL(GL gl, GLU glu) {
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// remember smooth setting so it can be reset 
		boolean smooth = p.g.smooth;

		// !!! VERY IMPORTANT
		// (all the renderers apart from OpenGL will choke if you ask for smooth scaling of image calls)
		p.noSmooth();

		/*
		// translate and scale, from the middle
		p.pushMatrix();
		p.translate(width/2, height/2);
		p.scale((float)sc);
		p.translate((float)tx, (float)ty);
		*/
		
//		/*
		gl.glPushMatrix();
		//Translate in die mitte
		gl.glTranslatef(width/2, height/2, 0);
		//Scale from the middle
		gl.glScalef((float)sc, (float)sc, 1);
		
//		gl.glScalef((float)sc, (float)sc, (float)sc);
		//Translate -TILE_WIDTH/2; -TILE_HEIGHT/2;  // half the world width, at zoom 0
		gl.glTranslatef((float)tx, (float)ty, 0);
//		*/
		
		
		
		/*
		// find the bounds of the ur-tile in screen-space:
		float minX = p.screenX(0,0);
		float minY = p.screenY(0,0);
		float maxX = p.screenX(TILE_WIDTH, TILE_HEIGHT);
		float maxY = p.screenY(TILE_WIDTH, TILE_HEIGHT);
		*/
		
		
//		/*
		model.clear();
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model);
		
		proj.clear();
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, proj);
		
		view.clear();
		gl.glGetIntegerv(GL.GL_VIEWPORT, view);
		float viewPortHeight = (float)view.get(3);
		
		winPos.clear();
		//See where position (0,0,0) projects to on screen
		glu.gluProject(0, 0, 0, model, proj, view, winPos);
		
		
		winPos.rewind();
//		float minX = (float) winPos.get(0);
//		float minY = (float) winPos.get(1);
		float minX = (float) winPos.get();
		float minY = (float) winPos.get();
		minY = viewPortHeight - minY;// Subtract The Current Y Coordinate From The Screen Height.
		
		///////////
		winPos.clear();
		//See where position (TILE_WIDTH, TILE_HEIGHT, 0) projects to on screen
		glu.gluProject(TILE_WIDTH, TILE_HEIGHT, 0, model, proj, view, winPos);
		
//		Vector3D b = new Vector3D(TILE_WIDTH, TILE_HEIGHT);
//		b.transform(Matrix.getZRotationMatrix(new Vector3D(0,0,0), 90));
//		glu.gluProject(b.x, b.y, 0, model, proj, view, winPos);//See where position (TILE_WIDTH, TILE_HEIGHT, 0) projects to on screen
		
		winPos.rewind();
		float maxX = (float) winPos.get();
		float maxY = (float) winPos.get();
		maxY = viewPortHeight - maxY;
//		*/

//		System.out.println("Projected (0,0,0) => MinXY:(" + minX + ", " + minY +  ") (" + TILE_WIDTH + ", "  + TILE_HEIGHT + ", 0) => MaxXY:(" + maxX + ", " + maxY + ")");
//		-84.0 -84.0 684.0 684.0 //unrotated
//		84.0 -84.0 -684.0 684.0 //rotated
		
//		gl.glTranslatef(TILE_WIDTH/2, TILE_HEIGHT/2,0); //TODO REMOVE
//		gl.glScalef((float)0.7f, (float)0.7f, 1); 
//		gl.glTranslatef(-TILE_WIDTH/2, -TILE_HEIGHT/2,0);
		
		// what power of 2 are we at?
		// 0 when scale is around 1, 1 when scale is around 2, 
		// 2 when scale is around 4, 3 when scale is around 8, etc.
		int zoom = bestZoomForScale((float)sc);

		// how many columns and rows of tiles at this zoom?
		// (this is basically (int)sc, but let's derive from zoom to be sure 
		int cols = (int)PApplet.pow(2, zoom);
		int rows = (int)PApplet.pow(2, zoom);

		// find the biggest box the screen would fit in:, aligned with the map:
		float screenMinX = 0;
		float screenMinY = 0;
		
		float screenMaxX = width;
		float screenMaxY = height;
		
		
		
		/*//for map Rotated 90�
		Vector3D min = new Vector3D(0,0,0);
		Matrix m1 = Matrix.getZRotationMatrix(new Vector3D(width/2, height/2, 0), 90);
//		m1.removeTranslationFromMatrix();
		min.transform(m1);
		System.out.println("Min:" + min);
		
		Vector3D max = new Vector3D(width, height, 0);
		Matrix m2 = Matrix.getZRotationMatrix(new Vector3D(width/2, height/2, 0), 90);
//		m2.removeTranslationFromMatrix();
		max.transform(m2);
		System.out.println("Max:" + max);
		
		//TODO mit vectoren die rotation maachen und screenMinX etc ausrechnen
		//je nach rotation
		//TODO auch f�r min/maxCols machen
		
		
//		screenMinX = height;
//		screenMinY = 0;
//		screenMaxX = 0;
//		screenMaxY = width;
		
		screenMinX = width;
		screenMinY = 0;
		screenMaxX = 0;
		screenMaxY = height;
		
		System.out.println("Screen => MinXY:(" + screenMinX + ", " + screenMinY +  ") => MaxXY:(" + screenMaxX + ", " + screenMaxY + ")");
		// TODO: align this, and fix the next bit to work with rotated maps
		 */
		
		// find start and end columns
		int minCol = (int)PApplet.floor(cols * (screenMinX-minX) / (maxX-minX));
		int maxCol = (int)PApplet.ceil(cols  * (screenMaxX-minX) / (maxX-minX));
		int minRow = (int)PApplet.floor(rows * (screenMinY-minY) / (maxY-minY));
		int maxRow = (int)PApplet.ceil(rows  * (screenMaxY-minY) / (maxY-minY));
		
		/*//for map Rotated 90�
		int tmp = minCol;
		minCol = minRow;
		minRow = tmp;
		int tmp2 = maxCol;
		maxCol = maxRow;
		maxRow = tmp2;
		
		System.out.println("Mincol: " + minCol + " maxCol: " + maxCol + " minrow: " + minRow + " maxRow: "  + maxRow);
		*/
		
		
		
		// pad a bit, for luck (well, because we might be zooming out between zoom levels)
		minCol -= GRID_PADDING;
		minRow -= GRID_PADDING;
		maxCol += GRID_PADDING;
		maxRow += GRID_PADDING;

		// we don't wrap around the world yet, so:
		minCol = PApplet.constrain(minCol, 0, cols);
		maxCol = PApplet.constrain(maxCol, 0, cols);
		minRow = PApplet.constrain(minRow, 0, rows);
		maxRow = PApplet.constrain(maxRow, 0, rows);

		// keep track of what we can see already:
//		Vector visibleKeys = new Vector();
		visibleKeys.clear();
	
		// grab coords for visible tiles
		for (int col = minCol; col <= maxCol; col++) {
			for (int row = minRow; row <= maxRow; row++) {

				// source coordinate wraps around the world:
				Coordinate coord = provider.sourceCoordinate(new Coordinate(row, col, zoom));

				// let's make sure we still have ints:
				coord.row 		= PApplet.round(coord.row);
				coord.column 	= PApplet.round(coord.column);
				coord.zoom 		= PApplet.round(coord.zoom);

				// keep this for later:
				visibleKeys.add(coord);

				if (!images.containsKey(coord)) {
					// fetch it if we don't have it
					grabTile(coord);

					// see if we have  a parent coord for this tile?
					boolean gotParent = false;
					for (int i = (int)coord.zoom; i > 0; i--) {
						Coordinate zoomed = coord.zoomTo(i).container();
						// make sure we still have ints:
						zoomed.row 		= PApplet.round(zoomed.row);
						zoomed.column 	= PApplet.round(zoomed.column);
						zoomed.zoom 	= PApplet.round(zoomed.zoom);
						if (images.containsKey(zoomed)) {
							visibleKeys.add(zoomed);
							gotParent = true;
							break;
						}
					}

					// or if we have any of the children
					if (!gotParent) {
						Coordinate zoomed = coord.zoomBy(1).container();
						Coordinate[] kids = { zoomed, zoomed.right(), zoomed.down(), zoomed.right().down() }; 
						for (int i = 0; i < kids.length; i++) {
							zoomed = kids[i];
							// make sure we still have ints:
							zoomed.row 		= PApplet.round(zoomed.row);
							zoomed.column 	= PApplet.round(zoomed.column);
							zoomed.zoom 	= PApplet.round(zoomed.zoom);
							if (images.containsKey(zoomed)) {
								visibleKeys.add(zoomed);
							} 
						} //for kids           
					} //if (!gotParent)
				}//if (!images.containsKey(coord))

			} //for rows
		} //for columns

		// sort by zoom so we draw small zoom levels (big tiles) first:
		Collections.sort(visibleKeys, zoomComparator);

		if (visibleKeys.size() > 0) {
			Coordinate previous = (Coordinate)visibleKeys.get(0);
			
			/*
			p.pushMatrix();
			// correct the scale for this zoom level:
			p.scale(1.0f/p.pow(2, previous.zoom));
			*/
			
//			/*
			gl.glPushMatrix();
			float scale = 1.0f/PApplet.pow(2, previous.zoom);
			gl.glScalef(scale, scale, 1);
//			*/
			
			int tilesDrawn = 0;
			
			for (int i = 0; i < visibleKeys.size(); i++) {
				Coordinate coord = (Coordinate)visibleKeys.get(i);

				if (coord.zoom != previous.zoom) {
					/*
					p.popMatrix();
					p.pushMatrix();
					// correct the scale for this zoom level:
					p.scale(1.0f/p.pow(2, coord.zoom));
					*/
					
//					/*
					gl.glPopMatrix();
					gl.glPushMatrix();
					// correct the scale for this zoom level:
					float coordScale = 1.0f/PApplet.pow(2, coord.zoom);
					gl.glScalef(coordScale, coordScale, 1);
//					*/
				}

				//TODO HEre the drawing is done if image for coord already in image hashtable!
				if (images.containsKey(coord)) {
					
//					PImage tile = (PImage)images.get(coord); //Orginal
					GLTexture texture = (GLTexture)images.get(coord);
					
					//Init texture here, because it hast to happen in opgl thread
					if (!texture.isGLTexObjectInitialized()){
//						texture.initTexture(texture.width, texture.height);
						texture.setupGLTexture(texture.width, texture.height);
						texture.loadGLTexture(texture);
						//activeOglTextureIDs.add(texture.getTextureID());
					}
					
					float x 	= coord.column 	* TILE_WIDTH;
					float y 	= coord.row 	* TILE_HEIGHT;
					float width = TILE_WIDTH;
					float height= TILE_HEIGHT;
					
//					image(img, x, y, width, height)
//					p.image(tile, x, y, width, height);
//					p.image(tile, coord.column * TILE_WIDTH, coord.row * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);

					/*
					//generateDefault a GLTexture object for use with pure openGL
					GLTextureParameters tParams = new GLTextureParameters();
					tParams.minFilter = GLConstants.LINEAR;
					
					//Initialize emtpy texture
					GLTexture texture = new GLTexture(p, Math.round(width), Math.round(height) , tParams);
					//Fill the texture with pixels
					texture.putImage(tile);
					*/

					/** Begins control for opacity **/
					//FIXME TEST
					int level = opacity.containsKey(texture) ? opacity.remove(texture) : 0;
					float alpha = fades[level];
					if(level < fades.length-1) {
						opacity.put(texture, level+1);
					} else {
						opacity.put(texture, 10);
					}
//					System.out.println("Opacity entries: " + opacity.size());
					gl.glColor4f(1.0F, 1.0F, 1.0F, alpha);
					/** Ends control for opacity **/

					int textureTarget = texture.getTextureTarget();
					gl.glEnable(textureTarget); //weiter nach unten?
					gl.glBindTexture(textureTarget, texture.getTextureID());
					
					//..
					gl.glBegin (GL.GL_QUADS);
					gl.glTexCoord2f(0.0f, 0.0f);
					gl.glVertex3f (x, y, 0.0f);
					
					gl.glTexCoord2f (1.0f, 0.0f);
					gl.glVertex3f (x + width, y, 0.0f);
					
					gl.glTexCoord2f (1.0f, 1.0f);
					gl.glVertex3f (x + width, y + height, 0.0f);
					
					gl.glTexCoord2f (0.0f, 1.0f);
					gl.glVertex3f (x , y + height, 0.0f);
					gl.glEnd ();
					
					gl.glBindTexture(textureTarget, 0);//Unbind texture
					gl.glDisable(textureTarget); //weiter nach unten?
					
					//texture.deleteTextureGL();
					
					/*
					if (recentImages.contains(tile)) {
						recentImages.remove(tile);
					}
					recentImages.add(tile);
					*/
					
					
					tilesDrawn++;

					if (recentImages.contains(texture)) {
						recentImages.remove(texture);
					}
					recentImages.add(texture);

					//Keep track of active opengl textures
					if (texture.getTextureID() != 0){
						if ( activeOglTextureIDs.contains(texture.getTextureID())) {
							activeOglTextureIDs.remove(new Integer(texture.getTextureID()));	
						}
						activeOglTextureIDs.add(texture.getTextureID());
					}

				}
			}
			/*
			p.popMatrix();
			*/
			
//			System.out.println("Tiles drawn:" + tilesDrawn);
			
//			/*
			gl.glPopMatrix();
//			 */
		}    

		/*
		p.popMatrix();
		*/
//		/*
		gl.glPopMatrix();
//		 */
		
		// stop fetching things we can't see:
		// (visibleKeys also has the parents and children, if needed, but that shouldn't matter)
		queue.retainAll(visibleKeys);

		// sort what's left by distance from center:
		queueSorter.setCenter(new Coordinate( (minRow + maxRow) / 2.0f, (minCol + maxCol) / 2.0f, zoom));
		Collections.sort(queue, queueSorter);

		// load up to 4 more things:
		processQueue();

		// clear some images away if we have too many...
		//TODO evtl mehr l�schen als nur 1 oder zwei, denn so muss wenn grenze einmal erreicht its
		//bei jedem neuen tile wieder was gel�scht werden 
		if (recentImages.size() > MAX_IMAGES_TO_KEEP) {
			//System.out.println("Recent Images > MAX_IMAGES_TO_KEEP");
			
			List sublist = recentImages.subList(0, recentImages.size() - MAX_IMAGES_TO_KEEP);
			
			//Delete gl texture
			for (Iterator iter = sublist.iterator(); iter.hasNext();) {
				GLTexture texture = (GLTexture) iter.next();
				
				if (texture.getTextureID() != 0){
					if ( activeOglTextureIDs.contains(texture.getTextureID())) {
						activeOglTextureIDs.remove(new Integer(texture.getTextureID()));	
					}
					texture.destroy();
					
					 opacity.remove(texture); //FIXME TEST
				}
			}
			
			
			sublist.clear();
			images.values().retainAll(recentImages);
		}

		// restore smoothing, if needed
		if (smooth) {
			p.smooth();
		}
	} 

	
	@Override
	protected void destroyComponent() {
		super.destroyComponent();
		
		for (Iterator<GLTexture> iter = recentImages.iterator(); iter.hasNext();) {
			GLTexture texture = (GLTexture) iter.next();
			if (texture.getTextureID() != 0){
				texture.destroy();
			}
		}
		recentImages.clear();
		
		Collection imagesValues = images.values();
		for (Iterator<GLTexture> iter = imagesValues.iterator(); iter.hasNext();) {
			GLTexture texture = (GLTexture) iter.next();
			if (texture.getTextureID() != 0){
				texture.destroy();
			}
		}
		images.clear();
		
		activeOglTextureIDs.clear();
		
		pending.clear();
		
		queue.clear();
	}
	/*
    public function putMarker(location:Location, marker:DisplayObject=null):void
		public function getMarker(id:String):DisplayObject
	    public function removeMarker(id:String):void

	    public function setCopyright(copyright:String):void {
	 */

	/*
	    public function onStartZoom():void
	    public function onStopZoom():void
	    public function onZoomed(delta:Number):void

	    public function onStartPan():void
	    public function onStopPan():void
	    public function onPanned(delta:Point):void

	    public function onResized():void

	    public function onExtentChanged(extent:MapExtent):void
	 */



	///////////////////////////////////////////////////////////////////////

	public float scaleForZoom(int zoom) {
		return p.pow(2.0f, zoom);
	}

	public float zoomForScale(float scale) {
		return p.log(scale) / p.log(2);
	}

	public int bestZoomForScale(float scale) {
		return (int)p.min(20, p.max(1, (int)p.round(p.log(scale) / p.log(2))));
	}

	//////////////////////////////////////////////////////////////////////////


	/////////////////////////////////////////////////////////////////
	public void processQueue() {
		while (pending.size() < MAX_PENDING && queue.size() > 0) {
			Coordinate coord = (Coordinate)queue.remove(0);
			TileLoader tileLoader = new TileLoader(coord);
			pending.put(coord, tileLoader);
			new Thread(tileLoader).start();
		}  
	}
	
	
	public class TileLoader implements Runnable {
		Coordinate coord;
		
		TileLoader(Coordinate coord) {
			this.coord = coord; 
		}

		//TODO sleep einbauen!?
		public void run() {
			String[] urls = provider.getTileUrls(coord);
			
			PImage img = p.loadImage(urls[0], "unknown"); // use "unknown" to let loadImage decide 
			if (img != null) {
				for (int i = 1; i < urls.length; i++) {
					
					try { //FIXME ADDED oder ist loadimage() schon threaded?
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					PImage img2 = p.loadImage(urls[i], "unknown");
					if (img2 != null) {
						img.blend(img2, 0, 0, img.width, img.height, 0, 0, img.width, img.height, BLEND);
					}
				}
			}
			tileDone(coord, img);
		}
	}
	
	// TODO: there could be issues when this is called from within a thread
	// probably needs synchronizing on images / pending / queue
	public void tileDone(Coordinate coord, PImage img) {
		// check if we're still waiting for this (new provider clears pending)
		// also check if we got something
		if (pending.containsKey(coord) 
			&& img != null
		) {
			
			//Create GLTexture with pimage pixels but dont initialzie it
			//because were dont have valid context (thread)
//			MTTextureParameters tParams = new MTTextureParameters();
//			tParams.minFilter = GLConstants.NEAREST;
//			tParams.magFilter = GLConstants.NEAREST;
////			tParams.minFilter = GLConstants.LINEAR;
////			tParams.magFilter = GLConstants.LINEAR;
////			
			//Initialize emtpy texture
//			MTTexture texture = new MTTexture(p, img.width, img.height , tParams, false);
//			//Fill the texture with pixels
//			//texture.putImage(img);
//			texture.putImageOnly(img);
			
			GLTextureSettings ts = new GLTextureSettings();
//			ts.shrinkFilter = SHRINKAGE_FILTER.NearestNeighborNoMipMaps; //FIXME WHY NOT USE LINEAR?
//			ts.expansionFilter = EXPANSION_FILTER.NearestNeighbor;
			ts.shrinkFilter = SHRINKAGE_FILTER.BilinearNoMipMaps;
			ts.expansionFilter = EXPANSION_FILTER.Bilinear;
			ts.wrappingHorizontal = WRAP_MODE.CLAMP_TO_EDGE;
			ts.wrappingVertical = WRAP_MODE.CLAMP_TO_EDGE;
			
			GLTexture texture = new GLTexture(p, ts);
			texture.loadPImageTexture(img);
			
			images.put(coord, texture); //FIXME orginal
			
			//      p.println("got " + coord + " image");
//			images.put(coord, img); //FIXME orginal
			
			pending.remove(coord);  
		}
		else {
			//      p.println("failed to get " + coord + " image");
			//      if (img == null) {
			//        p.println("but got a null one");
			//      }
			// try again?
			// but this is a bit risky, TODO: keep track of attempts
			queue.add(coord);
			pending.remove(coord);  
		}
	}
	
	
	public void grabTile(Coordinate coord) {
		if (!pending.containsKey(coord) 
			&& !queue.contains(coord) 
			&& !images.containsKey(coord)
		) {
			//    println("adding " + coord.toString() + " to queue");
			queue.add(coord);
		}
	}

	
	
	public class QueueSorter implements Comparator {
		Coordinate center;
		
		public void setCenter(Coordinate center) {
			this.center = center;
		} 
		
		public int compare(Object o1, Object o2) {
			Coordinate c1 = (Coordinate)o1; 
			Coordinate c2 = (Coordinate)o2;
			if (c1.zoom == center.zoom) {
				if (c2.zoom == center.zoom) {
					float d1 = p.dist(center.column, center.row, c1.column, c1.row);
					float d2 = p.dist(center.column, center.row, c2.column, c2.row);
					return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
				} 
				else {
					return -1;
				}
			}
			else if (c2.zoom == center.zoom) {
				return 1;
			}
			else {
				float d1 = p.abs(c1.zoom - center.zoom);
				float d2 = p.abs(c2.zoom - center.zoom);
				return  d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
			}
		}
	}

	
	public class ZoomComparator implements Comparator {
		
		public int compare(Object o1, Object o2) {
			Coordinate c1 = (Coordinate)o1;
			Coordinate c2 = (Coordinate)o2;
			return c1.zoom < c2.zoom ? -1 : c1.zoom > c2.zoom ? 1 : 0;
		}
		
	}
	
	/** @return zoom level of currently visible tile layer */
	public int getZoom() {
		return bestZoomForScale((float)sc);
	}

	public Location getCenter() {
		return provider.coordinateLocation(getCenterCoordinate());
	}

	public Coordinate getCenterCoordinate() {
		float row = (float)(ty * sc / -TILE_WIDTH);
		float column = (float)(tx * sc / -TILE_HEIGHT);
		float zoom = zoomForScale((float)sc);
		return new Coordinate(row, column, zoom); 
	}

	public void setCenter(Coordinate center) {
		//println("setting center to " + center);
		sc = p.pow(2.0f, center.zoom);
		tx = -TILE_WIDTH * center.column / sc;
		ty = -TILE_HEIGHT * center.row / sc;
	}

	public void setCenter(Location location) {
		setCenter(provider.locationCoordinate(location).zoomTo(getZoom()));
	}

	public void setCenterZoom(Location location, int zoom) {
		setCenter(provider.locationCoordinate(location).zoomTo(zoom));
	}

	/** sets scale according to given zoom level, should leave you with pixel perfect tiles */
	public void setZoom(int zoom) {
		sc = p.pow(2.0f, zoom); 
//		System.out.println("\nZoom: " + zoom);
//		System.out.println("New sc: " + sc);
	}
	
	//FIXME EXPERIMENTAL
	public void setScale(float zoom) {
		sc = zoom;
//		System.out.println("\nZoom: " + zoom);
//		System.out.println("New sc: " + sc);
//		sc = p.pow(2.0f, zoom); 
	}
	
	public double getScale(){
		return this.sc;
	}
	

	public void zoom(int dir) {
		sc = p.pow(2.0f, getZoom()+dir); 
	}

	public void zoomIn() {
		sc = p.pow(2.0f, getZoom()+1); 
//		System.out.println("New sc: " + sc);
	}  

	public void zoomOut() {
		sc = p.pow(2.0f, getZoom()-1); 
//		System.out.println("New sc: " + sc);
	}

	//	    public function setExtent(extent:MapExtent):void
	//	    public function getExtent():MapExtent

	/*
	    protected function coordinatePosition(centerCoord:Coordinate):MapPosition
	    public function locationsPosition(locations:Array):MapPosition
	    protected function extentPosition(extent:MapExtent):MapPosition
	 */

	//	    public function getCenterZoom():Array

	public AbstractMapProvider getMapProvider() {
		return this.provider;
	}

	public void setMapProvider(AbstractMapProvider provider) {
		if (this.provider.getClass() != provider.getClass()) {
			this.provider = provider;
			
			this.TILE_WIDTH  = provider.tileWidth();
			this.TILE_HEIGHT = provider.tileHeight();
			
			this.clearGLTextureObjects();
			
			images.clear();
			queue.clear();
			pending.clear();
		}
	}
	
	private void clearGLTextureObjects(){
		synchronized (images) {
			//Delete gl texture
			for (Iterator iter = images.values().iterator(); iter.hasNext();) {
				GLTexture texture = (GLTexture) iter.next();

				if (texture != null && texture.getTextureID() != 0){
					if (activeOglTextureIDs.contains(texture.getTextureID())) {
						activeOglTextureIDs.remove(new Integer(texture.getTextureID()));	
					}
					texture.destroy();
				}
			}
		}
	}

	public Point2f locationPoint(Location location) {
		PMatrix2D m = new PMatrix2D();
		m.translate(width/2, height/2);
		m.scale((float)sc);
		m.translate((float)tx, (float)ty);

		Coordinate coord = provider.locationCoordinate(location).zoomTo(0);
		float[] out = new float[2];
		m.mult(new float[] {coord.column * TILE_WIDTH, coord.row * TILE_HEIGHT}
				, out);

		return new Point2f(out[0], out[1]);
	}

	public Location pointLocation(Point2f point) {
		return pointLocation(point.x, point.y); 
	}

	public Location pointLocation(float x, float y) {
		// TODO: create this matrix once and keep it around for drawing and projecting
		PMatrix2D m = new PMatrix2D();
		m.translate(width/2, height/2);
		m.scale((float)sc);
		m.translate((float)tx, (float)ty);

		// find top left and bottom right positions of map in screenspace:
		float tl[] = new float[2];
		
		m.mult(new float[] {0,0}, tl);
		
		float br[] = new float[2];    
		m.mult(new float[] {TILE_WIDTH, TILE_HEIGHT} , br);

		float col = (x - tl[0]) / (br[0] - tl[0]);
		float row = (y - tl[1]) / (br[1] - tl[1]);
		Coordinate coord = new Coordinate(row, col, 0);

		return provider.coordinateLocation(coord);    
	}

	// TODO: pan by proportion of screen size, not by coordinate grid
	public void panUp() {
		setCenter(getCenterCoordinate().up());
	}
	public void panDown() {
		setCenter(getCenterCoordinate().down());
	}
	public void panLeft() {
		setCenter(getCenterCoordinate().left());
	}
	public void panRight() {
		setCenter(getCenterCoordinate().right());
	}

	public void panAndZoomIn(Location location) {
		// TODO: animate
		setCenterZoom(location, getZoom() + 1);
	}

	public void panTo(Location location) {
		// TODO: animate
		setCenter(location);
	}
	
	public void mouseDragged() {
		double dx = (double)(p.mouseX - p.pmouseX) / sc;
		double dy = (double)(p.mouseY - p.pmouseY) / sc;
		//    float angle = radians(-a);
		//    float rx = cos(angle)*dx - sin(angle)*dy;
		//    float ry = sin(angle)*dx + cos(angle)*dy;
		//    tx += rx;
		//    ty += ry;
		tx += dx;
		ty += dy;
	}
	
	public void move(float x, float y){
		double dx = (double)x / sc;
		double dy = (double)y / sc;
		tx += dx;
		ty += dy;
	}
	
	public ArrayList<Integer> getActiveOglTextureIDs() {
		return activeOglTextureIDs;
	}

	public Hashtable getImages() {
		return images;
	}

	public Vector getRecentImages() {
		return recentImages;
	}
	
	
	/*
	  public float screenX(float x, float y, float z) {
		  	model.clear();
			gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model);
			proj.clear();
			gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, proj);
			view.clear();
			gl.glGetIntegerv(GL.GL_VIEWPORT, view);
		  
		    float ax =
		      modelview.m00*x + modelview.m01*y + modelview.m02*z + modelview.m03;
		    float ay =
		      modelview.m10*x + modelview.m11*y + modelview.m12*z + modelview.m13;
		    float az =
		      modelview.m20*x + modelview.m21*y + modelview.m22*z + modelview.m23;
		    float aw =
		      modelview.m30*x + modelview.m31*y + modelview.m32*z + modelview.m33;

		    float ox =
		      projection.m00*ax + projection.m01*ay +
		      projection.m02*az + projection.m03*aw;
		    float ow =
		      projection.m30*ax + projection.m31*ay +
		      projection.m32*az + projection.m33*aw;

		    if (ow != 0) ox /= ow;
		    return p.width * (1 + ox) / 2.0f;
		  }


		  public float screenY(float x, float y, float z) {
		    float ax =
		      modelview.m00*x + modelview.m01*y + modelview.m02*z + modelview.m03;
		    float ay =
		      modelview.m10*x + modelview.m11*y + modelview.m12*z + modelview.m13;
		    float az =
		      modelview.m20*x + modelview.m21*y + modelview.m22*z + modelview.m23;
		    float aw =
		      modelview.m30*x + modelview.m31*y + modelview.m32*z + modelview.m33;

		    float oy =
		      projection.m10*ax + projection.m11*ay +
		      projection.m12*az + projection.m13*aw;
		    float ow =
		      projection.m30*ax + projection.m31*ay +
		      projection.m32*az + projection.m33*aw;

		    if (ow != 0) oy /= ow;
		    return p.height * (1 + oy) / 2.0f;
		  }
	 */

}

