package edu.hm.cs.capacitymeasuring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.hm.cs.capacitymeasuring.sender.Sender;
import edu.hm.cs.capacitymeasuring.sender.TCPSender;
import edu.hm.cs.capacitymeasuring.sender.UDPSender;

public class Client {

	private static final String ASKTCP_UDP = "Soll TCP oder UDP verwendet werden? ";
	private static final String NOTTCPORUDPENTERED = "Es wurde weder \"TCP\" noch \"UDP\" eingegeben!";
	private static final String ASKFORIP = "Bitte geben Sie die IP-Adresse des Servers ein: ";
	private static final String NOTICETHATTHEIPWASINVALID = "Es wurde keine gültige IP-Adresse eingegen!";
	private static final String ASKFORPORT = "Bitte geben Sie den Port des Servers ein: ";
	private static final String INVALIDPORTNUMBERENTERED = "Die eingegebene Portnummer ist ungültig!";
	private static final String ASKHOWLONGTOSEND = "Wie viele Millisekunden soll gesendet werden? ";
	private static final String ASKHOWOFTENTODELAY = "Alle wie viele Packete soll ein Verzögerung eingefügt werden? ";
	private static final String NO_OR_NEGATIVENUMBERENTERED = "Die Eingabe war weder eine positive Zahl noch \"0\"!";
	private static final String ASKDELAYLENGHT = "Wie viele Millisekunden soll verzögert werden? ";
	private static final String REACHEDSPEED = "Es wurde eine Datenrate von %f B/s erreicht. Des wurden %d Packete gesendet.";
	

	
	private static Sender sender = null;
	private static int sendtime_millisec = -1;
	private static int delaydistance = -1;
	private static int delay_millisec = -1;
	
	public static void main(String[] args) {
		readParameter();
		int sentPackages = 0;
		long starttime = 0;
		long stoptime = 0;
		try {
			starttime = new Date().getTime();
			while (sendtime_millisec > new Date().getTime() - starttime) {
				byte[] binaryMessage = new Package().getMessage();
				sender.send(binaryMessage);
//				System.out.println(Arrays.toString(binaryMessage));
				sentPackages++;
				
				if (delaydistance != 0 && sentPackages % delaydistance == 0)
					Thread.sleep(delay_millisec);
			}
			stoptime = new Date().getTime();
		} catch (IOException e) {
			System.out.println("Fehler beim Senden der Daten! " + e.getMessage());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		System.out.printf(REACHEDSPEED, calculateSpeed(sentPackages, starttime, stoptime), sentPackages);
	}
	
	private static float calculateSpeed(int sentpackages, long starttime, long stoptime) {
		return ((Package.DATASIZE + Package.SEQUENZNUMBERSIZE) * sentpackages) / (float) ((stoptime - starttime) / 1000);
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
			SocketAddress socketaddress = new InetSocketAddress(ip, port);
			
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
			
			System.out.print(ASKTCP_UDP);
			while (sender == null) {
				String input = reader.readLine();
				if (input.equals("UDP")) {
					sender = new UDPSender(socketaddress);
				} else if (input.equals("TCP")) {
					sender = new TCPSender(socketaddress);
				} else {
					System.out.println(NOTTCPORUDPENTERED);
					System.out.print(ASKTCP_UDP);
				}
			} 
			
		} catch (IOException ex) {
			System.out.println("Beim Einlesen der Parameter ist ein Fehler aufgetreten. Es werden die Stanardwerte benutzt!");
			delay_millisec = 300;
			delaydistance = 10;
		}
	}

}
