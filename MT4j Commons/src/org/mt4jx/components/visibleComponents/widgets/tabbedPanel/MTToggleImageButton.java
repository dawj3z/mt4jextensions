package org.mt4jx.components.visibleComponents.widgets.tabbedPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.util.MTColor;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;

import processing.core.PApplet;
import processing.core.PImage;

public class MTToggleImageButton extends MTImageButton {

    private static final ILogger LOG = MTLoggerFactory.getLogger(MTToggleImageButton.class.getName());

    static {
        LOG.setLevel(ILogger.DEBUG);
    }

    private final MTImageButton overlayButton;

    private boolean selected = false;

    private final ArrayList<MTToggleImageButtonListener> toggleListeners = new ArrayList<MTToggleImageButtonListener>();

    private final MTToggleImageButton selfRef;

    public MTToggleImageButton(PImage texture, PApplet pApplet) {
        super(texture, pApplet);
        setName("Unnamed Toggle Button");
        selfRef = this;
        overlayButton = new MTImageButton(texture, pApplet);
        this.addChild(overlayButton);
        overlayButton.setFillColor(new MTColor(0, 0, 0, 128));
        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (e.getID()) {
                    case TapEvent.BUTTON_CLICKED:
                        for (int i = 0; i < toggleListeners.size(); i++) {
                            toggleListeners.get(i).buttonClicked(selfRef);
                        }
                        break;

                    default:
                        break;
                }
            }
        };
        overlayButton.addActionListener(al);
        addActionListener(al);
        setNoStroke(true);
        overlayButton.setNoStroke(true);
        toggle();
    }

    public void toggle() {
        LOG.debug("TOGGLE");
        if (selected) {
            overlayButton.setVisible(true);
            selected = false;
        } else {
            overlayButton.setVisible(false);
            selected = true;
        }
    }

    @Override
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            toggle();
        }
    }

    public void addToggleListener(MTToggleImageButtonListener listener) {
        if (!toggleListeners.contains(listener)) {
            toggleListeners.add(listener);
        }
    }

    public void removeToggleListener(MTToggleImageButtonListener listener) {
        toggleListeners.remove(listener);
    }

    @Override
    public synchronized void addActionListener(ActionListener listener) {
        super.addActionListener(listener);
        overlayButton.addActionListener(listener);
    }
}
