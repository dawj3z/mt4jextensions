package org.mt4jx.components.visibleComponents.widgets.toolbar.example;

import org.mt4j.MTApplication;
/**
 * @author Alexander Phleps
 *
 */
public class StartTestScene extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize();
	}
	
	@Override
	public void startUp() {
		addScene(new TestScene(this, "TestScene"));
	}
}
