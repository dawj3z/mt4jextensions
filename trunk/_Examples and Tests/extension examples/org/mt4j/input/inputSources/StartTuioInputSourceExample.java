package org.mt4j.input.inputSources;

import org.mt4j.MTApplication;
import org.mt4j.input.inputSources.AbstractInputSource;
import org.mt4j.input.inputSources.Tuio2DCursorInputSource;
import org.mt4j.input.inputSources.TuioInputSource;

public class StartTuioInputSourceExample extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		AbstractInputSource[] allSources = this.getInputManager().getInputSources();
		for (int i = 0; i < allSources.length; i++) {
			if(allSources[i] instanceof TuioInputSource){
				this.getInputManager().unregisterInputSource(allSources[i]);
				System.out.println("unregistered " + allSources[i].getClass().getName());
			}
		}
		this.getInputManager().registerInputSource(new Tuio2DCursorInputSource(this));
		System.out.println("register Tuio2DCursorInputSource");
		addScene(new Scene(this, "Tuio Input Source Test Scene"));
	}
}
