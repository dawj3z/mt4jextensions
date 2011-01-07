package org.mt4jx.examples.mt4jKinect;
import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.input.inputData.SkeletonJointEvent;
import org.mt4jx.input.inputSources.SkeletonJointProcessor;

public class InputSourceTestScene extends AbstractScene implements IMTInputEventListener {

	public InputSourceTestScene(MTApplication mtApplication, String name) {
		super(mtApplication, name);

		this.setClearColor(new MTColor(0, 64, 0, 255));
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		this.registerGlobalInputProcessor(new SkeletonJointProcessor());
	}
	
	public boolean processInputEvent(MTInputEvent inEvt) {
		if (inEvt instanceof SkeletonJointEvent) {
			SkeletonJointEvent se = (SkeletonJointEvent)inEvt;
			Vector3D position = se.getPosition();

			AbstractShape comp;
			switch (se.getId()) {
			case SkeletonJointEvent.INPUT_DETECTED:
				System.out.println("INPUT_DETECTED");
				break;
			case SkeletonJointEvent.INPUT_UPDATED:
				System.out.println("INPUT_UPDATED");
				break;
			case SkeletonJointEvent.INPUT_ENDED:
				System.out.println("INPUT_ENDED");
			}
		}
		return false;
	}
	@Override
	public void init() {}
	@Override
	public void shutDown() {}
}
