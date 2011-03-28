package org.mt4j.input.inputSources;

import java.util.Hashtable;

import org.tuio4j.TuioClient;
/**
 * See license.txt for license information.
 * @author Uwe Laufs
 * @version 1.0
 */
public class TUIOClientManager {
	private Hashtable<Integer, TuioClient> portAndClient = new Hashtable<Integer, TuioClient>();
	private static TUIOClientManager instance;
	
	private TUIOClientManager(){
	}
	public static synchronized TUIOClientManager getInstance(){
		if(instance==null){
			instance = new TUIOClientManager();
		}
		return instance;
	}
	public synchronized TuioClient getClient(){
		return getClient(3333);
	}
	public synchronized TuioClient getClient(int port){
		TuioClient client = this.portAndClient.get(port);
		if(client == null){
			client = new TuioClient(port);
			System.out.println("TUIO client connected: port " + port);
			this.portAndClient.put(port, client);
		}else{
//			System.out.println("returned existing TUIO client.");
		}
		return client;
	}
}

