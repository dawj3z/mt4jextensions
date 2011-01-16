package com.modestmaps.providers;

import com.modestmaps.core.Coordinate;
import com.modestmaps.geo.LinearProjection;
import com.modestmaps.geo.Transformation;

public class DailyPlanet extends AbstractMapProvider {

	public DailyPlanet() {
		super(new LinearProjection(1, new Transformation(
				0.3183098861837907f, 0,	 					1,
                0, 					-0.3183098861837907f, 	0.5f)));
	}

	
	@Override
	public String[] getTileUrls(Coordinate coord) {
		// zoom level 0 is a 512x512 tile containing a linearly projected map of the world in the top half:
		// http://wms.jpl.nasa.gov/wms.cgi?request=GetMap&width=512&height=512&layers=daily_planet&styles=&srs=EPSG:4326&format=image/jpeg&bbox=-180,-270,180,90
		// the -270 there works, and kind of makes sense, and gives the same image as:
		// http://wms.jpl.nasa.gov/wms.cgi?request=GetMap&width=512&height=512&layers=daily_planet&styles=&srs=EPSG:4326&format=image/jpeg&bbox=-180,-90,180,90

		coord = sourceCoordinate(coord);

		double tilesWide = Math.pow(2, coord.zoom);
		double tilesHigh = Math.pow(2, coord.zoom -1);

		double w = -180.0 + (360.0 * coord.column / tilesWide);
		double n = 90 - (180.0 * coord.row / tilesHigh);
		double e = w + (360.0 / tilesWide);
		double s = n + (180.0 / tilesHigh);

//		String bbox = [ w, s, e, n ].join(',');

		// don't use URLVariables to build this URL, because there's a chance that the cache might require things in a particular order
		// here's the pattern: request=GetMap&layers=daily_planet&srs=EPSG:4326&format=image/jpeg&styles=&width=512&height=512&bbox=-180,88,-178,90
		// from http://onearth.jpl.nasa.gov/wms.cgi?request=GetTileService
		
		String url = "http://wms.jpl.nasa.gov/wms.cgi?" + 
				"request=GetMap" + 
				"&layers=daily_planet" + 
				"&srs=EPSG:4326" + 
				"&format=image/jpeg" + 
				"&styles=" + 
				"&width=512" + 
				"&height=512" + 
//				"&bbox=" + bbox;
				"&bbox=" + (int)w + ","+ (int)n + ","+ (int)e + ","+ (int)s;
				
		//geht
//		http://wms.jpl.nasa.gov/wms.cgi?request=GetMap&width=512&height=512&layers=daily_planet&styles=&srs=EPSG:4326&format=image/jpeg&bbox=-180,-270,180,90
		return new String[]{ url };
	}

	@Override
	public Coordinate sourceCoordinate(Coordinate coord) {
		double tilesWide = Math.pow(2, coord.zoom);
		double tilesHigh = Math.ceil(Math.pow(2, coord.zoom-1));
		coord = coord.copy();
		while (coord.row < 0) 
			coord.row += tilesHigh;
		while (coord.column < 0) 
			coord.column += tilesWide;
		
		coord.row 		%= tilesHigh;
		coord.column 	%= tilesWide;
		return coord;
	}

	
	
	@Override
	public int tileHeight() {
		return 512;
	}

	@Override
	public int tileWidth() {
		return 512;
	}

}
