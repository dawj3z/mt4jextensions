package org.mt4jx.input.inputSources.mt4jkinect.client;
import java.util.Hashtable;


public class SkeletonMessage extends KinectMessage {
    public static final int SKELETONJOINT_HEAD=0;
    public static final int SKELETONJOINT_NECK=1;

    public static final int SKELETONJOINT_LEFTSHOULDER=2;
    public static final int SKELETONJOINT_LEFTWLBOW=3;
    public static final int SKELETONJOINT_LEFTHAND=4;

    public static final int SKELETONJOINT_RIGHTSHOULDER=5;
    public static final int SKELETONJOINT_RIGHTELBOW=6;
    public static final int SKELETONJOINT_RIGHTHAND=7;

    public static final int SKELETONJOINT_TORSO=8;

    public static final int SKELETONJOINT_LEFTHIP=9;
    public static final int SKELETONJOINT_LEFTKNEE=10;
    public static final int SKELETONJOINT_LEFTFOOD=11;

    public static final int SKELETONJOINT_RIGHTHIP=12;
    public static final int SKELETONJOINT_RIGHTKNEE=13;
    public static final int SKELETONJOINT_RIGHTFOOD=14;
    
    private long sequenceId;
    private int userId;
    private long timestamp;
    private Hashtable<Integer, float[]> jointPositionTable = new Hashtable<Integer, float[]>();

    public SkeletonMessage(long sequenceId, int userId, Hashtable<Integer, float[]> jointPositionTable){
    	this.userId = userId;
    	this.sequenceId = sequenceId;
    	this.jointPositionTable = jointPositionTable;
    	this.timestamp = System.currentTimeMillis();
    }
	public int getUserId() {
		return userId;
	}
	public long getSequenceId() {
		return sequenceId;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public Hashtable<Integer, float[]> getJointPositionTable() {
		return jointPositionTable;
	}
	public boolean containsPosition(int jointId){
		return this.jointPositionTable.contains(jointId);
	}
	public float[] getPosition(int jointId){
		return this.jointPositionTable.get(jointId);
	}
	@Override
	public String toString() {
		return "[SkeletonMessage, userId=" + this.userId + ", sequenceId=" + this.sequenceId + ", " + this.jointPositionTable.size() + " joints]";
	}
}
