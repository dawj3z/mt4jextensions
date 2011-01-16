package com.modestmaps.providers;

import com.modestmaps.core.Coordinate;
import com.modestmaps.geo.MercatorProjection;
import com.modestmaps.geo.Transformation;

public abstract class CloudMade extends AbstractMapProvider {
	protected String[] tilePathPrefixes = new String[] {"a", "b", "c"};
	
	public static final String THE_ORIGINAL = "1";
	public static final String FINE_LINE = "2";
	public static final String TOURIST = "7";
	
	public static final String FRESH = "997";
	public static final String PALE_DAWN = "998";
	public static final String MIDNIGHT_COMMANDER = "999";


	public CloudMade() {
		super(new MercatorProjection(26, new Transformation(1.068070779e7f, 0.0f, 3.355443185e7f, 0.0f, -1.068070890e7f, 3.355443057e7f)));
	}

	@Override
	public abstract String[] getTileUrls(Coordinate coordinate) ;
	
	
	public static class TilePathCycle extends CloudMade {
		@Override
		public String[] getTileUrls(Coordinate coordinate) {
			int zoom 	= (int) coordinate.zoom;
			int column 	= (int) coordinate.column;
			int row 	= (int) coordinate.row;
			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".andy.sandbox.cloudmade.com/tiles/cycle/" + zoom + "/" + column + "/" + row + ".png";
			return new String[] {         url       };
		}
	}
	
	
	public static class Original extends CloudMade {
		@Override
		public String[] getTileUrls(Coordinate coordinate) {
			int zoom 	= (int) coordinate.zoom;
			int column 	= (int) coordinate.column;
			int row 	= (int) coordinate.row;
//			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/1/256/" + zoom + "/" + column + "/" + row + ".png";
			
			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/"+ THE_ORIGINAL + "/256/" + zoom + "/" + column + "/" + row + ".png";
			return new String[] {         url       };
		}
	}
	

	public static class FineLine extends CloudMade {
		@Override
		public String[] getTileUrls(Coordinate coordinate) {
			int zoom 	= (int) coordinate.zoom;
			int column 	= (int) coordinate.column;
			int row 	= (int) coordinate.row;
			//"http://{S}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/2/256/{Z}/{X}/{Y}.png";
			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/"+ FINE_LINE + "/256/" + zoom + "/" + column + "/" + row + ".png";
			return new String[] {         url       };
		}
	}
	
	
	public static class TilePathNoNames  extends CloudMade {
		@Override
		public String[] getTileUrls(Coordinate coordinate) {
			int zoom 	= (int) coordinate.zoom;
			int column 	= (int) coordinate.column;
			int row 	= (int) coordinate.row;
			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/3/256/" + zoom + "/" + column + "/" + row + ".png";
			return new String[] {         url       };
		}
	}
	
	public static class Tourist extends CloudMade {
		@Override
		public String[] getTileUrls(Coordinate coordinate) {
			int zoom 	= (int) coordinate.zoom;
			int column 	= (int) coordinate.column;
			int row 	= (int) coordinate.row;
			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/"+ TOURIST + "/256/" + zoom + "/" + column + "/" + row + ".png";
			return new String[] {         url       };
		}
	}

	public static class Fresh extends CloudMade {
		@Override
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/"+ FRESH + "/256/" + (int) coordinate.zoom + "/" + (int) coordinate.column + "/" + (int) coordinate.row + ".png";
			return new String[] {         url       };
		}
	}
	
	
	public static class PaleDawn extends CloudMade {
		@Override
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/"+ PALE_DAWN + "/256/" + (int) coordinate.zoom + "/" + (int) coordinate.column + "/" + (int) coordinate.row + ".png";
			return new String[] {         url       };
		}
	}
	
	public static class Midnight_Commander extends CloudMade {
		@Override
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://" + tilePathPrefixes[(int)random(0, 3)] + ".tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/"+ MIDNIGHT_COMMANDER + "/256/" + (int) coordinate.zoom + "/" + (int) coordinate.column + "/" + (int) coordinate.row + ".png";
			return new String[] {         url       };
		}
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
