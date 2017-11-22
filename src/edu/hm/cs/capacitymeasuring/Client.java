package edu.hm.cs.capacitymeasuring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

public class Client {

	private static final String ASKFORIP = "Bitte geben Sie die IP-Adresse des Servers ein: ";
	private static final String NOTICETHATTHEIPWASINVALID = "Es wurde keine g�ltige IP-Adresse eingegen!";
	private static final String ASKFORPORT = "Bitte geben Sie den Port des Servers ein: ";
	private static final String INVALIDPORTNUMBERENTERED = "Die eingegebene Portnummer ist ung�ltig!";
	private static final String ASKHOWLONGTOSEND = "Wie viele Millisekunden soll gesendet werden? ";
	private static final String ASKHOWOFTENTODELAY = "Alle wie viele Packete soll ein Verz�gerung eingef�gt werden? ";
	private static final String NO_OR_NEGATIVENUMBERENTERED = "Die Eingabe war weder eine positive Zahl noch \"0\"!";
	private static final String ASKDELAYLENGHT = "Wie viele Millisekunden soll verz�gert werden? ";
	private static final String REACHEDSPEED = "Es wurde eine Datenrate von %f B/s erreicht.";
	
	
	private static int sentPackages = 0;
	
	private static SocketAddress socketaddress;
	private static int sendtime_millisec = -1;
	private static int delaydistance = -1;
	private static int delay_millisec = -1;
	
	public static void main(String[] args) {
		readParameter();
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.connect(socketaddress);
			long starttime = new Date().getTime();
			while (sendtime_millisec > new Date().getTime() - starttime) {
				socket.send(new Package().getDatagramPackage());
				sentPackages++;
				
				if (delaydistance != 0 && sentPackages % delaydistance == 0)
					Thread.sleep(delay_millisec);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		System.out.printf(REACHEDSPEED, calculateSpeed());
	}
	
	private static float calculateSpeed() {
		return ((Package.DATASIZE + Package.SEQUENZNUMBERSIZE) * sentPackages) / (float)sendtime_millisec;
	}
	
	private static void readParameter() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader (System.in ))) {
			InetAddress ip = null;
			System.out.print(ASKFORIP);
			while (ip == null) {
				try {
					ip = InetAddress.getByName(reader.readLine());
				} catch (UnknownHostException e) {
					System.out.println(NOTICETHATTHEIPWASINVALID);
					System.out.print(ASKFORIP);
				}
			} 
			
			int port = -1;
			System.out.print(ASKFORPORT);
			while (port < 0 || port > 65535) {
				try {
					port = Integer.parseInt(reader.readLine());
					if (port < 0 || port > 65535) {
						throw new NumberFormatException("A negative number or number lager then 65535 was entered!");
					}
				} catch (NumberFormatException e) {
					System.out.println(INVALIDPORTNUMBERENTERED);
					System.out.print(ASKFORPORT);
				}
			} 
			socketaddress = new InetSocketAddress(ip, port);
			
			
			System.out.print(ASKHOWLONGTOSEND);
			while (sendtime_millisec < 0) {
				try {
					sendtime_millisec = Integer.parseInt(reader.readLine());
					if (sendtime_millisec < 0) {
						throw new NumberFormatException("A negative number was entered!");
					}
				} catch (NumberFormatException e) {
					System.out.println(NO_OR_NEGATIVENUMBERENTERED);
					System.out.print(ASKHOWLONGTOSEND);
				}
			} 
			
			System.out.print(ASKHOWOFTENTODELAY);
			while (delaydistance < 0) {
				try {
					delaydistance = Integer.parseInt(reader.readLine());
					if (delaydistance < 0) {
						throw new NumberFormatException("A negative number was entered!");
					}
				} catch (NumberFormatException e) {
					System.out.println(NO_OR_NEGATIVENUMBERENTERED);
					System.out.print(ASKHOWOFTENTODELAY);
				}
			} 
			
			System.out.print(ASKDELAYLENGHT);
			while (delay_millisec < 0) {
				try {
					delay_millisec = Integer.parseInt(reader.readLine());
					if (delay_millisec < 0) {
						throw new NumberFormatException("A negative number was entered!");
					}
				} catch (NumberFormatException e) {
					System.out.println(NO_OR_NEGATIVENUMBERENTERED);
					System.out.print(ASKDELAYLENGHT);
				}
			} 
			
		} catch (IOException ex) {
			System.out.println("Beim Einlesen der Parameter ist ein Fehler aufgetreten. Es werden die Stanardwerte benutzt!");
			delay_millisec = 300;
			delaydistance = 10;
		}
	}

}