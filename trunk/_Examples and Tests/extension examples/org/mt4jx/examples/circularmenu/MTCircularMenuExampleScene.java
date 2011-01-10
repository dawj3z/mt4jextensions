package org.mt4jx.examples.circularmenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.AnimationUtil;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.CircularMenuSegmentHandle;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.MTCircularMenu;

public class MTCircularMenuExampleScene extends AbstractScene {
	private MTApplication app;
	public MTCircularMenuExampleScene(final MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		
		MTColor black = new MTColor(0,0,0);
		MTColor white = new MTColor(255,255,255);
		this.setClearColor(new MTColor(150, 150, 150, 255));
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		IFont fontArial = FontManager.getInstance().createFont(mtApplication, "arial.ttf", 50, white);	

		
		final MTTextArea textField = new MTTextArea(mtApplication, fontArial); 		
		textField.setStrokeColor(black);
		textField.setFillColor(black);
		textField.setText("touch me!");
		//Center the textfield on the screen
		textField.setPositionGlobal(new Vector3D(mtApplication.width/2f, mtApplication.height/2f));
		//Add the textfield to our canvas
		this.getCanvas().addChild(textField);
		
		textField.registerInputProcessor(new TapProcessor(mtApplication));
		textField.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				if (ge instanceof TapEvent) {
					TapEvent te = (TapEvent) ge;
					switch (te.getId()) {
					case TapEvent.GESTURE_ENDED:
						if (te.isTapped()) {
							CircularMenuSegmentHandle segment;
							final MTCircularMenu menu = new MTCircularMenu(mtApplication, 60);

							segment = menu.createSegment("Cancel");
							segment.setFillColor(new MTColor(0, 127, 0, 255-32));
							segment = menu.createSegment("Remove");
							segment.setFillColor(new MTColor(127, 0, 0, 255-32));
							segment.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									AnimationUtil.bounceOut(textField, true);
								}
							});
							segment = menu.createSegment("Rotate");
							segment.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									AnimationUtil.rotate2D(textField, 110);
								}
							});
							menu.setPositionGlobal(textField.getCenterPointGlobal());
							getCanvas().addChild(menu);
							AnimationUtil.scaleIn(menu);
							AnimationUtil.moveIntoScreen(menu, app);
						}
						break;
					default:
						System.out.println("TapEvent!!");
						break;
					}
				}
				return false;
			}

		});
	}
	@Override
	public void init() {}
	@Override
	public void shutDown() {}
}
