package org.mt4jx.commons;
import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4jx.components.visibleComponents.shapes.widgets.imageinfo.Foo;
import org.mt4jx.components.visibleComponents.shapes.widgets.imageinfo.MTInfoPanel;

public class CommonsExampleScene extends AbstractScene {
	private MTApplication app;
	
	public CommonsExampleScene(final MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		MTRectangle rect = new MTRectangle(mtApplication, mtApplication.loadImage("./data/logo.gif"));
		MTInfoPanel ip = new MTInfoPanel(rect, mtApplication, "This is the label text.", "This is the text. This is the text. This is the text. This is the text. This is the text. This is the text.", 500, 300);
		this.getCanvas().addChild(ip);
		
		Foo foo = new Foo(mtApplication);
		this.getCanvas().addChild(foo);
	}
}
