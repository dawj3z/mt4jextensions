package org.mt4jx.input.inputSources.mt4jkinect.client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import laufs.commons.udpmulticastclient.UDPMulticastClient;
import laufs.commons.udpmulticastclient.UDPMulticastClientListener;

public class UDPKinectReceiver implements UDPMulticastClientListener {

	private UDPMulticastClient client;
	boolean connectDesired = false;
	long currentSequenceId = -1;
	ArrayList<UDPKinectRecieverListener> listeners = new ArrayList<UDPKinectRecieverListener>();
	
	public UDPKinectReceiver(String ip, int port){
		this.client = new UDPMulticastClient(ip, port);
		this.client.addListener(this);
	}
	public UDPKinectReceiver(){
		this("230.0.0.1", 6666);
	}
	public void connect(){
		this.connectDesired = true;
		this.client.startListen();
	}
//	public void disconnect(){
//		this.connectDesired = false;
//		this.client.stopListen();
//	}
	public boolean isConnected(){
		return this.client.isListening();
	}
	public void addListener(UDPKinectRecieverListener ukrl){
		if(!this.listeners.contains(ukrl)){
			this.listeners.add(ukrl);
		}
	}
	@Override
	public void packageReceived(byte[] msgBytes) {
		String msg = new String(msgBytes, 0, msgBytes.length);
		if(msg.startsWith("SKELETON")){
			try {
				SkeletonMessage sm = parseSkeletonMessage(msg);
				if(sm.getSequenceId()>this.currentSequenceId){
					for (int i = 0; i < this.listeners.size(); i++) {
						this.listeners.get(i).skeletonMessageReceived(sm);
					}
					this.currentSequenceId = sm.getSequenceId();
				}else{
					System.out.println("Message Drop (late receive)");
				}
//				System.out.println(sm.getSequenceId());
//				if(sm.getSequenceId()%30==0){
//					System.out.println(msg);
//				}
			} catch (ParseMessageException e) {
				System.out.println("Message Drop (invalid)");
				System.out.println(msg);
			}
		}
	}
	@Override
	public void clientStartedListening() {
		System.out.println("clientStartedListening");
	}
	//TODO: fix and test stop/restart
	@Override
	public void clientStoppedListening() {
//		System.out.println("clientStoppedListening");
//		while(!this.client.isListening() && this.connectDesired){
//			System.out.println("...auto reconnect...");
//			this.client.stopListen();
//			this.client.startListen();
//			try {
//				Thread.sleep(2500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	private SkeletonMessage parseSkeletonMessage(String msg) throws ParseMessageException {
		try {
//			System.out.println("msg:" + msg);
			StringTokenizer lineSplitter = new StringTokenizer(msg, "|", false);
			String header = lineSplitter.nextToken();
			StringTokenizer columnSplitter = new StringTokenizer(header, ",", false);
			String msgType = columnSplitter.nextToken();
			long sequenceId = Long.parseLong(columnSplitter.nextToken());
			int userId = Integer.parseInt(columnSplitter.nextToken());
			
			// parse joints
			Hashtable<Integer, float[]> jointPositionTable = new Hashtable<Integer, float[]>();
			
			while(lineSplitter.hasMoreTokens()){
				String currentLine = lineSplitter.nextToken();
				columnSplitter = new StringTokenizer(currentLine, ",", false);
				if(columnSplitter.countTokens()==4){
					int jointId = Integer.parseInt(columnSplitter.nextToken());
					float[] position = new float[3];
					position[0] = Float.parseFloat(columnSplitter.nextToken());
					position[1] = Float.parseFloat(columnSplitter.nextToken());
					position[2] = Float.parseFloat(columnSplitter.nextToken());
					jointPositionTable.put(jointId, position);
				}else{
					break; // udp fill stuff ?
				}
			}
			SkeletonMessage skelMsg = new SkeletonMessage(sequenceId, userId, jointPositionTable);
			return skelMsg;
		} catch (Exception e) {
			throw new ParseMessageException(e);
		}
	}
//    float raw_depth_to_meters(int raw_depth)
//    {
//      if (raw_depth < 2047)
//      {
//       return 1.0 / (raw_depth * -0.0030711016 + 3.3309495161);
//      }
//      return 0;
//    }

}
