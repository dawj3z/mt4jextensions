package laufs.commons.udpmulticastclient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class UDPMulticastClient {
	private MulticastSocket socket;
	private String ip="230.0.0.1";
	private int port=6666;
	private boolean stopFlag = false;
	private boolean isListening = false;
	private ArrayList<UDPMulticastClientListener> listeners = new ArrayList<UDPMulticastClientListener>();
	
	public UDPMulticastClient(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	public UDPMulticastClient(){
	}
	public void startListen(){
		System.out.println("startListen()");
		try {
			this.socket = new MulticastSocket(port);
			InetAddress address = InetAddress.getByName(ip);
			socket.joinGroup(address);
			socket.setLoopbackMode(false);
			this.isListening = true;
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		ListenerThread listenerThread = new ListenerThread();
		listenerThread.start();
	}
//	public void stopListen() {
//		try {
//			setStopFlag(true);
//			this.socket.leaveGroup(InetAddress.getByName(ip));
//			this.socket.disconnect();
//			this.socket.close();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	public void addListener(UDPMulticastClientListener listener){
		if(!this.listeners.contains(listener)){
			this.listeners.add(listener);
		}
	}
	private void setStopFlag(boolean stopFlag){
		if(this.stopFlag!=stopFlag){
			this.stopFlag = stopFlag;
			for (int i = 0; i < listeners.size(); i++) {
				UDPMulticastClientListener l = listeners.get(i);
				if (stopFlag) {
					this.isListening = false;
					l.clientStoppedListening();
				}else{
					this.isListening = true;
					l.clientStartedListening();
				}
			}
		}
	}
    class ListenerThread extends Thread {
		@Override
		public void run() {
//			System.out.println("Started ListenerThread");
	        byte[] buf = new byte[512];
	        DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				while(!stopFlag){
					socket.receive(packet);
					if(!stopFlag){
						for (int i = 0; i < listeners.size(); i++) {
							listeners.get(i).packageReceived(packet.getData());
						}
					}
				}
			} catch (IOException e) {
				setStopFlag(true);
			}
			packet.setLength(buf.length);
//			System.out.println("Stopped ListenerThread");
		}
    }
	public boolean isListening() {
		return isListening;
	}
}
