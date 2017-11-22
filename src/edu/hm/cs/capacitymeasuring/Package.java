package edu.hm.cs.capacitymeasuring;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Package {
	
	public final static int SEQUENZNUMBERSIZE = 2;
	public final static int DATASIZE = 1398;
	
	private static short nextsequenznumber = 32767;
	private final short sequenznumber;
	private final byte[] data;
	
	public Package() {
		this(new byte[0]);
	}
	
	public Package(byte[] data) {
		sequenznumber = nextsequenznumber++;
		
		if (data.length <= 1398)
			this.data = Arrays.copyOf(data, DATASIZE);
		else
			throw new IllegalArgumentException("The message is to long. It is possible to send up to " + DATASIZE + " Bytes.");
	}
	
	public DatagramPacket getDatagramPackage() {
		ByteBuffer buffer = ByteBuffer.allocate(SEQUENZNUMBERSIZE + DATASIZE);
		buffer.putShort(sequenznumber);
		buffer.put(data);
		return new DatagramPacket(buffer.array(), SEQUENZNUMBERSIZE + DATASIZE);
	}
}
