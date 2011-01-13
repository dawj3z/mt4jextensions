package basic.video;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.gestureAction.DefaultPanAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.componentProcessors.panProcessor.PanProcessorTwoFingers;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;
import org.mt4jx.components.visibleComponents.widgets.video.MTVideoTexture;

import processing.core.PApplet;
import advanced.puzzle.PuzzleFactory;

public class VideoPuzzleTest_ extends MTApplication {
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize(true);
	}
	
	
	@Override
	public void startUp() {
		addScene(new Scene(this, "VideoPuzzleTest"));
	}
	
	private class Scene extends AbstractScene{
		private PuzzleFactory pf;
		private AbstractShape[] tiles;
		
		private String path = "data" + separator;
			
		public Scene(MTApplication mtApplication, String name) {
			super(mtApplication, name);
			registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
			
			getCanvas().registerInputProcessor(new ZoomProcessor(mtApplication, 700));
			getCanvas().addGestureListener(ZoomProcessor.class, new DefaultZoomAction());
			getCanvas().registerInputProcessor(new PanProcessorTwoFingers(mtApplication, 700));
			getCanvas().addGestureListener(PanProcessorTwoFingers.class, new DefaultPanAction());
			
			pf = new PuzzleFactory(getMTApplication());
//			final MyVideoView vidTex = new MyVideoView(path + "CityWall.h264.mp4", new Vertex(0,0), getMTApplication());
			final MyVideoView vidTex = new MyVideoView(path + "station.mov", new Vertex(0,0), getMTApplication());
			
			vidTex.setNoFill(true);
			vidTex.setNoStroke(true);
			vidTex.setPickable(false);
			getCanvas().addChild(vidTex);
			getMTApplication().invokeLater(new Runnable() {
				public void run() {
					vidTex.loop();
				}
			});
		}
		
		
		private class MyVideoView extends MTVideoTexture{
			public MyVideoView(String movieFile, Vertex upperLeft, PApplet pApplet) {
				super(movieFile, upperLeft, pApplet);
			}
			
			@Override
			protected void onFirstFrame() {
				super.onFirstFrame();
				getMTApplication().invokeLater(new Runnable() {
					public void run() {
						tiles = pf.createTiles(getTexture(), 3);
						for (AbstractShape sh : tiles) {
							sh.setPositionGlobal(new Vector3D(ToolsMath.getRandom(0, MT4jSettings.getInstance().getWindowWidth()), ToolsMath.getRandom(0, MT4jSettings.getInstance().getWindowHeight())));
						}
						getCanvas().addChildren(tiles);
					}
				});
			}
		}

		public void onEnter() {}
		
		public void onLeave() {}
		
	}


}
