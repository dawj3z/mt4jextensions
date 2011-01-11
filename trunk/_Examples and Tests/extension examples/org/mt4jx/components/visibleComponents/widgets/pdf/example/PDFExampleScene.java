package org.mt4jx.components.visibleComponents.widgets.pdf.example;
import java.io.File;

import org.mt4j.MTApplication;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4jx.components.visibleComponents.widgets.pdf.MTPDF;

public class PDFExampleScene extends AbstractScene {
	private MTApplication app;
	
	public PDFExampleScene(final MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		
		MTPDF pdf = new MTPDF(mtApplication, new File("./data/demo.pdf"));
		pdf.scaleGlobal(.5f, .5f, .5f, pdf.getCenterPointGlobal());
		this.getCanvas().addChild(pdf);
	}
}
