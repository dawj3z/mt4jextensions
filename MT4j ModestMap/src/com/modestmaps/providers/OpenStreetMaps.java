package com.modestmaps.providers;

import com.modestmaps.core.Coordinate;
import com.modestmaps.geo.MercatorProjection;
import com.modestmaps.geo.Transformation;

public class OpenStreetMaps extends AbstractMapProvider {

	public OpenStreetMaps() {
		super(new MercatorProjection(26, new Transformation(1.068070779e7f, 0, 				 3.355443185e7f, 
															0, 				-1.068070890e7f, 3.355443057e7f)));
	}

	@Override
	public String[] getTileUrls(Coordinate coordinate) {
		int zoom 	= (int) coordinate.zoom;
		int column 	= (int) coordinate.column;
		int row 	= (int) coordinate.row;
		String url = "http://tile.openstreetmap.org/" + zoom +"/" + column + "/" + row + ".png";
		return new String[] {         url       };
	}

	/*
	 public String getZoomString(Coordinate coordinate) {
	      return toOSM( (int)coordinate.column, (int)coordinate.row, (int)coordinate.zoom );
	    }
	 
	 
	private static String toOSM(int i, int j, int k) {
		
		return null;
	}
	 */
	
	
	@Override
	public int tileHeight() {
		return 256;
	}

	@Override
	public int tileWidth() {
		return 256;
	}
	
	/*
	 
	     def __init__(self):
        t = Transformation(1.068070779e7, 0, 3.355443185e7,
		                   0, -1.068070890e7, 3.355443057e7)

        self.projection = MercatorProjection(26, t)

    def tileWidth(self):
        return 256

    def tileHeight(self):
        return 256

    def getTileUrls(self, coordinate):
        return ('http://tile.openstreetmap.org/%d/%d/%d.png' % (coordinate.zoom, coordinate.column, coordinate.row),)

	 
	 */

}
