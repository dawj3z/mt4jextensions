package org.mt4jx.components.visibleComponents.shapes.widgets.imageinfo;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.MTColor;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.layout.MTColumnLayout2D;

import processing.core.PApplet;

public class MTInfoPanel extends MTRoundRectangle {

    private static final ILogger LOG = MTLoggerFactory.getLogger(MTInfoPanel.class.getName());

    static {
        LOG.setLevel(ILogger.DEBUG);
    }

    private MTColumnLayout2D rows;

    private IFont headlineFont, textFont;

    private final float strokeWeight = 2.5f;

    private final MTColor textColor = new MTColor(255, 255, 255);

    private final MTColor fillColor = new MTColor(0, 0, 0, 192);

    private final MTColor strokeColor = new MTColor(255, 255, 255, 128);

    private final float maxWidth, maxHeight;

    private final String text, labelText;

    private final PApplet pa;

    private final AbstractShape image;

    public MTInfoPanel(PApplet pa, String labelText, AbstractShape image, String text, float maxWidth, float maxHeight) {
        this(pa, labelText, image, text, maxWidth, maxHeight, null, null);
    }

    public MTInfoPanel(PApplet pa, String labelText, AbstractShape image, String text, float maxWidth, float maxHeight, IFont headlineFont, IFont textFont) {
        super(pa, 0, 0, 0, 400, 300, 12, 12);
        setFillColor(fillColor);
        setStrokeColor(strokeColor);
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.headlineFont = headlineFont;
        this.textFont = textFont;
        this.text = text;
        this.labelText = labelText;
        this.pa = pa;
        this.image = image;
        init();
    }

    private void init() {
        rows = new MTColumnLayout2D(pa);
        rows.setPickable(false);
        this.addChild(rows);
        rows.translate(new Vector3D(1, 1));

        // setting default font if missing
        if (headlineFont == null) {
            headlineFont = FontManager.getInstance().createFont(pa, "arial",
                    32, // Font size
                    textColor); // Font outline color
        }
        if (textFont == null) {
            textFont = FontManager.getInstance().createFont(pa, "arial",
                    16, // Font size
                    textColor); // Font outline color
        }
        final MTTextArea ta_label = new MTTextArea(pa, 0, 0, maxWidth, 40, headlineFont);
        ta_label.setNoFill(true);
        ta_label.setNoStroke(true);
        ta_label.setPickable(false);
        if (labelText != null) {
            ta_label.setText(labelText);
        }
        rows.addChild(ta_label);
        MTLine line = new MTLine(pa, 0, 0, maxWidth, 0);
        line.setStrokeColor(strokeColor);
        line.setStrokeWeight(strokeWeight);
        line.setPickable(false);
        rows.addChild(line);

        image.setNoStroke(true);
        image.setPickable(false);
        rows.addChild(image);

        float textHeight = maxHeight - rows.getHeightXY(TransformSpace.GLOBAL);
        if (textHeight < 90) {
            textHeight = 90;
        }

        line = new MTLine(pa, 0, 0, maxWidth, 0);
        line.setStrokeColor(strokeColor);
        line.setStrokeWeight(strokeWeight);
        line.setPickable(false);
        rows.addChild(line);

        final MTTextArea ta_text = new MTTextArea(pa, 0, 0, maxWidth, textHeight, textFont);
        ta_text.setNoFill(true);
        ta_text.setNoStroke(true);
        ta_text.setPickable(false);
        if (text != null) {
            ta_text.setText(text);
        }
        rows.addChild(ta_text);
        setSizeLocal(rows.getWidthXY(TransformSpace.LOCAL), rows.getHeightXY(TransformSpace.LOCAL));
        {
            final float w = getWidthXY(TransformSpace.LOCAL);
            final float h = getHeightXY(TransformSpace.LOCAL);
            // System.out.println("w/h" + w + "/" + h);
            if ((w > maxWidth) || (h > maxHeight)) {
                final float fw = maxWidth / w;
                final float fh = maxHeight / h;
                // System.out.println("fw/fh" + fw + "/" + fh);
                if (fw < fh) {
                    scale(fw, fw, fw, getCenterPointLocal());
                } else {
                    scale(fh, fh, fh, getCenterPointLocal());
                }
            }
        }
        final float w = getWidthXY(TransformSpace.GLOBAL);
        final float h = getHeightXY(TransformSpace.GLOBAL);
        LOG.debug("w/h" + w + "/" + h);
    }
}
