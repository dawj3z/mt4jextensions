package mapsExample;

import org.mt4j.MTApplication;
import org.mt4j.sceneManagement.AbstractScene;

import com.modestmaps.TestInteractiveMap;

public class MTMapsExample extends MTApplication{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize(true);
	}

	@Override
	public void startUp() {
		addScene(new MTMapsExampleScene(this, "maps example scene"));
	}
	
	private class MTMapsExampleScene extends AbstractScene{

		public MTMapsExampleScene(MTApplication mtApplication, String name) {
			super(mtApplication, name);
			
			TestInteractiveMap map = new TestInteractiveMap(mtApplication);
			getCanvas().addChild(map);
		}
		
	}

}
