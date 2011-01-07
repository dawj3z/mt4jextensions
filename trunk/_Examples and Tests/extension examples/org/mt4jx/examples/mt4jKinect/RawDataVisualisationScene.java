package org.mt4jx.examples.mt4jKinect;
import java.util.Hashtable;

import javax.media.opengl.GL;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.MTLight;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.mesh.MTSphere;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GLMaterial;
import org.mt4jx.input.inputSources.mt4jkinect.client.SkeletonMessage;
import org.mt4jx.input.inputSources.mt4jkinect.client.UDPKinectReceiver;
import org.mt4jx.input.inputSources.mt4jkinect.client.UDPKinectRecieverListener;

public class RawDataVisualisationScene extends AbstractScene implements UDPKinectRecieverListener {
	private MTApplication app;
	private MTComponent skeletonLayer;
	private MTComponent layer;
	
	private Hashtable<Integer, Hashtable<Integer, float[]>> userIdAndSkeletonjointPositionTable = new Hashtable<Integer, Hashtable<Integer, float[]>>();
	
	private MTLight light;
	private GLMaterial material;
	
	private MTSphere[] jointComponents = new MTSphere[15];
	
	@Override
	public void skeletonMessageReceived(SkeletonMessage sm) {
		this.userIdAndSkeletonjointPositionTable.put(sm.getUserId(), sm.getJointPositionTable());
	}
	
	public RawDataVisualisationScene(MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		MTColor white = new MTColor(255,255,255);
		this.setClearColor(new MTColor(0, 0, 0, 0));
		
		UDPKinectReceiver receiver = new UDPKinectReceiver();
		System.out.println("connect udp (direct, no input source)");
		receiver.connect();
		receiver.addListener(this);
		
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		this.getCanvas().registerInputProcessor(new ZoomProcessor(app));
		this.getCanvas().addGestureListener(ZoomProcessor.class, new DefaultZoomAction());
		float camX,camY,camZ;
		camX=getSceneCam().getPosition().x;
		camY=getSceneCam().getPosition().y;
		camZ=getSceneCam().getPosition().z;
		this.getSceneCam().setPosition(new Vector3D(camX,camY,camZ+500));
		
		initLightAndMaterial();
		
		this.layer = new MTComponent(app);
		this.getCanvas().addChild(layer);
		
		this.skeletonLayer = new MTComponent(app);
		this.getCanvas().addChild(skeletonLayer);

		{
			MTColor gridCol = new MTColor(0,255,0,128);
			MTLine gridLine;

			MTLine line1 = new MTLine(app, 0,0,0,   1024,0,0);
			line1.setStrokeColor(gridCol);
			this.layer.addChild(line1);
			MTLine line2 = new MTLine(app, 0,0,0,   0,1024,0);
			line2.setStrokeColor(gridCol);
			this.layer.addChild(line2);
			
			MTRectangle r = new MTRectangle(1024,1024, app);
			r.setStrokeColor(new MTColor(0,255,0,128));
			r.setNoFill(true);
			r.setPickable(false);
			r.translate(new Vector3D(0,0,-2048));
			this.layer.addChild(r);
			
			for (int i = 0; i <= 1024; i=i+32) {
				gridLine = new MTLine(app, i,0,0,   i,0,-2048);
				gridLine.setStrokeColor(gridCol);
				gridLine.setPickable(false);
				this.layer.addChild(gridLine);
				
				gridLine = new MTLine(app, 0,i,0,   0,i,-2048);
				gridLine.setStrokeColor(gridCol);
				gridLine.setPickable(false);
				this.layer.addChild(gridLine);
				
				gridLine = new MTLine(app, 0,i,-2048,   1024,i,-2048);
				gridLine.setStrokeColor(gridCol);
				gridLine.setPickable(false);
				this.layer.addChild(gridLine);
			}
		}
		
		this.addJointSpheres();
		
		this.registerPreDrawAction(new IPreDrawAction() {
			@Override
			public void processAction() {
				Hashtable<Integer, float[]> jointPositionTable = userIdAndSkeletonjointPositionTable.get(1);
				if(jointPositionTable!=null){
					for (int i = 0; i < 15; i++) {
						float[] xyz = jointPositionTable.get(i);

						if(xyz!=null){
							float x = xyz[0];
							float y = xyz[1];
							float z = -1*xyz[2];
							jointComponents[i].setPositionGlobal(new Vector3D( x,y,z ) );
							jointComponents[i].setVisible(true);
							System.out.println("RH ORIGINAL x=" +xyz[0]+" "+xyz[1]+" " + xyz[2]);
						}else{
							System.out.println("setVisible(false)");
							jointComponents[i].setVisible(false);
						}

					}
				}
//				Integer[] userIds = userIdAndSkeletonjointPositionTable.keySet().toArray(new Integer[userIdAndSkeletonjointPositionTable.size()]);
//				for (int i = 0; i < userIds.length; i++) {
//					Hashtable<Integer, float[]> jointPositionData = userIdAndSkeletonjointPositionTable.get(userIds[i]);
//					Integer[] jointIds = jointPositionData.keySet().toArray(new Integer[jointPositionData.size()]);
//					for (int j = 0; j < jointIds.length; j++) {
//						float[] xyz = jointPositionData.get(jointIds[j]);
//						app.point(xyz[0],xyz[1],xyz[2]);
//					}
//				}
			}
			@Override
			public boolean isLoop() {
				// TODO Auto-generated method stub
				return true;
			}
		});
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
	}
	private void addJointSpheres(){
		this.jointComponents = new MTSphere[15];
		MTSphere jointComponent;
		for (int i = 0; i < jointComponents.length; i++) {
			if(i==0){
				jointComponent = new MTSphere(app,"SkeletonJoint ", new Vector3D(0,0,0), 100, 100, 40);
			}else{
				jointComponent = new MTSphere(app,"SkeletonJoint ", new Vector3D(0,0,0), 100, 100, 20);
			}
			
			jointComponent.setMaterial(material);
			this.skeletonLayer.addChild(jointComponent);
			this.skeletonLayer.setLight(light);
			jointComponent.setMaterial(material);
			jointComponents[i] = jointComponent;
		}
	}
	private void initLightAndMaterial(){
		//Init light settings
		MTLight.enableLightningAndAmbient(app, 150, 150, 150, 255);
		//Create a light source //I think GL_LIGHT0 is used by processing!
		light = new MTLight(app, GL.GL_LIGHT3, new Vector3D(0,0,0));
		//Set up a material to react to the light
		material = new GLMaterial(Tools3D.getGL(app));
		material.setAmbient(new float[]{ .3f, .3f, .3f, 1f });
		material.setDiffuse(new float[]{ .9f, .9f, .9f, 1f } );
		material.setEmission(new float[]{ .0f, .0f, .0f, 1f });
		material.setSpecular(new float[]{ 1.0f, 1.0f, 1.0f, 1f });  // almost white: very reflective
		material.setShininess(55);// 0=no shine,  127=max shine
		if (!(MT4jSettings.getInstance().isOpenGlMode())){
			System.err.println(this.getClass().getName() + " example can only be run in OpenGL mode.");
			return;
		}
	}
	
	@Override
	public void init() {}
	@Override
	public void shutDown() {}
}
