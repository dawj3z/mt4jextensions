package com.modestmaps.providers;

import com.modestmaps.core.Coordinate;
import com.modestmaps.geo.MercatorProjection;
import com.modestmaps.geo.Transformation;

public class BlueMarble extends AbstractMapProvider {

	public BlueMarble() {
		super(new MercatorProjection(26, new Transformation(1.068070779e7f, 0.0f, 3.355443185e7f, 0.0f, -1.068070890e7f, 3.355443057e7f)));
	}

	@Override
	public String[] getTileUrls(Coordinate coordinate) {
		return new String[]{ "http://s3.amazonaws.com/com.modestmaps.bluemarble/" + (int)coordinate.zoom + "-r" + (int)coordinate.row + "-c" + (int)coordinate.column + ".jpg" };
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
