package org.mt4jext.mttree;

import java.util.HashMap;
import java.util.Map;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.DefaultPanAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.componentProcessors.panProcessor.PanProcessorTwoFingers;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.AbstractGlobalInputProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.IFrustum;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.font.fontFactories.BitmapFontFactory;

import processing.core.PGraphics;

/**
 * @author Ewoud van Pareren
 * 
 * This Scene is designed to hold a Tree instance.
 * It handles most of the GUI interaction and serves as a bridge between
 * a Tree and an MT4j application.
 */
public class TreeScene extends AbstractScene {
	
	private int valid = 0;
	
	
	private MTApplication app;
	private Tree tree;
	
	private Map<MTComponent, Node> mapping;
	
	IFont font;
	
	/**
	 * Get the tree attached to this scene.
	 * 
	 * @return the Tree instance attached to this scene
	 */
	public Tree getTree() {
		return tree;
	}

	/**
	 * To be called from Tree: attach this Tree instance.
	 * 
	 * This method is not for use by any application.
	 * Use Tree.setTreeScene() instead.
	 * 
	 * @param tree
	 */
	protected void setTree(Tree tree) {
		this.tree = tree;
	}

	/**
	 * Constructor.
	 * 
	 * @param mtApplication the MTApplication to use
	 * @param name the name of the Scene
	 */
	public TreeScene(MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		this.setClearColor(MTColor.WHITE);
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		
		// Invalidate on interaction
		this.registerGlobalInputProcessor(new AbstractGlobalInputProcessor() {
			@Override
			public void processInputEvtImpl(MTInputEvent arg0) {
				//if (app instanceof MTInvalidationApplication)
					//((MTInvalidationApplication)app).
				invalidate();
			}
		});
		
		// TODO Auto-generated constructor stub
		this.getCanvas().setFrustumCulling(true);
		
		FontManager.getInstance().registerFontFactory(".ttf", new BitmapFontFactory());
		
		getCanvas().registerInputProcessor(new ZoomProcessor(app));
		getCanvas().addGestureListener(ZoomProcessor.class, new DefaultZoomAction());
		getCanvas().registerInputProcessor(new PanProcessorTwoFingers(app));
		getCanvas().addGestureListener(PanProcessorTwoFingers.class, new DefaultPanAction());
		
		
		font = FontManager.getInstance().createFont(app, "arial.ttf", 12, MTColor.WHITE, MTColor.WHITE, false);
		
		mapping = new HashMap<MTComponent, Node>();
		
	}

	/**
	 * Built-in invalidation system.
	 * 
	 * Typically, this is called on user interaction, after tree updates
	 * and in other situations where redrawing is needed.
	 */
	public void invalidate() {
		valid = 10;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mt4j.sceneManagement.AbstractScene#drawAndUpdate(processing.core.PGraphics, long)
	 */
	public void drawAndUpdate(PGraphics given, long time) {
		if (valid > 0) {
			super.drawAndUpdate(given, time);
			valid--;
		}
	}
	
	/**
	 * Initialize.
	 * 
	 * Currently unused.
	 */
	@Override
	public void init() {
	}

	/**
	 * Shutdown cleanup.
	 * 
	 * Currently unused.
	 */
	@Override
	public void shutDown() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Add a Node to the Scene.
	 * 
	 * Note that this also places the given component-childNode pair
	 * in a hashmap, to allow looking up Nodes via components.
	 * 
	 * Typically called by Tree.
	 * If you want to add a Node to this scene, do it through the Tree.
	 * If you want to add a different kind of component, use addChild(MTComponent). 
	 * 
	 * @param child the Node to add
	 */
	protected void addChild(Node child) {
		MTComponent in = child.getComponent();
		// First remove any previous listeners
		in.unregisterAllInputProcessors();
		in.removeAllGestureEventListeners();
		
		child.setComponentListeners(this);
		
		getCanvas().addChild(in);
		if (child.isChild())
			getCanvas().addChild(child.getEdge().getComponent());
		mapping.put(in, child);
	}
	
	/**
	 * Add a component as child.
	 * 
	 * This is used for non-node components, like lines, GUI and
	 * other things like that.
	 * 
	 * @param in the component to add to the scene
	 */
	public void addChild(MTComponent in) {
		//clearAllGestures(in);
		getCanvas().addChild(in);
	}
	
	/**
	 * Remove the child component from the scene.
	 * 
	 * Used internally. May be used externally to remove some components.
	 * 
	 * Warning: be careful not to remove node components.
	 * That is handled by the Tree system
	 * (remove/hide its corresponding Node and call Tree.update()).
	 * 
	 * @param in the component to remove from the scene
	 */
	public void removeChild(MTComponent in) {
		// For safety, remove all listeners
		in.unregisterAllInputProcessors();
		in.removeAllGestureEventListeners();
		
		this.getCanvas().removeChild(in);
		
		Node check = mapping.get(in);
		if (check != null && check.isChild() && check.getEdge().getComponent() != null) {
			removeChild(check.getEdge().getComponent());
		}
		mapping.remove(in);
	}
	
	/**
	 * Generate a default component.
	 * 
	 * This is typically used to generate a standard Node component.
	 * 
	 * @return a default MTRectangle component
	 */
	public MTRectangle makeDefaultComponent() {

		MTColor textAreaColor = new MTColor(50,50,50,255);
		
		//Add component multi-touch gestures
		MTTextArea returnThis = new MTTextArea(app, font);
		//MTRectangle returnThis = new MTRectangle(0,0,30,30,app);
		returnThis.setFillColor(textAreaColor);
		returnThis.setStrokeColor(textAreaColor);
		returnThis.setText("Node");
		//returnThis.generateAndUseDisplayLists();
		
		
		return returnThis;
	}
	
	/**
	 * Generate a default component with custom text.
	 * 
	 * 
	 * @param in the text to be used
	 * @return a default MTRectangle component (with custom text)
	 */
	public MTRectangle makeDefaultComponent(String in) {

		MTColor textAreaColor = new MTColor(50,50,50,255);
		
		//Add component multi-touch gestures
		MTTextArea returnThis = new MTTextArea(app, font);
		//MTRectangle returnThis = new MTRectangle(0,0,30,30,app);
		returnThis.setFillColor(textAreaColor);
		returnThis.setStrokeColor(textAreaColor);
		returnThis.setText(in);
		//returnThis.generateAndUseDisplayLists();
		
		
		return returnThis;
	}
	
	/**
	 * Construct a default line.
	 * 
	 * This generates a simple MTLine of 0px (aka 1px regardless of transformation).
	 * 
	 * This method is typically used to populate Edge objects with components.
	 * 
	 * Note that the returned Line is customized to prevent frustum culling
	 * when its start and end points are not in view.
	 * 
	 * @return a simple MTLine
	 */
	public MTLine makeDefaultLine() {
		
		MTLine returnThis = new MTLine(app, 0,0,1,1) {
			// Override the frustum culling check
			// Otherwise, lines are not visible if their appropiate nodes aren't
			public boolean isContainedIn(IFrustum frustum) {
				float halfsize = this.getLength()/2;
				
				return (frustum.isSphereInFrustum(getCenterPointGlobal(), halfsize) != IFrustum.OUTSIDE); // Always draw the lines?
			}
		};
		returnThis.setStrokeColor(MTColor.BLACK);
		//returnThis.setUseVBOs(true);
		returnThis.unregisterAllInputProcessors();
		returnThis.removeAllGestureEventListeners();
		returnThis.setPickable(false);
		
		return returnThis;
	}
	
	public void resetCamera() {
		try {
			((MTCamera)getSceneCam()).resetToDefault();
		} catch (ClassCastException err) {
			
		}
	}
	
	
	/**
	 * Remove all node components.
	 * 
	 * This is used upon attaching/detaching Tree instances.
	 */
	protected void removeAllNodeComponents() {
		
		Node[] getAll = mapping.values().toArray(new Node[0]);
		
		for (Node n: getAll) {
			removeChild(n.getComponent());
			if (n.isChild() && n.getEdge().getComponent() != null)
				removeChild(n.getEdge().getComponent());
		}
	}

	/**
	 * Get all nodes in the scene.
	 * 
	 * This is used upon attaching/detaching Tree instances.
	 */
	protected Node[] getAllNodes() {
		Node[] getAll = mapping.values().toArray(new Node[0]);
		
		return getAll;
	}
	
}
