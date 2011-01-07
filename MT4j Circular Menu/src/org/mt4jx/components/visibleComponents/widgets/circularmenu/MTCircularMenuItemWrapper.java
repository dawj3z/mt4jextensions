package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import org.mt4j.components.visibleComponents.shapes.AbstractShape;

public class MTCircularMenuItemWrapper {
	private AbstractShape wrapped;
	private int id;
	public MTCircularMenuItemWrapper(AbstractShape wrapped, int id){
		this.wrapped = wrapped;
		this.id = id;
	}
	public AbstractShape getWrapped() {
		return wrapped;
	}
	public int getId() {
		return id;
	}
	
}
