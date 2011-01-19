/***********************************************************************
 *   MT4j Extension: MTCircularMenu
 *   
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License (LGPL)
 *   as published by the Free Software Foundation, either version 3
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the LGPL
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * @author Uwe Laufs
 *
 */
public class MTCircularMenu extends MTEllipse {
	public static final MTCircularMenuBehaviour BEHAVIOUR_DEFAULT = new MTCircularMenuBehaviour();
	
	private PApplet pApplet;
	
	private int segmentHandleIdCounter = 0;
	
	private ArrayList<CircularMenuSegmentHandle> segmentHandles = new ArrayList<CircularMenuSegmentHandle>();
	
	private boolean dirty;

	private MTCircularMenuBehaviour behavior = BEHAVIOUR_DEFAULT;
	
	private float innerRadius,outerRadius;
	private IFont font;
	
	public MTCircularMenu(PApplet pApplet, float innerRadius, float outerRadius){
		super(pApplet, new Vector3D(0,0), outerRadius, outerRadius);
		this.pApplet = pApplet;
		this.innerRadius = innerRadius;
		this.outerRadius = outerRadius;
		
		this.setComposite(false);
		this.removeAllGestureEventListeners();
		// defaults used by segments
		this.setFillColor(new MTColor(0,0,0, 255-32));
		this.setStrokeColor(new MTColor(255,255,255, 255-32));
		this.setStrokeWeight(4f);
		this.setNoFill(true);
		this.setNoStroke(true);
		
		MTColor white = new MTColor(255,255,255);
		this.font = FontManager.getInstance().createFont(this.pApplet, "arial", 18);
		this.font.setFillColor(white);
	}
	/**
	 * @param The Text to be displayed in the new menu item
	 * @return The menu item, which is required as key to access the menu segments.
	 */
	public CircularMenuSegmentHandle createSegment(String itemText) {
		MTTextArea item = createText(itemText, this.font);
		item.setName("Textarea '" +itemText+ "'");
		return this.createSegment(item);
	}
	public CircularMenuSegmentHandle createSegment(PImage icon) {
		MTRectangle rect = new MTRectangle(pApplet, icon);
		rect.setPickable(false);
		rect.setNoStroke(true);
		return this.createSegment(rect);
	}
	public CircularMenuSegmentHandle createSegment(AbstractShape item) {
		CircularMenuSegmentHandle segmentHandle = this.createSegmentHandle();
		// setting menu's colors as default for all segments that are created.
		segmentHandle.setFillColor(this.getFillColor());
		segmentHandle.setStrokeColor(this.getStrokeColor());
		segmentHandle.setStrokeWeight(this.getStrokeWeight());
		segmentHandle.setContainedItem(item);
		this.segmentHandles.add(segmentHandle);
		this.setDirty(true);
		return segmentHandle;
	}
	public void setFillColor(MTColor fillColor){
		super.setFillColor(fillColor);
		this.setDirty(true);
	}
	public void setStrokeColor(MTColor strokeColor){
		super.setStrokeColor(strokeColor);
		this.setDirty(true);
	}
	private synchronized CircularMenuSegmentHandle createSegmentHandle(){
		return new CircularMenuSegmentHandle(this.segmentHandleIdCounter++);
	}
	private void createMenu(float innerRadius, float outerRadius){
		this.recurseRemoveChildren(this);

		CircularMenuSegmentHandle[] segmentHandles = this.getSegmentHandles();
		
		float degreeInc = (float)(360.0d/(double)segmentHandles.length);
		
		for (int i = 0; i < segmentHandles.length; i++) {
			CircularMenuSegmentHandle currentSegmentHandle = segmentHandles[i];
			float orientationAngleDegrees;
			if(segmentHandles.length==1){
				orientationAngleDegrees = degreeInc*i-0.5f*degreeInc;
			}else if(segmentHandles.length%2 == 0){
				orientationAngleDegrees = degreeInc*i;
			}else{
				System.out.println("ELSE");
				orientationAngleDegrees = (degreeInc*i)-(degreeInc*0.25f);
			}
			MTCircularMenuSegment segment = new MTCircularMenuSegment(this, innerRadius, outerRadius, degreeInc, orientationAngleDegrees);
			segment.setStrokeWeight(currentSegmentHandle.getStrokeWeight());

			AbstractShape currentItem = segmentHandles[i].getContainedItem();
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
			ActionListener[] als = segmentHandles[i].getActionListeners();
			System.out.println("#ActionListeners " + currentItem.getName() + ": " + als.length);
			TriggerAction[] triggerActions = this.getBehavior().createTriggerActions(segment);
			
			for (int j = 0; j < triggerActions.length; j++) {
				segment.addGestureListener(this.behavior.getTriggerProcessor(), triggerActions[j]);
				// remove and reset actionlisteners to trigger actions
				if(als!=null){
					for (int k = 0; k < als.length; k++) {
						triggerActions[j].addActionListener(als[k]);
//						System.out.println("Added ActionListener to TriggerAction for " + currentItem.getName());
					}
				}
				ActionListener[] actionListeners = segmentHandles[i].getActionListeners();
				for (int k = 0; k < actionListeners.length; k++) {
					triggerActions[j].addActionListener(actionListeners[k]);
				}
			}
			// set colors from segment handle'S values
			segment.setFillColor(currentSegmentHandle.getFillColor());
			segment.setStrokeColor(currentSegmentHandle.getStrokeColor());
		}
		this.setDirty(false);
	}
	public CircularMenuSegmentHandle[] getSegmentHandles(){
		return this.segmentHandles.toArray(new CircularMenuSegmentHandle[this.segmentHandles.size()]);
	}
	private void recurseRemoveChildren(MTComponent rootComponent){
		MTComponent[] children = rootComponent.getChildren();
		for (int i = 0; i < children.length; i++) {
			recurseRemoveChildren(children[i]);
			this.removeChild(children[i]);
			children[i].destroy();
		}
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
	public void setFont(IFont font) {
		this.font = font;
	}
	public IFont getFont() {
		return font;
	}

	@Override
	public void preDraw(PGraphics graphics) {
		// (re)create segments if anything was added
		if(this.isDirty()){
			// TODO: calc outer radius as required
			createMenu(innerRadius, outerRadius);
//			System.out.println("isDirty==true->Menu (re)created.");
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
