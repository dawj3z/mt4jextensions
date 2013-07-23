package org.mt4jx.components.visibleComponents.widgets.tabbedPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.util.MTColor;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4j.util.math.Vector3D;
import org.mt4jx.components.visibleComponents.layout.MTColumnLayout2D;
import org.mt4jx.components.visibleComponents.layout.MTRowLayout2D;

import processing.core.PApplet;

public class MTTabbedPanel extends MTColumnLayout2D {

    private static final ILogger LOG = MTLoggerFactory.getLogger(MTTabbedPanel.class.getName());

    static {
        LOG.setLevel(ILogger.DEBUG);
    }

    private final MTRowLayout2D tabContainer;

    private final MTRowLayout2D contentContainer;

    private final Hashtable<MTComponent, MTComponent> tabAndContent = new Hashtable<MTComponent, MTComponent>();

    private final MTToggleImageButtonGroup toggleGroup = new MTToggleImageButtonGroup();

    public MTTabbedPanel(PApplet pApplet) {
        super(pApplet);
        tabContainer = new MTRowLayout2D(pApplet);
        tabContainer.setPickable(false);
        tabContainer.setFillColor(new MTColor(255, 0, 0));
        contentContainer = new MTRowLayout2D(pApplet);
        contentContainer.setPickable(false);
        this.addChild(tabContainer);
        this.addChild(contentContainer);
        contentContainer.translate(new Vector3D(0, -10));
        setNoFill(true);
        setNoStroke(true);
    }

    public void addTab(MTToggleImageButton tabButton, MTComponent tabContent) {
        tabAndContent.put(tabButton, tabContent);
        tabButton.setNoStroke(true);
        toggleGroup.add(tabButton);
        createTab(tabButton, tabContent);
        if (tabAndContent.size() == 1) {
            this.setVisibleContent(tabButton);
        }
    }

    private void createTab(final MTImageButton tabButton, MTComponent tabContent) {
        tabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.debug("MTTabbedPanel.actionPerformed(ActionEvent e)");
                switch (e.getID()) {
                    case TapEvent.BUTTON_CLICKED:
                        setVisibleContent(tabButton);
                        break;
                    default:
                        break;
                }
            }
        });
        tabContainer.addChild(tabButton);
        contentContainer.addChild(tabContent);
    }

    private synchronized void setVisibleContent(final MTImageButton tabButton) {
        final MTComponent[] content = tabAndContent.values().toArray(new MTComponent[tabAndContent.size()]);
        for (final MTComponent element : content) {
            element.setVisible(false);
        }
        final MTImageButton[] tabs = tabAndContent.keySet().toArray(new MTImageButton[tabAndContent.size()]);
        this.setVisibleContent(tabAndContent.get(tabButton));
    }

    public synchronized void setVisibleContent(final MTComponent tabContent) {
        LOG.debug("setVisibleContent(final MTComponent tabContent)");
        final MTComponent[] content = tabAndContent.values().toArray(new MTComponent[tabAndContent.size()]);
        for (final MTComponent element : content) {
            if (tabContent.equals(element)) {
                for (final MTComponent element2 : content) {
                    element2.setVisible(false);
                }
                element.setVisible(true);
            }
        }
    }

    public MTRowLayout2D getTabContainer() {
        return tabContainer;
    }

    public MTRowLayout2D getContentContainer() {
        return contentContainer;
    }

}
