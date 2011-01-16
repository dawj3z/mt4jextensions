package org.mt4jx.input.inputData;

import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputSources.AbstractInputSource;
import org.mt4j.util.math.Vector3D;

public class SkeletonJointCursorEvent extends AbstractCursorInputEvt {
	private float z;
	private long sequenceId;
	private int userId;
	
	//TODO: TEST, never tested.
	public SkeletonJointCursorEvent(  AbstractInputSource source, 
								float positionX,
								float positionY, 
								float positionZ,
								InputCursor m,
								int id,
								long sequenceId,
								int userId) {
		
		super(source, positionX, positionY, id, m);
		this.z = positionZ;
		this.sequenceId = sequenceId;
		this.userId = userId;
	}

	public float getZ() {
		return z;
	}

	public long getSequenceId() {
		return sequenceId;
	}

	public int getUserId() {
		return userId;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new SkeletonJointCursorEvent((AbstractInputSource)this.getSource(), this.getPosX(), this.getPosY(), this.z, this.getCursor(), this.getId(), this.sequenceId, this.userId);
	}
	@Override
	public Vector3D getPosition(){
		return new Vector3D(this.getX(), this.getY(), this.getZ());
	}
}
