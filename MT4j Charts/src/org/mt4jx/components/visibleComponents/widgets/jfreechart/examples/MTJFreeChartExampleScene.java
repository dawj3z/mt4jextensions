package org.mt4jx.components.visibleComponents.widgets.jfreechart.examples;

import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.mt4j.MTApplication;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.widgets.jfreechart.MTJFreeChart;

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
public class MTJFreeChartExampleScene extends AbstractScene {

	public MTJFreeChartExampleScene(MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.setClearColor(new MTColor(0, 0, 96, 255));
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));

		// Create Example Data
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        for (int i = 0; i < 10; i++) {
	            dataset.addValue(10*Math.random(), "MySeries", "T"+i);
			}
	    // Create JFreeChart
        JFreeChart chart1 = ChartFactory.createLineChart("Line Chart","x axis","y axis",dataset,PlotOrientation.VERTICAL,true,true,false);
        // Put the JFreeChart into a MTFreeChart
		MTJFreeChart mtChart1 = new MTJFreeChart(800, 600, mtApplication, chart1);
		
		// Create another chart
        DefaultPieDataset pds = new DefaultPieDataset();
	        pds.setValue("Java", new Double(18.051));
	        pds.setValue("C", new Double(18.058));
	        pds.setValue("C++", new Double(9.707));
	        pds.setValue("PHP", new Double(9.662));
	        pds.setValue("(Visual) Basic", new Double(6.392));
	        pds.setValue("C#", new Double(4.435));
	        pds.setValue("Python", new Double(4.205));
	        pds.setValue("Perl", new Double(3.553));
	        pds.setValue("Delphi", new Double(2.715));
	        pds.setValue("JavaScript", new Double(2.469));
	        
        JFreeChart chart2 = ChartFactory.createPieChart3D("Top 10: TIOBE Programming Community Index\nfor April 2010 (www.tiobe.com)", pds, true, true, Locale.GERMANY);
        	PiePlot3D plot = (PiePlot3D) chart2.getPlot();
	        plot.setStartAngle(290);
	        
        MTJFreeChart mtChart2 = new MTJFreeChart(800, 600, mtApplication, chart2);
        // enable redraw of the chart when it's scaled by the user
        mtChart2.setRedrawWhenScaled(true);
		this.getCanvas().addChild(mtChart1);
		this.getCanvas().addChild(mtChart2);
		mtChart1.setPositionGlobal(new Vector3D(mtApplication.width/2f,mtApplication.height/2f));
		mtChart2.setPositionGlobal(new Vector3D(150+mtApplication.width/2f,150+mtApplication.height/2f));
	}
	@Override
	public void init() {}
	@Override
	public void shutDown() {}
}
