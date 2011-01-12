package org.mt4jx.input.inputSources.mt4jkinect.examples;

import org.mt4j.MTApplication;
import org.mt4jx.input.inputSources.UDPKinectInputSource;

public class StartInputSourceTestScene extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		this.getInputManager().registerInputSource(new UDPKinectInputSource(this));
		System.out.println("...registered UDPKinectInputSource");
		addScene(new InputSourceTestScene(this, "Input Source Test Scene"));
	}
}
