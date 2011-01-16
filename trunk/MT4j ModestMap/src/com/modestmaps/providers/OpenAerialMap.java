package com.modestmaps.providers;

import com.modestmaps.core.Coordinate;
import com.modestmaps.geo.MercatorProjection;
import com.modestmaps.geo.Transformation;

public class OpenAerialMap extends AbstractMapProvider {

	public OpenAerialMap() {
		super(new MercatorProjection(26, new Transformation(1.068070779e7f, 0.0f, 3.355443185e7f, 0.0f, -1.068070890e7f, 3.355443057e7f)));
	}

	@Override
	public String[] getTileUrls(Coordinate coordinate) {
		String url = "http://tile.openaerialmap.org/tiles/1.0.0/openaerialmap-900913/" + (int)coordinate.zoom + "/" + (int)coordinate.column + "/" + (int)coordinate.row + ".jpg";
		return new String[] {        url       };
	}

	@Override
	public int tileHeight() {
		return 256;
	}

	@Override
	public int tileWidth() {
		return 256;
	}

}
