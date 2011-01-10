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
package org.mt4jx.components.visibleComponents.shapes;

import java.util.ArrayList;

import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsGeometry;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PApplet;

/**
 * @author Uwe Laufs
 *
 */
public class MTEllipseSegmentComplexPoly extends MTComplexPolygon {
	private float theta = 0.0f;
	
	public MTEllipseSegmentComplexPoly(PApplet pApplet, Vertex[] vertices) {
		super(pApplet, vertices);
	}
	
	public MTEllipseSegmentComplexPoly(PApplet pApplet, float innerRadius, float outerRadius, float degrees, float orientationAngleDegrees){
		this(pApplet, new Vertex[]{});
		this.setVertices(this.createEllipseSegment(innerRadius, innerRadius, outerRadius, outerRadius, degrees, 32, orientationAngleDegrees));
	}

	private Vertex[] createBowVertices(float radiusX, float radiusY, int resolution, float degrees, float orientationAngleDegrees){
		degrees = (float)Math.toRadians(degrees);
		Vector3D centerPoint = this.getCenterPointGlobal();
		System.out.println("SEGMENT CENTERPOINT LOCAL: " + centerPoint.x + "/" + centerPoint.y);
		System.out.println(centerPoint);
		Vertex[] verts = new Vertex[resolution+1];
		float t;
		float inc = degrees / (float)resolution;

		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);

		MTColor fillColor = this.getFillColor();

		float orientationRadians = (float)Math.toRadians(orientationAngleDegrees);
		for (int i=0; i<verts.length; i++){
			t = 0 + (i * inc);
			float x = (float) (centerPoint.x - (radiusX * Math.cos(t) * cosTheta)
					+ (radiusY * Math.sin(t) * sinTheta) );
			float y = (float) (centerPoint.y - (radiusX * Math.cos(t) * sinTheta)
					- (radiusY * Math.sin(t) * cosTheta) );
			Vector3D vetorForRotationTmp = new Vector3D(x, y);
			vetorForRotationTmp = vetorForRotationTmp.rotateZ(orientationRadians);
			
			verts[i] = new Vertex(vetorForRotationTmp.x, vetorForRotationTmp.y, centerPoint.z, fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getAlpha());
		}
		//Create tex coords
		float width = radiusX*2;
		float height = radiusY*2;
		float upperLeftX = centerPoint.x-radiusX;
		float upperLeftY = centerPoint.y-radiusY;
		for (int i = 0; i <= resolution; i++) {
			Vertex vertex = verts[i];
			vertex.setTexCoordU((vertex.x-upperLeftX)/width);
			vertex.setTexCoordV((vertex.y-upperLeftY)/height);
			//				System.out.println("TexU:" + vertex.getTexCoordU() + " TexV:" + vertex.getTexCoordV());
		}
		return verts;
	}

	private Vertex[] createEllipseSegment(float innerRadiusX,float innerRadiusY, float outerRadiusX, float outerRadiusY, float degrees, int resolution, float orientationAngleDegrees){
		Vertex[] innerBow = this.createBowVertices(innerRadiusX, innerRadiusY, resolution, degrees,orientationAngleDegrees);
		float[] minMaxInner = ToolsGeometry.getMinXYMaxXY(innerBow);
		for (int i = 0; i < minMaxInner.length; i++) {
			System.out.println("minMaxInner[" + i + "]=" + minMaxInner[i]);
		}
		Vertex[] outerBow = this.createBowVertices(outerRadiusX, outerRadiusY, resolution, degrees,orientationAngleDegrees);
		
		ArrayList<Vertex> segment = new ArrayList<Vertex>();
		
		for (int i = 0; i < innerBow.length; i++) {
			segment.add(innerBow[i]);
		}
		for (int i = outerBow.length-1; i>=0; i--) {
			segment.add(outerBow[i]);
		}
		segment.add((Vertex)segment.get(0).getCopy()); //NEED TO USE COPY BECAUSE TEX COORDS MAY GET SCALED DOUBLE IF SAME VERTEX OBJECT!
		Vertex[] result = segment.toArray(new Vertex[segment.size()]);
		return result;
	}

}
