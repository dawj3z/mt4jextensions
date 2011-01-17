package org.mt4jx.components.visibleComponents.widgets.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * @author Alexander Phleps
 *
 */
public class MTToolbarButton extends MTRectangle implements IclickableButton {
	private ArrayList<ActionListener> registeredActionListeners;
	private MTLayoutContainer listMenu = null;
	
	
	
	public MTLayoutContainer getListMenu() {
		return listMenu;
	}

	public MTToolbarButton(PImage texture, PApplet applet) {
		super(applet, texture);
		
		this.registeredActionListeners = new ArrayList<ActionListener>();
		setNoStroke(true);

		//Make clickable
		this.setGestureAllowance(TapProcessor.class, true);
		this.registerInputProcessor(new TapProcessor(applet));
		this.addGestureListener(TapProcessor.class, new MTToolbarButtonClickAction(this));
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected IBoundingShape computeDefaultBounds(){
		return new BoundsZPlaneRectangle(this);
	}
		
	/**
	 * Adds the action listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void addActionListener(ActionListener listener){
		if (!registeredActionListeners.contains(listener)){
			registeredActionListeners.add(listener);
		}
	}
	
	/**
	 * Removes the action listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void removeActionListener(ActionListener listener){
		if (registeredActionListeners.contains(listener)){
			registeredActionListeners.remove(listener);
		}
	}
	
	/**
	 * Gets the action listeners.
	 * 
	 * @return the action listeners
	 */
	public synchronized ActionListener[] getActionListeners(){
		return (ActionListener[])registeredActionListeners.toArray(new ActionListener[this.registeredActionListeners.size()]);
	}
	
	/**
	 * Fire action performed.
	 */
	protected void fireActionPerformed() {
		ActionListener[] listeners = this.getActionListeners();
		synchronized(listeners) {
			for (int i = 0; i < listeners.length; i++) {
				ActionListener listener = (ActionListener)listeners[i];
				listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "action performed on tangible button"));
				
			}
		}
	}
	
	/**
	 * fires an action event with a ClickEvent Id as its ID.
	 * 
	 * @param ce the ce
	 */
	public void fireActionPerformed(TapEvent ce) {
		ActionListener[] listeners = this.getActionListeners();
		if(ce.isTapped()){
			synchronized(listeners) {
				for (int i = 0; i < listeners.length; i++) {
					ActionListener listener = (ActionListener)listeners[i];
					listener.actionPerformed(new ActionEvent(this, ce.getTapID(),  "action performed on tangible button"));
				}
			}
		}
	}
	
	
	
	

	
	
	//processing level ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
	
	/**
	 * Draws border, color and transparency on processing level.
	 */
	public void drawComponent(processing.core.PGraphics g) {
		super.drawComponent(g);
//		parent.fill(parent.GRAY, 100);
//		parent.strokeWeight(borderSize);
//		parent.stroke(255, 255, 255, 100);
////		parent.strokeJoin(parent.BEVEL); //strokeJoin() is not available with this renderer.
//		parent.rectMode(parent.CORNER);
//		//parent.rect(-borderSize, -borderSize, buttonCount * size + borderSize * 2, size + borderSize * 2);
//		parent.rect(0, 0, this.getWidthXY(TransformSpace.LOCAL), this.getHeightXY(TransformSpace.LOCAL));
		
		

		
		
		
	}

	public void addSubmenu(final MTLayoutContainer listMenu) {
		// TODO Auto-generated method stub
		this.listMenu = listMenu;
		listMenu.setVisible(false);
		
		
		MTLayoutContainer parent = (MTLayoutContainer) this.getParent();
		
		listMenu.alignTo(this, parent.getAlign(), parent.getLayout());
		this.addChild(listMenu);
		
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch(event.getID()) {
					case TapEvent.TAPPED:
//						menu.align(MTToolbar.TOP); //just temporary
						System.out.println("click on button with list");
						
						listMenu.toggle();
					break;
				}
			}
        });
		
		
	}

	
}
