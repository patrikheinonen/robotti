package model;

import java.io.DataOutputStream;


import java.io.IOException;
import java.util.Scanner;
import java.io.DataInputStream;

import java.net.Socket;


import lejos.utility.Delay;


/**
* The purpose of this class is to create a connection to the robot.
* it also contains a method to create a connection and methods to send and read strings
* which are later parsed to proper form for example integer. 
* @author Patrik Heinonen, Paavo Mattila, Joonas Taivalmaa.
* @version versionumero
*/ 

public class DataInterface extends Thread {
	private Socket robot;
	private DataOutputStream out;
	private DataInputStream in;
	//private ObjectReader or;
	MapCreator mcrt = new MapCreator();
	//LineMap map = new LineMap(mcrt.getLines(), mcrt.getRectangle());
	
	Scanner input = new Scanner(System.in);
	
	public DataInterface() {
		
	}
	
	
	/**
	 * This method is used to enable connection to the robot.
	 * It creates a connection with the socket 1111.
	 */
	
	public void createConnection() {
		try {
			robot = new Socket("10.0.1.1", 1111);
			out = new DataOutputStream(robot.getOutputStream());
			in = new DataInputStream(robot.getInputStream());
			//or = new ObjectReader(in);
			out.writeUTF("Activating");
			out.flush();
			
			Delay.msDelay(2000);
			

		} catch (Exception e) {
			System.out.println("paska");
		}
	}


	/**
	 * the purpose is to send a string to the robot by first writing it to the 
	 * output stream and then flushing the outputstream
	 * @param teksti, the string that we send to the robot.
	 * 
	 */
	
	public void sendUTF(String teksti) throws IOException {
		out.writeUTF(teksti);
		out.flush();
	}
	
	/**
	 * the purpose is to read a string the robot sends
	 * 
	 * @return it returns the string we read from the inputsteam
	 * 
	 */
	
	public String readLine() throws IOException {
		
		return in.readUTF();
	}
	
	

}
