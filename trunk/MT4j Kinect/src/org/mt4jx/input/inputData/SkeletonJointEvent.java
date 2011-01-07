package org.mt4jx.input.inputData;

import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputSources.AbstractInputSource;
import org.mt4j.util.math.Vector3D;

public class SkeletonJointEvent extends MTInputEvent {

	private long sequenceId;
	private int userId;
	private int id;
	private float positionX,positionY,positionZ;
	
	public static final int INPUT_DETECTED = 0;
	
	public static final int INPUT_UPDATED = 1;
	
	public static final int INPUT_ENDED = 2;
	
	//TODO: TEST, never tested.
	public SkeletonJointEvent(  AbstractInputSource source, 
								float positionX,
								float positionY, 
								float positionZ,
								int id,
								long sequenceId,
								int userId) {
		
		super(source);
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.id = id;
		this.sequenceId = sequenceId;
		this.userId = userId;
	}

	public long getSequenceId() {
		return sequenceId;
	}

	public int getUserId() {
		return userId;
	}
	
	public float getPositionX() {
		return positionX;
	}

	public float getPositionY() {
		return positionY;
	}

	public float getPositionZ() {
		return positionZ;
	}

	public int getId() {
		return id;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new SkeletonJointEvent((AbstractInputSource)this.getSource(), this.positionX, this.positionY, this.positionZ, this.id, this.sequenceId, this.userId);
	}
	public Vector3D getPosition(){
		return new Vector3D(this.positionX, this.positionY, this.positionZ);
	}
}
