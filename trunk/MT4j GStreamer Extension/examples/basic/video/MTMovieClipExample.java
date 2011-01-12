package basic.video;

import org.mt4j.MTApplication;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vertex;
import org.mt4jx.components.visibleComponents.widgets.video.MTMovieClip;

public class MTMovieClipExample extends MTApplication {
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize(true);
	}
	
	@Override
	public void startUp() {
		addScene(new MTMovieClipExampleScene(this, "MTMovieClip Example Scene"));
	}

	
	private class MTMovieClipExampleScene extends AbstractScene{
		
		public MTMovieClipExampleScene(MTApplication mtApplication, String name) {
			super(mtApplication, name);
			MTMovieClip movieClip = new MTMovieClip("data" + separator + "station.mov", new Vertex(), mtApplication);
			getCanvas().addChild(movieClip);
		}

		@Override
		public void init() {	}

		@Override
		public void shutDown() {	}
	}
}
