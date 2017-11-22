package edu.hm.cs.capacitymeasuring.sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class UDPSender implements Sender {

	private final DatagramSocket socket;
	
	public UDPSender (SocketAddress socketaddress) throws SocketException {
		socket = new DatagramSocket();
		socket.connect(socketaddress);
	}
	
	
	@Override
	public void send(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.send(packet);
	}

}
