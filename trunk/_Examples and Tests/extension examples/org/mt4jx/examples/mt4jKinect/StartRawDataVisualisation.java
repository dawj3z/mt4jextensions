package org.mt4jx.examples.mt4jKinect;

import org.mt4j.MTApplication;

public class StartRawDataVisualisation extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addScene(new RawDataVisualisationScene(this, "Hello World Scene"));
	}
}
