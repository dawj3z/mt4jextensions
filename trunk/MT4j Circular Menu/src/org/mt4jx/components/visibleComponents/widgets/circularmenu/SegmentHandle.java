package org.mt4jx.components.visibleComponents.widgets.circularmenu;
// TODO: replace segmentIds with handles, add methods to handle
public class SegmentHandle {
	private static long idCounter=0;
	private long id;
	public SegmentHandle(){
		synchronized (this.getClass()) {
			this.id = idCounter++;
		}
	}
	public long getId() {
		return id;
	}
	@Override
	public int hashCode() {
		return ("" + this.getId()).hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SegmentHandle){
			return this.id==((SegmentHandle)obj).getId();
		}else{
			return false;
		}
	}
}
