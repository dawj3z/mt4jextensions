package org.mt4jx.components.visibleComponents.widgets.circularmenu.examples;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
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

import processing.core.PImage;

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
		final MTTextArea tf1 = new MTTextArea(mtApplication, fontArial); 		
		tf1.setStrokeColor(black);
		tf1.setFillColor(black);
		tf1.setText("touch me!");
		//Center the textfield on the screen
		tf1.setPositionGlobal(new Vector3D(mtApplication.width/2f, mtApplication.height/2f));
		this.itemLayer.addChild(tf1);
		
		// register TapProcessor
		tf1.registerInputProcessor(new TapProcessor(mtApplication));
		// create action that shows the menu when the textfield is tapped
		tf1.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				if (ge instanceof TapEvent) {
					TapEvent te = (TapEvent) ge;
					switch (te.getId()) {
					case TapEvent.GESTURE_ENDED:
						if (te.isTapped()) {
							CircularMenuSegmentHandle segment;
							final MTCircularMenu textMenu = new MTCircularMenu(mtApplication, 45, 150);

							// no actionListener required
							segment = textMenu.createSegment("Cancel");
							segment.setFillColor(new MTColor(0, 127, 0, 255-16));
							
							segment = textMenu.createSegment("Remove");
							segment.setFillColor(new MTColor(127, 0, 0, 255-16));
							// add action listener: bounce out and destroy
							segment.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									AnimationUtil.bounceOut(tf1, true);
								}
							});
							segment = textMenu.createSegment("Rotate");
							// add action listener: rotate
							segment.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									AnimationUtil.rotate2D(tf1, 360f/3f);
								}
							});
							textMenu.setPositionGlobal(tf1.getCenterPointGlobal());
							cleanUpMenuLayer();
							menuLayer.addChild(textMenu);
							AnimationUtil.scaleIn(textMenu);
							AnimationUtil.moveIntoScreen(textMenu, app);
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
		
		
		final MTTextArea tf2 = new MTTextArea(mtApplication, fontArial); 		
		tf2.setStrokeColor(black);
		tf2.setFillColor(black);
		tf2.setText("touch me!");
		//Center the textfield on the screen
		tf2.setPositionGlobal(new Vector3D(mtApplication.width/2f+200f, mtApplication.height/2f+200f));
		this.itemLayer.addChild(tf2);
		// register TapProcessor
		tf2.registerInputProcessor(new TapProcessor(mtApplication));
		// create action that shows the menu when the textfield is tapped
		tf2.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				if (ge instanceof TapEvent) {
					TapEvent te = (TapEvent) ge;
					switch (te.getId()) {
					case TapEvent.GESTURE_ENDED:
						if (te.isTapped()) {
							CircularMenuSegmentHandle segment;
							final MTCircularMenu iconMenu = new MTCircularMenu(mtApplication, 35, 120);

							// no actionListener required
							segment = iconMenu.createSegment(app.loadImage("./data/Address-Book-icon.png"));
							segment.setFillColor(new MTColor(32, 32, 192, 255-32));
							
							segment = iconMenu.createSegment(app.loadImage("./data/iPhoto-icon.png"));
							segment.setFillColor(new MTColor(32, 32, 127, 255-32));
							// add action listener: bounce out and destroy
						
							segment = iconMenu.createSegment(app.loadImage("./data/iChat-icon.png"));
							// add action listener: rotate

							iconMenu.setPositionGlobal(tf2.getCenterPointGlobal());
							cleanUpMenuLayer();
							menuLayer.addChild(iconMenu);
							AnimationUtil.scaleIn(iconMenu);
							AnimationUtil.moveIntoScreen(iconMenu, app);
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
