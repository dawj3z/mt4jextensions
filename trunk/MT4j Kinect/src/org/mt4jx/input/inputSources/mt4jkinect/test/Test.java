package org.mt4jx.input.inputSources.mt4jkinect.test;

import org.mt4jx.input.inputSources.mt4jkinect.client.SkeletonMessage;
import org.mt4jx.input.inputSources.mt4jkinect.client.UDPKinectReceiver;
import org.mt4jx.input.inputSources.mt4jkinect.client.UDPKinectRecieverListener;

public class Test {
	public static void main(String[] args) {
		UDPKinectReceiver r = new UDPKinectReceiver();
		System.out.println("...connect");
		r.connect();
		System.out.println("is connected: " + r.isConnected());
		System.out.println("....waiting for messages");
		r.addListener(new UDPKinectRecieverListener() {
			@Override
			public void skeletonMessageReceived(SkeletonMessage sm) {
				System.out.println("received SkeletonMessage: " + sm);
			}
		});
	}
}
