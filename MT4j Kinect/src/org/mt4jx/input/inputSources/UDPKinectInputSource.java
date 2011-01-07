package org.mt4jx.input.inputSources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import org.mt4j.MTApplication;
import org.mt4j.input.inputData.ActiveCursorPool;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputSources.AbstractInputSource;
import org.mt4jx.input.inputData.SkeletonJointEvent;
import org.mt4jx.input.inputSources.mt4jkinect.client.SkeletonMessage;
import org.mt4jx.input.inputSources.mt4jkinect.client.UDPKinectReceiver;
import org.mt4jx.input.inputSources.mt4jkinect.client.UDPKinectRecieverListener;

public class UDPKinectInputSource extends AbstractInputSource implements UDPKinectRecieverListener {
	private UDPKinectReceiver receiver;
	
	private Hashtable<Integer, HashSet<Integer>> userIdToAvailableJointIds = new Hashtable<Integer, HashSet<Integer>>();
	private Hashtable<String, InputCursor> skeletonJointStringToCursor = new Hashtable<String, InputCursor>();
	public UDPKinectInputSource(MTApplication app){
		super(app);
		System.out.println("UDPKinectInputSource()");
		this.receiver = new UDPKinectReceiver();
		this.receiver.connect();
		this.receiver.addListener(this);
		System.out.println("UDPKinectInputSource is listening.");
	}
	@Override
	public void skeletonMessageReceived(SkeletonMessage sm) {
		System.out.println("skeletonMessageReceived()");
		Hashtable<Integer, float[]> jointPositionTable = sm.getJointPositionTable();
		Integer[] jointIds = jointPositionTable.keySet().toArray(new Integer[jointPositionTable.size()]);
		Arrays.sort(jointIds); // return ordered
		
		ArrayList<SkeletonJointEvent> events = new ArrayList<SkeletonJointEvent>();
		
		for (int i = 0; i < jointIds.length; i++) {
			float [] xyz = jointPositionTable.get(jointIds[i]);
			boolean isAvailableJointId = this.isAvailableJointId(sm.getUserId(), jointIds[i]);
			int eventId;
			InputCursor cursor = getCursor(sm.getUserId(), jointIds[i]);
			if(isAvailableJointId){
				eventId = SkeletonJointEvent.INPUT_UPDATED;
			}else{
				eventId = SkeletonJointEvent.INPUT_DETECTED;
				ActiveCursorPool.getInstance().putActiveCursor(cursor.getId(), cursor);
			}
			rememberAvailableJointIds(sm.getUserId(), jointIds[i]);
			
			SkeletonJointEvent se =  new SkeletonJointEvent(this, xyz[0], xyz[1], -1f*xyz[2], eventId, sm.getSequenceId(), sm.getUserId()); 
			System.out.println("enque " + se);
			this.enqueueInputEvent(se);
		}
		//TODO: cleanup lost cursors, create SKELETON_JOINT_LOST Events
	}
	
	private void rememberAvailableJointIds(int userId, int JointId){
		HashSet<Integer> availableJointIds = this.userIdToAvailableJointIds.get(userId);
		if(availableJointIds==null){
			availableJointIds = new HashSet<Integer>();
		}
		availableJointIds.add(JointId);
		this.userIdToAvailableJointIds.put(userId, availableJointIds);
	}
	private boolean isAvailableJointId(int userId, int jointId){
		HashSet<Integer> availableJointIds = this.userIdToAvailableJointIds.get(userId);
		if(availableJointIds==null){
			return false;
		}else{
			return availableJointIds.contains(jointId);
		}
	}
	private String getSkeletonJointString(int userId, int jointId){
		return "SkeletonJoint " + userId + "." + jointId;
	}
	private InputCursor getCursor(int userId, int jointId){
		String key = this.getSkeletonJointString(userId, jointId);
		InputCursor cursor = this.skeletonJointStringToCursor.get(key);
		if(cursor==null){
			cursor = new InputCursor();
			this.skeletonJointStringToCursor.put(key, cursor);
		}
		return cursor;
	}
}
