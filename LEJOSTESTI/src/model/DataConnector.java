package model;

import lejos.robotics.filter.Dump;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The DataConnector class creates connection to controlling device.
 * It also contains methods to send and receive String typed data.
 *
 * @author Joonas Taivalmaa
 * @version 1.0
 * @since 18.3.2019
 */
public class DataConnector {
	private ServerSocket serv;
	private Socket s;
	private DataInputStream in;
	private DataOutputStream out;
	private Tower tower;
	private volatile boolean suppressed = false;

	public DataConnector (Tower tower) {
		this.tower = tower;

	}

		/**
		 * This method is used to enable connection to controlling device.
		 * It connects to Serversocket 1111, and reads a handshake string.
		 */
	public void StartConnection() throws IOException{
		serv = new ServerSocket(1111);
		s = serv.accept();
		in = new DataInputStream(s.getInputStream());
		out = new DataOutputStream(s.getOutputStream());
		System.out.println(((DataInputStream) in).readUTF());


	}


		/**
		 * This method sends String-typed data to connected device.
		 * @param dataString This string will be sent to the connected device.
		 * @exception IOException
		 */
	public void sendUTF(String dataString) throws IOException {
		out.writeUTF(dataString);

	}
		/**
		 * This method reads String-typed data from the connected device
		 * @returns String sent from device
		 * @exception IOException
		 */
	public String read() throws IOException {
		return (((DataInputStream) in).readUTF());

	}


}
