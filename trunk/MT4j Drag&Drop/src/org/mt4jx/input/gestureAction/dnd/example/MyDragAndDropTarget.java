package org.mt4jx.input.gestureAction.dnd.example;

import java.util.ArrayList;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.util.MTColor;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4jx.input.gestureAction.dnd.DragAndDropTarget;

import processing.core.PApplet;

public class MyDragAndDropTarget extends MTRectangle implements DragAndDropTarget {

    /** The Constant logger. */
    private static final ILogger LOG = MTLoggerFactory.getLogger(MyDragAndDropTarget.class.getName());

    static {
        LOG.setLevel(ILogger.DEBUG);
    }

    private final ArrayList<MTComponent> droppedComponents = new ArrayList<MTComponent>();

    private final MTTextArea text;

    public MyDragAndDropTarget(PApplet pApplet, IFont font) {
        super(100, 100, 400, 200, pApplet);

        final IFont arial = FontManager.getInstance().createFont(pApplet, "arial.ttf",
                30, // Font size
                new MTColor(0, 0, 0, 255), // Font fill color
                new MTColor(0, 0, 0, 255));

        text = new MTTextArea(pApplet, arial);
        text.setNoFill(true);
        text.setNoStroke(true);
        setStrokeWeight(3f);
        this.addChild(text);
        text.setPositionGlobal(getCenterPointGlobal());
        text.removeAllGestureEventListeners();
        setPickable(false);
    }

    public void setText() {
        String text = getName() + "\n";
        for (int i = 0; i < droppedComponents.size(); i++) {
            text += "- " + droppedComponents.get(i).getName() + "\n";
        }
        this.text.setText(text);
        this.text.setPositionGlobal(getCenterPointGlobal());
    }

    @Override
    public void componentDropped(MTComponent droppedComponent, DragEvent de) {
        if (!droppedComponents.contains(droppedComponent)) {
            droppedComponents.add(droppedComponent);
        }

        setStrokeColor(getFillColor());
        LOG.debug(getName() + ": " + droppedComponent.getName() + " dropped.");
        setText();
    }

    @Override
    public void componentEntered(MTComponent droppedComponent) {
        LOG.debug(getName() + ": " + droppedComponent.getName() + " entered.");
        setStrokeColor(new MTColor(255, 0, 0));
        setText();
    }

    @Override
    public void componentExited(MTComponent droppedComponent) {
        droppedComponents.remove(droppedComponent);
        setStrokeColor(getFillColor());
        LOG.debug(getName() + ": " + droppedComponent.getName() + " exited.");
        setText();
    }

    @Override
    public boolean dndAccept(MTComponent component) {
        return true;
    }
}
