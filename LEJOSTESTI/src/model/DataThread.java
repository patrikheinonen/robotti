package model;
/**
 * DataThread class is ran in background and it listens
 * to input from controlling device. It has several operations
 * depending on input.
 * @author Joonas Taivalmaa, Patrik Heinonen, Paavo Mattila
 *
 */
public class DataThread extends Thread {
	
	private Tower tower;
	private String line;
	private boolean done = false;
	/**
	 * This is the constructor method.
	 * @param tower is the Guard Tower.
	 */
	public DataThread(Tower tower) {
		this.tower = tower;
	}
	/**
	 * Run method is started in main class.
	 * It waits for user input, parses it and performs operations.
	 */
	public void run() {
		while (!done) {
			line = tower.readLine();
			switch (line) {
			// lukee käyttöliittymältä tulevat komennot. DataThread-luokka lukee komennot ja asettaa arvot.
			case "usermode": tower.setUsermode(true); break;
			case "scannermode": tower.setScannermode(); break;
			case "sndmode": tower.setSnDMode(true); break;
			case "fire": tower.setUserFire(true); break;
			case "stop": tower.setStopCommand(true); break;
			case "burstfire": tower.setBurstfire(true); break;
			
			
			default: try {
				if (line.charAt(0) == 'e') {					// jos käyttöliittymältä tulee
					line = line.replace("e", "");				// merkkijono, joka on e-alkuinen
					tower.setElevation(Integer.parseInt(line));	// se tulkitaan korkeusasteena. Tämän tulee olla
				}												// arvoltaan 0 - 200. Esim "e120"
				else tower.azimuthAngleTurn(Integer.parseInt(line));
			} catch (Exception e) {
				tower.setNextAngle(0);}
			}
			System.out.println(line);
		}
	}

}
