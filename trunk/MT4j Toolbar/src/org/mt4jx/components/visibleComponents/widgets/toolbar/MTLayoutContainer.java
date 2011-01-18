/***********************************************************************
 *   MT4j Extension: Toolbar
 *   
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License (LGPL)
 *   as published by the Free Software Foundation, either version 3
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the LGPL
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4jx.components.visibleComponents.widgets.toolbar;

import java.util.ArrayList;
import java.util.Iterator;

import org.mt4j.MTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

/**
 * Implementation of a simple layout manager. When adding other components as
 * children gestures will be removed and their positioning handled.
 * 
 * @author Alexander Phleps
 */
public class MTLayoutContainer extends MTRectangle {
	// constants ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
	// ---- ---- ---- ----
	public static final int LAYOUT_VERTICAL = 0;
	public static final int LAYOUT_HORIZONTAL = 1;

	public static final int ALIGN_NONE = 0;
	public static final int ALIGN_BOTTOM = 1;
	public static final int ALIGN_TOP = 2;
	public static final int ALIGN_LEFT = 3;
	public static final int ALIGN_RIGHT = 4;
	
	public static MTLayoutContainer visibleOne = null;

	// variables ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
	// ---- ---- ---- ----
	private int layout = 0;
	private int align = 0;

	private PApplet parent;

	// private float x = 0f;
	// private float y = 0f;
	// private float width = 0f;
	// private float height = 0f;

	private float borderSize = 6f;
	private ArrayList<AbstractShape> components;
	private boolean removeGestureProcessors = true;

	// methods ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
	// ---- ---- ----

	/**
	 * Constructor.
	 */
	public MTLayoutContainer(int align, int layout, MTApplication app) {
		super(app, 0, 0, 0, 60, 60);

		this.parent = app;
		this.layout = layout;
		this.align = align;
		components = new ArrayList<AbstractShape>();
		unregisterAllInputProcessors();
	}

	/**
	 * Adding children unregisters their gestures and calls the layout()
	 * function
	 */
	public void addChild(AbstractShape comp) {
		super.addChild(comp);
		components.add(comp);
		// comp.unregisterAllInputProcessors();
		if(removeGestureProcessors){
			comp.removeAllGestureEventListeners(DragProcessor.class);
			comp.removeAllGestureEventListeners(RotateProcessor.class);
			comp.removeAllGestureEventListeners(ScaleProcessor.class);
		}

		layout();
		align();
	}

	/**
	 * Places children next to each other in a horizontal or vertical (TODO!)
	 * manner
	 */
	protected void layout() {
		Iterator<AbstractShape> it = components.iterator();
		AbstractShape current = null;
		float currentWidth = 0f;
		float currentHeight = 0f;
		float addedLenght = 0f;
		float maxThickness = 0f;

		while (it.hasNext()) {
			current = (AbstractShape) it.next();
			currentWidth = current.getWidthXY(TransformSpace.LOCAL);
			currentHeight = current.getHeightXY(TransformSpace.LOCAL);

			if (layout == LAYOUT_HORIZONTAL) {
				current.setPositionRelativeToParent(new Vector3D(addedLenght
						+ currentWidth / 2, currentHeight / 2));
				addedLenght += currentWidth;
				if (maxThickness < currentHeight)
					maxThickness = currentHeight;
			} else if (layout == LAYOUT_VERTICAL) {
				current.setPositionRelativeToParent(new Vector3D(
						currentWidth / 2, addedLenght + currentHeight / 2));
				addedLenght += currentHeight;
				if (maxThickness < currentWidth)
					maxThickness = currentWidth;
			}
		}

		if (layout == LAYOUT_HORIZONTAL) {
			this.setWidthLocal(addedLenght);
			this.setHeightLocal(maxThickness);
		} else if (layout == LAYOUT_VERTICAL) {
			this.setWidthLocal(maxThickness); // ###
			this.setHeightLocal(addedLenght);
		}
	}

	/**
	 * Handles alignment of the container.
	 */
	private void align() {
		float sceneWidth = parent.getWidth();
		float sceneHeight = parent.getHeight();

		float width = getWidthXY(TransformSpace.LOCAL);
		float height = getHeightXY(TransformSpace.LOCAL);

		if (align == ALIGN_BOTTOM) {
			this.setPositionGlobal(new Vector3D(sceneWidth / 2, sceneHeight
					- height / 2));

		} else if (align == ALIGN_TOP) {
			this
					.setPositionGlobal(new Vector3D(sceneWidth / 2,
							0 + height / 2));

		} else if (align == ALIGN_LEFT) {
			this
					.setPositionGlobal(new Vector3D(0 + width / 2,
							sceneHeight / 2));

		} else if (align == ALIGN_RIGHT) {
			this.setPositionGlobal(new Vector3D(sceneWidth - width / 2,
					sceneHeight / 2));
		}
	}

	public void alignTo(MTToolbarButton button, int align, int layout) {
		float x = 0f;
		float y = 0f;

		float buttonHeight = button.getHeightXY(TransformSpace.GLOBAL);
		float buttonWidth = button.getWidthXY(TransformSpace.GLOBAL);
		float listHeight = this.getHeightXY(TransformSpace.GLOBAL);
		float listWidth = this.getWidthXY(TransformSpace.GLOBAL);

		if (layout == MTLayoutContainer.LAYOUT_HORIZONTAL) {
			if (align == MTLayoutContainer.ALIGN_BOTTOM) {
				x = (-listWidth / 2) + (buttonWidth / 2);
				y = -listHeight;
			} else if (align == MTLayoutContainer.ALIGN_TOP) {
				x = (-listWidth / 2) + (buttonWidth / 2);
				y = buttonHeight;
			}
		} else if (layout == MTLayoutContainer.LAYOUT_VERTICAL) {
			if (align == MTLayoutContainer.ALIGN_LEFT) {
				x = buttonWidth;
				y = buttonHeight / 2 - listHeight / 2;
			} else if (align == MTLayoutContainer.ALIGN_RIGHT) {
				x = -listWidth;
				y = buttonHeight / 2 - listHeight / 2;
			}
		}
		this.translate(new Vector3D(x, y));
	}

	// getter / setter

	public int getAlign() {
		return align;
	}

	public int getLayout() {
		return layout;
	}

	public void setAlignment(int align) {
		this.align = align;
	}

	public void setLayout(int layout) {
		this.layout = layout;
	}

	/**
	 * Toggles visibility, hides the current open sub-menu when opening a new one
	 */
	public void toggle() {
		if (this.isVisible()) {
			setVisible(false);
		} else {
			if(visibleOne != null)
				visibleOne.setVisible(false);
			setVisible(true);
			visibleOne = this;
		}

	}

	// processing level ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
	// ---- ---- ---- ---- ----

	/**
	 * Draws border, color and transparency on processing level.
	 */
	public void drawComponent(processing.core.PGraphics g) {
		parent.fill(PApplet.GRAY, 100);
		parent.strokeWeight(borderSize);
		parent.stroke(255, 255, 255, 100);
		// parent.strokeJoin(parent.BEVEL); //strokeJoin() is not available with
		// this renderer.
		parent.rectMode(PApplet.CORNER);
		// parent.rect(-borderSize, -borderSize, buttonCount * size + borderSize
		// * 2, size + borderSize * 2);
		parent.rect(0, 0, this.getWidthXY(TransformSpace.LOCAL), this
				.getHeightXY(TransformSpace.LOCAL));
	}
	public void setAutoRemoveGestureProcessors(boolean remove){
		this.removeGestureProcessors = remove;
	}
}
