package org.mt4jx.components.visibleComponents.shapes;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.visibleComponents.shapes.GeometryInfo;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.util.math.Vertex;
import org.mt4j.util.opengl.GluTrianglulator;

import processing.core.PApplet;

public class MTComplexPolygon extends MTTriangleMesh {

	public MTComplexPolygon(PApplet pApplet, Vertex[] vertices) {
		super(pApplet, new GeometryInfo(pApplet, new Vertex[]{}), false);
		this.setVertices(vertices);
		this.setNoStroke(false);
	}
	
	@Override
	public void setVertices(Vertex[] vertices) {
		GluTrianglulator triangulator = new GluTrianglulator(getRenderer());
		Vertex[] tris = triangulator.tesselate(vertices);
		triangulator.deleteTess();
		
		super.setVertices(tris);
		
		List<Vertex[]> contours = new ArrayList<Vertex[]>();
		contours.add(vertices);
		this.setOutlineContours(contours);
	}

}
