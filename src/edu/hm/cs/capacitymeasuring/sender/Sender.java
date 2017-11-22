package edu.hm.cs.capacitymeasuring.sender;

import java.io.IOException;

public interface Sender {
	
	void send(byte[] data) throws IOException;
}
