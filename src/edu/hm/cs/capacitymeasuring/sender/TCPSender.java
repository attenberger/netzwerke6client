package edu.hm.cs.capacitymeasuring.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class TCPSender implements Sender {

	private final Socket socket;
	private final OutputStream writer;
	
	public TCPSender(SocketAddress socketaddress) throws IOException {
		socket = new Socket();
		socket.connect(socketaddress);
		writer = socket.getOutputStream();
	}
	
	@Override
	public void send(byte[] data) throws IOException {
		writer.write(data);
		writer.flush();
	}
	
	@Override
	public void finalize() throws IOException  {
		if (socket != null && !socket.isClosed())
			socket.close();
	}

}
