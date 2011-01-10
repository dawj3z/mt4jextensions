package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;

import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.util.MTColor;

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
		new Exception().printStackTrace();
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
