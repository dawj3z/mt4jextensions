package laufs.commons.udpmulticastclient;

public interface UDPMulticastClientListener {
	public void packageReceived(byte[] bytes);
	public void clientStartedListening();
	public void clientStoppedListening();
}
