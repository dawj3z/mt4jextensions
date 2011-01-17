package org.mt4jx.components.visibleComponents.widgets.circularmenu.examples;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
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
import org.mt4jx.components.visibleComponents.widgets.circularmenu.CircularMenuSegmentHandle;
import org.mt4jx.components.visibleComponents.widgets.circularmenu.MTCircularMenu;
import org.mt4jx.util.animation.AnimationUtil;

public class MTCircularMenuExampleScene extends AbstractScene {
	private MTApplication app;
	
	private MTComponent menuLayer;
	private MTComponent itemLayer;
	public MTCircularMenuExampleScene(final MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		// create layers to separate items from menus and to keep menus on top
		this.itemLayer = new MTComponent(mtApplication);
		this.getCanvas().addChild(this.itemLayer);
		this.menuLayer = new MTComponent(mtApplication);
		this.getCanvas().addChild(this.menuLayer);
		
		MTColor black = new MTColor(0,0,0);
		MTColor white = new MTColor(255,255,255);
		
		this.setClearColor(new MTColor(150, 150, 150, 255));
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		// create textfield item
		IFont fontArial = FontManager.getInstance().createFont(mtApplication, "arial.ttf", 50, white);	
		final MTTextArea textField = new MTTextArea(mtApplication, fontArial); 		
		textField.setStrokeColor(black);
		textField.setFillColor(black);
		textField.setText("touch me!");
		//Center the textfield on the screen
		textField.setPositionGlobal(new Vector3D(mtApplication.width/2f, mtApplication.height/2f));
		this.itemLayer.addChild(textField);
		
		// register TapProcessor
		textField.registerInputProcessor(new TapProcessor(mtApplication));
		// create action that shows the menu when the textfield is tapped
		textField.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				if (ge instanceof TapEvent) {
					TapEvent te = (TapEvent) ge;
					switch (te.getId()) {
					case TapEvent.GESTURE_ENDED:
						if (te.isTapped()) {
							CircularMenuSegmentHandle segment;
							final MTCircularMenu menu = new MTCircularMenu(mtApplication, 45, 150);

							// no actionListener required
							segment = menu.createSegment("Cancel");
							segment.setFillColor(new MTColor(0, 127, 0, 255-32));
							
							segment = menu.createSegment("Remove");
							segment.setFillColor(new MTColor(127, 0, 0, 255-32));
							// add action listener: bounce out and destroy
							segment.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									AnimationUtil.bounceOut(textField, true);
								}
							});
							segment = menu.createSegment("Rotate");
							// add action listener: rotate
							segment.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									AnimationUtil.rotate2D(textField, 360f/3f);
								}
							});
							menu.setPositionGlobal(textField.getCenterPointGlobal());
							cleanUpMenuLayer();
							menuLayer.addChild(menu);
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
	private void cleanUpMenuLayer(){
		MTComponent[] menus = this.menuLayer.getChildren();
		for (int i = 0; i < menus.length; i++) {
			this.menuLayer.removeChild(menus[i]);
			menus[i].destroy();
		}
	}
}
