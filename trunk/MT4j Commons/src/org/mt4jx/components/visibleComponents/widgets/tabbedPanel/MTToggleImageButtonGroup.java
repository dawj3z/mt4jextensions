package org.mt4jx.components.visibleComponents.widgets.tabbedPanel;

import java.util.ArrayList;

import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;

public class MTToggleImageButtonGroup implements MTToggleImageButtonListener {

    private static final ILogger LOG = MTLoggerFactory.getLogger(MTToggleImageButtonGroup.class.getName());

    static {
        LOG.setLevel(ILogger.DEBUG);
    }

    private final ArrayList<MTToggleImageButton> toggleButtons = new ArrayList<MTToggleImageButton>();

    public MTToggleImageButtonGroup() {
    }

    public void add(MTToggleImageButton tib) {
        if (!toggleButtons.contains(tib)) {
            toggleButtons.add(tib);
            tib.addToggleListener(this);
        }
        if (toggleButtons.size() == 1) {
            setSelectedButton(tib);
        } else {
            tib.setSelected(false);
        }
    }

    public void remove(MTToggleImageButton tib) {
        if (toggleButtons.contains(tib)) {
            toggleButtons.remove(tib);
            tib.removeToggleListener(this);
        }
    }

    @Override
    public void buttonClicked(MTToggleImageButton button) {
        LOG.debug("MTToggleImageButtonGroup.buttonClicked(MTToggleImageButton button)");
        setSelectedButton(button);
    }

    private void setSelectedButton(MTToggleImageButton button) {
        for (int i = 0; i < toggleButtons.size(); i++) {
            final MTToggleImageButton current = toggleButtons.get(i);
            LOG.debug("DESELECT: " + current);
            current.setSelected(false);
        }
        if (!button.isSelected()) {
            button.setSelected(true);
        }
    }
}