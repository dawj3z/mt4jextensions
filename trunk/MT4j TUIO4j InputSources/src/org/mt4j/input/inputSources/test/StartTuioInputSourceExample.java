package org.mt4j.input.inputSources.test;

import org.mt4j.MTApplication;
import org.mt4j.input.inputSources.AbstractInputSource;
import org.mt4j.input.inputSources.Tuio2DCursorInputSource;
import org.mt4j.input.inputSources.Tuio2dObjectInputSource;
/**
 * See license.txt for license information.
 * @author Uwe Laufs
 * @version 1.0
 */
public class StartTuioInputSourceExample extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		AbstractInputSource[] allSources = this.getInputManager().getInputSources();
		/*
		for (int i = 0; i < allSources.length; i++) {
			if(allSources[i].getClass().getSimpleName().equals("TuioInputSource")){
				this.getInputManager().unregisterInputSource(allSources[i]);
				System.out.println("unregistered " + allSources[i].getClass().getName());
			}
		}
		*/
		this.getInputManager().registerInputSource(new Tuio2DCursorInputSource(this));
		System.out.println("register Tuio2DCursorInputSource");
		this.getInputManager().registerInputSource(new Tuio2dObjectInputSource(this));
		System.out.println("register Tuio2dObjectInputSource");
		addScene(new Scene(this, "Tuio Input Source Test Scene"));
	}
}
