package org.mt4j.components.visibleComponents.widgets.examples;

import org.mt4j.MTApplication;

public class StartCommonsExample extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addScene(new CommonsExampleScene(this, "CommonsExampleScene"));
	}
}
