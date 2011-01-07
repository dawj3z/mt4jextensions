package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PGraphics;

public class MTCircularMenu extends MTEllipse {
	public static final MTCircularMenuBehaviour BEHAVIOUR_DEFAULT = new MTCircularMenuBehaviour();
	
	private PApplet pApplet;
	
	private int itemCounter = 0;
	
	private Hashtable<Integer, AbstractShape> idAndItem = new Hashtable<Integer, AbstractShape>();
	private ArrayList<MTCircularMenuItemWrapper> items = new ArrayList<MTCircularMenuItemWrapper>();
	/**
	 * contains the itemId and the segment, the item is contained in
	 */
	private Hashtable<Integer, MTCircularMenuSegment> itemIdAndSegment = new Hashtable<Integer, MTCircularMenuSegment>();

	private Hashtable<Integer, MTColor> itemIdAndSegmentFillColor = new Hashtable<Integer, MTColor>();
	private Hashtable<Integer, ActionListener> itemIdAndActionListener = new Hashtable<Integer, ActionListener>();
	
	private Hashtable<Integer, ArrayList<ActionListener>> itemIdAndActionListeners = new Hashtable<Integer, ArrayList<ActionListener>>();
	private boolean dirty;

	//TODO:
	//	private Hashtable<AbstractShape, ArrayList<MTCircularMenuSegment>> itemAndChildSegment = new Hashtable<AbstractShape, ArrayList<MTCircularMenuSegment>>();
	
	private MTCircularMenuBehaviour behavior = BEHAVIOUR_DEFAULT;
	
	private float innerRadius;
	private IFont font;
	
	private MTColor defaultFillColor = new MTColor(0,0,0,32);
	private MTColor defaultStrokeColor = new MTColor(0,0,0,32);
	private float defaultStrokeWeight = 1.5f;
	
	private MTColor defaultSegmentFillColor = new MTColor(0,0,0, 128);
	private MTColor defaultSegmentStrokeColor = new MTColor(0,0,0);
	
	public MTCircularMenu(PApplet pApplet, float innerRadius){
		super(pApplet, new Vector3D(0,0), innerRadius, innerRadius);
		this.pApplet = pApplet;
		this.innerRadius = innerRadius;
		
		this.setComposite(false);
		this.removeAllGestureEventListeners();
		
		this.setFillColor(defaultFillColor);
		this.setStrokeColor(defaultStrokeColor);
		this.setStrokeWeight(this.defaultStrokeWeight);
		
		MTColor white = new MTColor(255,255,255);
		this.font = FontManager.getInstance().createFont(this.pApplet, "arial", 
				18,
				white,
				white);
		
//		MTEllipse innerEllipse = new MTEllipse(pApplet, this.getCenterPointGlobal(), innerRadius, innerRadius);
//		innerEllipse.setFillColor(ellipseColor);
//		innerEllipse.setStrokeColor(ellipseColor);
//		innerEllipse.setPickable(false);
//		this.addChild(innerEllipse);
//		innerEllipse.setPositionGlobal(this.getCenterPointGlobal());
//      minRadiusDelta: Es kann ein RadiusDelta gesetzt werden, den die Anzeige braucht
	}
	/**
	 * @param The Text to be displayed in the new menu item
	 * @return The menu item, which is required as key to access the menu segments.
	 */
	public int addItem(String itemText) {
		MTTextArea item = createText(itemText, this.font);
		item.setName("Textarea '" +itemText+ "'");
		return this.addItem(item);
	}
	public int addItem(AbstractShape item) {
		int id = this.createItemId();
		this.idAndItem.put(id,item);
		this.items.add(new MTCircularMenuItemWrapper(item, id));
		this.setDirty(true);
		return id;
	}
	public void setSegmentColor(int elementId, MTColor fillColor){
		this.itemIdAndSegmentFillColor.put(elementId, fillColor);
		this.setDirty(true);
	}
	public void setDefaultSegmentColor(MTColor fillColor){
		this.defaultFillColor = fillColor;
		this.setDirty(true);
	}
	private synchronized int createItemId(){
		return this.itemCounter++;
	}
	public void createMenu(float innerRadius, float outerRadius){
		this.removeAndDestroyChildrenRecursively(this);
		this.itemIdAndSegment.clear();
		
		AbstractShape[] allItems = this.getItems();
		float degreeInc = (float)(360.0d/(double)allItems.length);
		
		for (int i = 0; i < allItems.length; i++) {
			float orientationAngleDegrees;
			if(allItems.length==1){
				orientationAngleDegrees = degreeInc*i-0.5f*degreeInc;
			}else if(allItems.length%2 == 0){
				orientationAngleDegrees = degreeInc*i;
			}else{
				System.out.println("ELSE");
				orientationAngleDegrees = (degreeInc*i)-(degreeInc*0.25f);
			}
			MTCircularMenuSegment segment = new MTCircularMenuSegment(this, innerRadius, outerRadius, degreeInc, orientationAngleDegrees);
			segment.setStrokeWeight(2f);

			AbstractShape currentItem = allItems[i];
			currentItem.setPositionGlobal(segment.getCenterPointGlobal());
			segment.addChild(currentItem);
			this.addChild(segment);
			segment.setName("Segment " + (i+1));
			currentItem.setName("Item " + (i+1));
			
			// set behaviour
			for (int j = 0; j < this.getBehavior().getSegmentSelectionActions().length; j++) {
				segment.addGestureListener(this.getBehavior().getSegmentSelectionProcessor(), this.getBehavior().getSegmentSelectionActions()[j]);
			}
			// action listeners for current segment
			ActionListener[] als = this.getActionListeners(this.getItemId(allItems[i]));
			System.out.println("#ActionListeners " + currentItem.getName() + ": " + als.length);
			TriggerAction[] triggerActions = this.getBehavior().createTriggerActions(segment);
			
			for (int j = 0; j < triggerActions.length; j++) {
				segment.addGestureListener(this.behavior.getTriggerProcessor(), triggerActions[j]);
				// remove and reset actionlisteners to trigger actions
				if(als!=null){
					for (int k = 0; k < als.length; k++) {
						triggerActions[j].addActionListener(als[k]);
						System.out.println("Added ActionListener to TriggerAction for " + currentItem.getName());
					}
				}
				ActionListener[] actionListeners = this.getActionListeners(getItemId(currentItem));
				for (int k = 0; k < actionListeners.length; k++) {
					triggerActions[j].addActionListener(actionListeners[k]);
				}
			}

			int itemId = getItemId(allItems[i]);
			MTColor fillColor = this.itemIdAndSegmentFillColor.get(itemId);
			if(fillColor!=null){
				segment.setFillColor(fillColor);
			}else{
				segment.setFillColor(this.defaultSegmentFillColor);
				segment.setStrokeColor(this.defaultSegmentStrokeColor);
			}
			this.itemIdAndSegment.put(itemId, segment);
		}
		this.setDirty(false);
	}
	private AbstractShape getItem(int id){
		for (int i = 0; i < this.items.size(); i++) {
			MTCircularMenuItemWrapper current = items.get(i);
			if(current!=null && current.getId()==id){
				return current.getWrapped();
			}
		}
		return null;
	}
	Integer getItemId(AbstractShape item){
		for (int i = 0; i < this.items.size(); i++) {
			MTCircularMenuItemWrapper current = items.get(i);
			if(current!=null && current.getWrapped().equals(item)){
				return current.getId();
			}
		}
		return null;
	}
	public boolean hasChildren(MTCircularMenuSegment segment){
		System.out.println("TODO: implement: public void hasChildren(MTEllipseSegment segment)");
		return false;
	}
	private void removeAndDestroyChildrenRecursively(MTComponent rootComponent){
		MTComponent[] children = rootComponent.getChildren();
		for (int i = 0; i < children.length; i++) {
			removeAndDestroyChildrenRecursively(children[i]);
			this.removeChild(children[i]);
			children[i].destroy();
		}
	}
	private MTCircularMenuSegment getCurrentSegment(int itemId){
		return this.itemIdAndSegment.get(itemId);
	}
	private AbstractShape[] getItems(){
		ArrayList<AbstractShape> result = new ArrayList<AbstractShape>();
		for (int i = 0; i < items.size(); i++) {
			result.add(items.get(i).getWrapped());
		}
		return result.toArray(new AbstractShape[result.size()]);
	}
	private MTTextArea createText(String text, IFont font){
		text = text.trim();
		MTTextArea textField = new MTTextArea(this.pApplet, font);
		textField.setComposite(true);
		textField.setNoStroke(true);
		textField.setNoFill(true);
		textField.setText(text);
		return textField;
	}
	protected PApplet getPApplet(){
		return this.pApplet;
	}
	public MTCircularMenuBehaviour getBehavior() {
		return behavior;
	}
	public IFont getFont() {
		return font;
	}
	public void setFont(IFont font) {
		this.font = font;
	}
	public void addActionListener(int  id, ActionListener al){
		
		ArrayList<ActionListener> list = this.itemIdAndActionListeners.get(id);
		if(list==null){
			list = new ArrayList<ActionListener>();
		}
		if(!list.contains(al)){
			list.add(al);
			System.out.println("ActionListener " + al + " added to " + this.getItem(id).getName());
			this.itemIdAndActionListeners.put(id, list);
		}
	}
	public ActionListener[] getActionListeners(int id){
		ArrayList<ActionListener> list = this.itemIdAndActionListeners.get(id);
		System.out.println("menu.getActionListeners called for key item: " + this.getItem(id).getName());
		if(list==null){
			list = new ArrayList<ActionListener>();
		}
		System.out.println("#ActionListeners for item " + this.getItem(id).getName() + ":" + list.size());
		return list.toArray(new ActionListener[list.size()]);
	}
	
//	public void performInEffect(){
//		//TODO: Externalize implementation to behaviour class
//		new Animation("in animation", new MultiPurposeInterpolator(1000f, getCenterPointGlobal().z, 500, 0, 1, 1), this).addAnimationListener(new IAnimationListener(){
//        	private Vector3D centerPointTarget;
//        	public void processAnimationEvent(AnimationEvent ae) {
//        		centerPointTarget = getCenterPointGlobal();
//        		float currentZ = ae.getCurrentValue();
//        		setPositionGlobal(new Vector3D(centerPointTarget.x,centerPointTarget.y,currentZ));
//        	}}).start();
//	}
//	public void performOutEffect(){
//		//TODO: Externalize implementation to behaviour class
//        new Animation("out animation", new MultiPurposeInterpolator(getCenterPointGlobal().z, 1000f , 300, 0, 1, 1), this).addAnimationListener(new IAnimationListener(){
//        	private Vector3D centerPointTarget;
//        	public void processAnimationEvent(AnimationEvent ae) {
//        		centerPointTarget = getCenterPointGlobal();
//        		float currentZ = ae.getCurrentValue();
//        		setPositionGlobal(new Vector3D(centerPointTarget.x,centerPointTarget.y,currentZ));
//        	}}).start();
//	}
	@Override
	public void preDraw(PGraphics graphics) {
		// create segments if something was added
		if(this.isDirty()){
			createMenu(innerRadius, 150);
			System.out.println("isDirty==true->Menu (re)created.");
		}
		super.preDraw(graphics);
	}
	/**
	 * This method is called if the menu structure is changed, e.g. if an item is added.
	 * @param dirty
	 */
	private synchronized void setDirty(boolean dirty){
		this.dirty = dirty;
	}
	private synchronized boolean isDirty(){
		return this.dirty;
	}
}
