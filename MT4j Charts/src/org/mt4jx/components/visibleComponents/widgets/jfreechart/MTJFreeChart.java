package org.mt4jx.components.visibleComponents.widgets.jfreechart;

import org.jfree.chart.JFreeChart;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import processing.core.PApplet;
import processing.core.PImage;

/***********************************************************************
 * MT4jCharts, created April 2010
 * by Uwe Laufs
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
public class MTJFreeChart extends MTRectangle {
	private JFreeChart jFreeChart;
	private boolean redrawWhenScaled = false;
	private IGestureEventListener scaleAction;

	public MTJFreeChart(float sizeX, float sizeY, PApplet pApplet, JFreeChart jFreeChart){
		super(new PImage(jFreeChart.createBufferedImage(Math.round(sizeX), Math.round(sizeY))), pApplet);
		this.createScaleListener();
		this.setJFreeChart(jFreeChart);
		if(redrawWhenScaled){
			this.addGestureListener(ScaleProcessor.class, this.scaleAction);
		}
	}
	public JFreeChart getJFreeChart() {
		return this.jFreeChart;
	}
	public void setJFreeChart(JFreeChart jFreeChart) {
		this.jFreeChart = jFreeChart;
		redrawChart();
	}
	public boolean isRedrawWhenScaled() {
		return redrawWhenScaled;
	}
	public void setRedrawWhenScaled(boolean redrawWhenScaled) {
		if(this.redrawWhenScaled!=redrawWhenScaled){
			this.redrawWhenScaled = redrawWhenScaled;
			if(redrawWhenScaled){
				this.addGestureListener(ScaleProcessor.class, this.scaleAction);
			}else{
				this.removeGestureEventListener(ScaleProcessor.class, this.scaleAction);
			}
		}
	}
	public void redrawChart(){
		if(this.getJFreeChart()!=null){
			int width = Math.round(getWidthXY(TransformSpace.GLOBAL));
			int height = Math.round(getHeightXY(TransformSpace.GLOBAL));
			setTexture(new PImage(getJFreeChart().createBufferedImage(width, height)));
		}
	}
	private void createScaleListener(){
		this.scaleAction = new IGestureEventListener(){
				@Override
				public boolean processGestureEvent(MTGestureEvent gestureEvent) {
					if(gestureEvent instanceof ScaleEvent){
						ScaleEvent se = (ScaleEvent)gestureEvent;
						switch (se.getId()) {
						case ScaleEvent.GESTURE_ENDED:
							redrawChart();
							break;
						}
					}
					return false;
				}
			};
	}
}
