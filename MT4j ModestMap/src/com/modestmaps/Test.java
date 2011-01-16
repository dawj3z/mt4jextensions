package com.modestmaps;



import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.mt4j.util.logging.JavaLogger;
import org.mt4j.util.logging.MTLoggerFactory;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

import com.modestmaps.core.Point2f;
import com.modestmaps.geo.Location;
import com.modestmaps.providers.BlueMarble;
import com.modestmaps.providers.Microsoft;
import com.modestmaps.providers.OpenAerialMap;

public class Test extends PApplet {
	
//	private InteractiveMap map;
	private TestInteractiveMap map;
	
	private TestInteractiveMap map2;
	
	private InteractiveMap orgMap;
	
	private StaticMap staticMap;
	
	private Location stuttgartAirPortLoc ;
	private Location stuttgartLoc;
	

	public static void main(String[] args) {
		//Initializes the application
		PApplet.main(new String[] { 
				   "--display=1",
				   "com.modestmaps.Test" }); 
	}
	
	boolean openGL = true;
	
	
	//TODO yahoo maps funktioniert nicht. png issue? 
	//TODO viewport overlays machen - wie zoom und position von overlay mit orginal abgleichen?
	//TODO in multitouch framework integrieren mit gesten und smooth scale
	
	@Override
	public void setup(){
		MTLoggerFactory.setLoggerProvider(new JavaLogger());
		//TOGGLES ALWAYS ON TOP MODE
		//this.frame.setAlwaysOnTop(true);
		int width = 600;
		int height = 400;
		// Applet size - size() must be the first command in setup() method
		if (openGL)
			size(width, height, PApplet.OPENGL); 
		else
			size(width, height, PApplet.JAVA2D); 
		
		//hint(ENABLE_OPENGL_4X_SMOOTH); // ENABLES OPENGL EXTRA SMOOTHING -> DOESENT GET CONSISTENT RESULTS ON ALL MACHINES! DISABLE WHEN PROBLEMS OCCUR!
		//hint(ENABLE_DEPTH_SORT); // Enable primitive z-sorting of triangles and lines in P3D and OPENGL. This can slow performance considerably, and the algorithm is not yet perfect.
		//hint(DISABLE_ERROR_REPORT); // Speeds up the OPENGL renderer setting by not checking for errors while running.
		//hint(ENABLE_ACCURATE_TEXTURES); //Enables better texture accuracy for the P3D renderer. This option will do a better job of dealing with textures in perspective.  
		
		//Set background color
		background(60);
		
		//Set the framerate
		frameRate(30);

		stuttgartAirPortLoc = new Location(	48.689878f, 	9.221964f);
		stuttgartLoc = new Location( 48.7771056f, 	9.1807688f);
		
//		boolean quiet = false;
//
//		if (!quiet) {
//			println();
//			println("yahoo test");
//			println();  
//		}
//		boolean passed = true;
//		AbstractMapProvider p = new Yahoo.RoadProvider();
//		String[] urls = p.getTileUrls(new Coordinate(25322, 10507, 16));
//		println(urls);
//		passed = passed && urls[0].startsWith("http://us.maps2.yimg.com/us.png.maps.yimg.com/png?v=");
//		passed = passed && urls[0].endsWith("&t=m&x=10507&y=7445&z=2");
//		if (!quiet) 
//			println("1: " + passed);
		

		if (openGL){
			map = new TestInteractiveMap(this, new Microsoft.HybridProvider(), width, height);
			
//			map = new TestInteractiveMap(this, new Microsoft.RoadProvider(), width, height);
			
//			map = new TestInteractiveMap(this, new OpenAerialMap(), width, height);
			
//			map = new TestInteractiveMap(this, new BlueMarble(), width, height);
			
//			map = new TestInteractiveMap(this, new OpenStreetMaps(), width, height);

//			map = new TestInteractiveMap(this, new CloudMade.Fresh(), width, height);

			//Overlay test
//			map = new TestInteractiveMap(this, new Microsoft.AerialProvider(), width, height);
//			map2 = new TestInteractiveMap(this, new Microsoft.RoadProvider(), width, height);
			
			//Yahoo, not working
//			map = new TestInteractiveMap(this, new Yahoo.AerialProvider(), width, height);
//			map = new TestInteractiveMap(this, new Yahoo.HybridProvider(), width, height);
			
//			map = new TestInteractiveMap(this, new DailyPlanet(), width, height);
		}else{
			orgMap = new InteractiveMap(this, new Microsoft.HybridProvider(), width, height);
//			orgMap = new InteractiveMap(this, new Yahoo.AerialProvider(), width, height);
		}

//		map = new InteractiveMap(this, new Yahoo.HybridProvider());


		/*
		if (openGL){
			  map.zoomIn();
				 map.zoomIn();
				 map.zoomIn();
				 map.zoomIn();
				 map.panRight();
				 map.panUp();
				 map.panUp();
				 map.panUp();
				 map.panUp();
				 map.panUp();
				 map.panUp();
				 map.panUp();
				 map.panUp();
				 map.panUp();
				 map.panUp();
				 map.panRight();
				 map.zoomIn();
				 map.zoomIn();
				 map.zoomIn(); 
				 
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 map.panDown();
				 
				 map.panLeft();
				 map.zoomIn();
				 map.zoomIn();
				 map.panDown();
				 map.panLeft();
				 map.zoomIn();
				 
				
				 map.zoomIn();
				 map.zoomIn();
				 map.zoomIn();
				 map.zoomIn();
		}else{
		
		
		}
		*/
		
		if (openGL){
			System.out.println("Zoom: " + map.getZoom());
//			zoomFloat = map.getZoom();
		}else{
			System.out.println("Zoom: " + orgMap.getZoom());
//			zoomFloat = orgMap.getZoom();
		}
		

		registerKeyEvent(this);
		
		/*
		Vector3D hit = map.getIntersectionPoint(new Ray(new Vector3D(100,100,100), new Vector3D(0,0,-10)));
		boolean c = map.containsPoint(new Vector3D(100,245,0));
		System.out.println("HIT:" + hit);
		System.out.println("Contains: " + c);
		*/
		
		map.sc = 1;
	}


	/**********************************************************************************************
	 * Processings draw() gets called repeatedly by processings PApplet Class - unless noloop() is called
	 **********************************************************************************************/
	@Override
	public void draw(){
		background(60);

		if (openGL){
			PGraphicsOpenGL pgl = ((PGraphicsOpenGL)g);
			GL gl = pgl.beginGL();
			GLU glu = pgl.glu;
			
			
			gl.glPushMatrix();
			gl.glViewport(0, 0, width, height);

			// rotate the map:
			/*
			gl.glTranslatef(width/2, height/2, 0);
			gl.glRotatef(90, 0, 0, 1); //(float)Math.toRadians(180)
			gl.glTranslatef(-width/2, -height/2, 0);
			*/
			
			//Draw map
			map.drawPureGL(gl, glu);  // (that's it! really... everything else is interactions now)

			gl.glPopMatrix();
			pgl.endGL();
			
			
			/*
			gl = pgl.beginGL();
			gl.glPushMatrix();
			gl.glTranslatef(0, 0, 0.5f);
			gl.glViewport(0, 0, 256, 256);
//			gl.glViewport(0, 0, 900, 900);
			map2.draw(gl, glu);
			gl.glPopMatrix();
			pgl.endGL();
			*/
			
			if (mousePressed){
				map.mouseDragged();
//				map2.mouseDragged();
			}
		}else{
			if (mousePressed){
				orgMap.mouseDragged();
			}
			orgMap.draw();
		}
		
		//UR tile bounds unrotated
		//Projected (0,0,0) => MinXY:(171.99998, 72.0) (256, 256, 0) => MaxXY:(428.0, 328.0)
//		pushMatrix();
//		translate(172,72);
//		box(10);
//		popMatrix();
//		pushMatrix();
//		translate(428.0f, 328.0f);
//		box(10);
//		popMatrix();

		//UR tile bounds rotated
		//Projected (0,0,0) => MinXY:(428.0, 72.0) (256, 256, 0) => MaxXY:(171.99998, 328.0)
		pushMatrix();
		translate(428.0f, 72.0f);
		box(10);
		popMatrix();
		pushMatrix();
		translate(171.99998f, 328.0f);
		box(10);
		popMatrix();
		
//		staticMap.draw();
//		line(100, 100, 300, 300);
	}

	float zoomFloat = 128;
	int zoomInt = 1;
	public void keyEvent(KeyEvent e){
		//System.out.println(e.getKeyCode());
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;
		
		switch (e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			if (openGL)
				map.panLeft();
			else
				orgMap.panLeft();
			break;
		case KeyEvent.VK_UP:
			if (openGL)
				map.panUp();
			else
				orgMap.panUp();
			break;
		case KeyEvent.VK_RIGHT:
			if (openGL)
				map.panRight();
			else
				orgMap.panRight();
			break;
		case KeyEvent.VK_DOWN:
			if (openGL)
				map.panDown();
			else
				orgMap.panDown();
			break;
		case KeyEvent.VK_PLUS:
//			zoomFloat += 0.5f;
			zoomInt++;
			
//			zoomFloat += pow(2.0f, 0.1f);
			
//			float bla = pow(2.0f, (float)(map.getZoom() ));
			float bla = pow(2.0f, (float)(map.getZoom()));
			
			float delta = bla/150f;
			System.out.println("scale + " + delta);
			zoomFloat += delta;
			
			
//			zoomFloat += pow(2.0f, 0.1f);
			
			if (openGL){
				map.zoomIn();
//				map.setScale(zoomFloat);
//				map.setZoom(zoomInt);
			}
			else{
				orgMap.zoomIn();
//				orgMap.setScale(zoomFloat);
//				orgMap.setZoom(zoomInt);
			}
			break;
		case KeyEvent.VK_MINUS:
//			zoomFloat -= 0.5f;
			zoomInt--;
			
			float bla2 = pow(2.0f, (float)(map.getZoom()));
			
			float delta2 = bla2/150f;
			System.out.println("scale + " + delta2);
			zoomFloat -= delta2;
			
			if (openGL){
				map.zoomOut();
//				map.setScale(zoomFloat);
//				map.setZoom(zoomInt);
			}
			else{
				orgMap.zoomOut();
//				orgMap.setScale(zoomFloat);
//				orgMap.setZoom(zoomInt);
			}
			break;
		case KeyEvent.VK_T:
			System.out.println("Active images: " + map.getImages().size());
			System.out.println("Recent images: " + map.getRecentImages().size());
			System.out.println("Active loaded OpenGL textures: " + map.getActiveOglTextureIDs().size());
			break;
		case KeyEvent.VK_F12:
			saveFrame();
		    break;
		case KeyEvent.VK_A:
			if (openGL){
				map.setMapProvider( new Microsoft.AerialProvider());
			}else{
				orgMap.setMapProvider( new Microsoft.AerialProvider());
			}
		    break;
		case KeyEvent.VK_R:
			if (openGL){
				map.setMapProvider( new Microsoft.RoadProvider());
			}else{
				orgMap.setMapProvider( new Microsoft.RoadProvider());
			}
		    break;
		case KeyEvent.VK_H:
			if (openGL){
				map.setMapProvider( new Microsoft.HybridProvider());
			}else{
				orgMap.setMapProvider( new Microsoft.HybridProvider());
			}
		    break;
		case KeyEvent.VK_O:
			if (openGL){
				map.setMapProvider( new OpenAerialMap());
			}else{
				orgMap.setMapProvider( new OpenAerialMap());
			}
		    break;
		case KeyEvent.VK_B:
			if (openGL){
				map.setMapProvider( new BlueMarble());
			}else{
				orgMap.setMapProvider( new BlueMarble());
			}
		    break;
		case KeyEvent.VK_C:
			if (openGL){
				map.sc *= 1.025;
			}else{
				orgMap.sc *= 1.025;
			}
		    break;
		case KeyEvent.VK_V:
			if (openGL){
				map.sc *= 1.0/1.025;
			}else{
				orgMap.sc *= 1.0/1.025;
			}
		    break;
		case KeyEvent.VK_X:
			map.move(5,5);
		    break;
		case KeyEvent.VK_F1:
//			map.setCenter(stuttgartAirPortLoc);
			
//			map.setCenterZoom(stuttgartAirPortLoc, 10);
			map.setCenterZoom(stuttgartLoc, 13);
			System.out.println("Center set to location: " + stuttgartLoc);
		    break;
		case KeyEvent.VK_F2:
			Location center = map.getCenter();
			System.out.println("Center location: " + center);
			map.setCenter(center);
		    break;
		case KeyEvent.VK_F3:
			Location c = map.getCenter();
			Point2f p = map.locationPoint(c);
			System.out.println("Center location: " + c + " -> Point on screen: X:" + p.x + " Y:" + p.y);
		    break;
		default:
			break;
		}
		
	/*
		   if (keyPressed) {
    if (key == CODED) {
      if (keyCode == LEFT) {
        map.tx += 5.0/map.sc;
      }
      else if (keyCode == RIGHT) {
        map.tx -= 5.0/map.sc;
      }
      else if (keyCode == UP) {
        map.ty += 5.0/map.sc;
      }
      else if (keyCode == DOWN) {
        map.ty -= 5.0/map.sc;
      }
    }  
    else if (key == '+' || key == '=') {
      map.sc *= 1.05;
    }
    else if (key == '_' || key == '-' && map.sc > 2) {
      map.sc *= 1.0/1.05;
    }
  	}

	 */
	}
	
	
	/**
	  map.zoomIn();
//		 map.zoomIn();
//		 map.zoomIn();
//		 map.zoomIn();
////		 map.panRight();
////		 map.panUp();
////		 map.panUp();
////		 map.panUp();
////		 map.panUp();
////		 map.panUp();
////		 map.panUp();
////		 map.panUp();
////		 map.panUp();
////		 map.panUp();
//		 map.panUp();
//		 map.panUp();
//		 map.panUp();
//		 map.panUp();
//		 map.panUp();
////		 map.panUp();
//		 map.panRight();
////		 map.panRight();
//		 map.zoomIn();
//		 map.zoomIn();
//		 map.zoomIn(); 
//		 map.panLeft();
////		 map.panLeft();
////		 map.panLeft();
//		 map.zoomIn();
//		 map.zoomIn();
//		 map.panDown();
////		 map.panDown();
//		 map.panLeft();
//		 map.zoomIn();
//		 map.zoomIn();
////		 map.zoomIn();
////		 map.panUp();
////		 map.panLeft();
////		 map.zoomIn();
////		 map.panLeft();
////		 map.zoomIn();
////		 map.zoomIn();
////		 map.panDown();
////		 map.zoomIn();
////		 map.panDown();
////		 map.panLeft();
////		 map.panLeft();
////		 map.zoomIn();
 */
	
	
}
