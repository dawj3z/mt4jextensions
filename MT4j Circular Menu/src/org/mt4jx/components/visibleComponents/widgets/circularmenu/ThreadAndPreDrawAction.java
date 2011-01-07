package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import org.mt4j.components.visibleComponents.widgets.progressBar.AbstractProgressThread;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.sceneManagement.Iscene;

public abstract class ThreadAndPreDrawAction extends AbstractProgressThread {
	private Iscene scene;
	private int priority = Thread.MIN_PRIORITY;

	public ThreadAndPreDrawAction(Iscene scene){
		super(100);
		this.scene = scene;
		this.setPriority(priority);
		this.start();
	}
	@Override
	public void run() {
		System.out.println("DO IN THREAD FIRST");
		doFirstThreaded();
		IPreDrawAction preDraw = new IPreDrawAction(){
			@Override
			public boolean isLoop() {
				return false;
			}
			@Override
			public void processAction() {
				System.out.println("DO IN PREDRAW ACTION SECOND");
				doSecondPreDraw();
			}
		};
		this.scene.registerPreDrawAction(preDraw);
	}
	public abstract void doFirstThreaded();
	public abstract void doSecondPreDraw();
}
