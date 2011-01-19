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

import org.mt4j.util.MTColor;
import org.mt4jx.components.visibleComponents.shapes.MTEllipseSegmentComplexPoly;

/**
 * @author Uwe Laufs
 *
 */
public class MTCircularMenuSegment extends MTEllipseSegmentComplexPoly {
	private boolean isDown = false;
	private float scaleDelta = 0.04f;
	
	private MTCircularMenu parentMenu;
	private float innerRadius;
	private float outerRadius;
	private float segmentAngle;
	private float orientationAngleDegrees;
	private static long idCounter =0;
	private long segmentId;
	
	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
	
	public MTCircularMenuSegment(MTCircularMenu parentMenu, float innerRadius, float outerRadius, float segmentAngle, float orientationAngleDegrees){
		super(parentMenu.getPApplet(), innerRadius, outerRadius, segmentAngle, orientationAngleDegrees);
		this.parentMenu = parentMenu;
		this.segmentId = createSegmentId();
		this.innerRadius = innerRadius;
		this.outerRadius = outerRadius;
		this.segmentAngle = segmentAngle;
		this.orientationAngleDegrees = orientationAngleDegrees;
		this.setStrokeColor(new MTColor(255,255,255));
		this.setComposite(true);
		this.removeAllGestureEventListeners();
	}
	public boolean hasChildren(){
		return false; //TODO: implement when children are supported
	}
	public synchronized void segmentUp(){
		if(isDown){
			this.scale(1f*(1f+scaleDelta), 1f*(1f+scaleDelta), 1f, this.getCenterPointLocal());
			this.isDown = false;
		}
	}
	public synchronized void segmentDown(){
		if(!isDown){
			this.scale(1f/(1f+scaleDelta), 1f/(1f+scaleDelta), 1f, this.getCenterPointLocal());
			this.isDown = true;
		}
	}
	public void setRandomFillColor(float alpha){
		MTColor randomColor = new MTColor((int)(255*Math.random()),(int)(255*Math.random()),(int)(255*Math.random()), alpha);
		this.setFillColor(randomColor);
	}
	private synchronized long createSegmentId(){
		return idCounter++;
	}
	public MTCircularMenu getParentMenu() {
		return parentMenu;
	}
	public float getOuterRadius() {
		return outerRadius;
	}
	public float getSegmentAngle() {
		return segmentAngle;
	}
	public long getSegmentId() {
		return segmentId;
	}
	public void addActionListener(ActionListener al){
		this.actionListeners.add(al);
	}
	
//	TODO: IMPLEMENT
//	public Vector3D getRealCenterPointGlobal(){
//		
//	}
//	public Vector3D getCenterOfEllispe(){
//		return 
//	}
	
	// 1. Additem(AbstractVisualComponent) DONE
	// 2. bei add: alle children entfernen, alle children neu erzeugen, AbstractVisualComponents separat merken DONE
	// 3. setItems: setzt alle, spart performance
	//
}
