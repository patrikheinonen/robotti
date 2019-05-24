package model;


import java.io.IOException;
import view.DefaultViewController;

/**
 * DataThread class is ran in background and it listens to input from
 * controlling device. It has several operations depending on input.
 * 
 * @author Joonas Taivalmaa, Patrik Heinonen, Paavo Mattila
 *
 */
public class DataThread extends Thread {

	private DataInterface inter;

	private String message;
	/**
	 * This boolean tells when the UserMode is on. Used for restricting fire
	 * control.
	 */
	boolean usermodeOn = false;

	private boolean done = false;

	/**
	 * This is the constructor method.
	 * 
	 * @param inter is the Datainterface.
	 */
	public DataThread(DataInterface inter) {
		this.inter = inter;

	}

	/**
	 * Run method is started in main class. It waits for user input, parses it and
	 * performs operations.
	 */
	public void run() {

		while (!done) {

			try {

				message = inter.readLine();
				System.out.println(message);

			} catch (IOException e) {

				e.printStackTrace();
			}

			switch (message) {
			// lukee robotilta tulevat komennot. DataThread-luokka lukee komennot ja
			// asettaa arvot.
			case "usermodeOn":
				usermodeOn = true;
				break;
			case "usermodeOff":
				usermodeOn = false;
				break;

			default:
				try {

					if (message.charAt(0) == 'i') {
						message = message.replace("i", "");
						DefaultViewController.setRobotAngle(Integer.parseInt(message));
					}

				} catch (Exception e) {

				}
			}

		}

	}

	public boolean getUsermodeOn() {
		return usermodeOn;
	}
}
