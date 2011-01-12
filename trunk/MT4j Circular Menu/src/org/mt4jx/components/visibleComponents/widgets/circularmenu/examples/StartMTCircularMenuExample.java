package org.mt4jx.components.visibleComponents.widgets.circularmenu.examples;

import org.mt4j.MTApplication;

public class StartMTCircularMenuExample extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addScene(new MTCircularMenuExampleScene(this, "MTCircularMenuExample"));
	}
}
