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

import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.util.MTColor;

/**
 * @author Uwe Laufs
 *
 */
public class CircularMenuSegmentHandle {
	private long id;
	private MTColor fillColor = new MTColor(0,0,0);
	private MTColor strokeColor = new MTColor(255,255,255);
	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private AbstractShape containedItem;
	private float strokeWeight = 2f;
	
	CircularMenuSegmentHandle(long id){
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public MTColor getFillColor() {
		return this.fillColor;
	}
	public void setFillColor(MTColor fillColor) {
		System.out.println("setFillColor("+fillColor+")");
		this.fillColor = fillColor;
	}
	public MTColor getStrokeColor() {
		return this.strokeColor;
	}
	public void setStrokeColor(MTColor strokeColor) {
		this.strokeColor = strokeColor;
	}
	public void addActionListener(ActionListener al){
		if(!this.actionListeners.contains(al)){
			this.actionListeners.add(al);
		}
	}
	
	public float getStrokeWeight() {
		return strokeWeight;
	}
	public void setStrokeWeight(float strokeWeight) {
		this.strokeWeight = strokeWeight;
	}
	public ActionListener[] getActionListeners(){
		return this.actionListeners.toArray(new ActionListener[this.actionListeners.size()]);
	}
	public AbstractShape getContainedItem() {
		return containedItem;
	}
	public void setContainedItem(AbstractShape containedItem) {
		this.containedItem = containedItem;
	}
}
