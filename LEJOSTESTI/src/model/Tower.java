package model;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.UARTSensor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

import java.io.IOException;

import lejos.hardware.lcd.LCD;

/**
 * Tower class is the software manifest of physical Guard Tower.
 * Tower class contains the basic motor and sensor assimilations and base methods for
 * moving mechanical parts.
 * @author Joonas Taivalmaa, Torsti Laine, Patrik Heinonen
 * @version 1.0
 *
 */
public class Tower {


	private RegulatedMotor[] towerMotors;
	private UARTSensor[] towerSensors;
	private SampleProvider[] sampleproviders;
	private boolean[] towerModes;

	private boolean stopCommand = false;
	private DataConnector data;
	private int nextAngle;
	// references for sample data
	public static final int ANGLETOBEACON = 0, DISTANCETOTARGET = 0;
	// references for motor speeds
	public static final int SLOWSPEED = 400, MEDIUMSPEED = 800, HIGHSPEED = 1500;
	// references for modes in list towerModes
	public static final int SCANNER = 0, USER = 1, SND = 2, USERFIRE = 3, STOPCOMMAND = 4, BURSTFIRE = 5;
	// references for sampleproviders in list sampleProviders
	public static final int HORIZONTALDISTANCE = 0, HORIZONTALANGLE = 1, VERTICALANGLE = 2;
	// references for motor turn directions and switches
	public static final boolean CLOCKWISE = true, COUNTERCLOCKWISE = false, UP = true, DOWN = false, ON = true, OFF = false;
	// references for sensors in list towerSensors
	public static final int IRHORIZONTAL = 0, IRVERTICAL = 1;
	// references for motors in list towerMotors
	public static final int GUNMOTOR = 0, ELEVATIONMOTOR = 1, AZIMUTHMOTOR = 2, RELOADMOTOR = 3;
	/**
	 * This is the constructor, which assimilates motors and sensors in their ports.
	 * It also creates a new instance of DataConnector class for itself.
	 */
	public Tower() {
		this.towerMotors = new RegulatedMotor[] { new EV3LargeRegulatedMotor(MotorPort.A),
				new EV3LargeRegulatedMotor(MotorPort.B), new EV3LargeRegulatedMotor(MotorPort.C),
				new EV3MediumRegulatedMotor(MotorPort.D) };
		this.towerSensors = new UARTSensor[] { new EV3IRSensor(SensorPort.S1), new EV3IRSensor(SensorPort.S2) };
		this.sampleproviders = new SampleProvider[] { ((EV3IRSensor) towerSensors[IRHORIZONTAL]).getDistanceMode(),
				((EV3IRSensor) towerSensors[IRHORIZONTAL]).getSeekMode(),
				((EV3IRSensor) towerSensors[IRVERTICAL]).getSeekMode() };
		this.towerModes = new boolean[] { ON, OFF, OFF, OFF, OFF, OFF };

		for (RegulatedMotor motor : towerMotors) {
			motor.resetTachoCount();
		}

		this.data = new DataConnector(this);
	}
	/**
	 * This method is used to turn on scannermode, which is default
	 * mode of operation.
	 */
	public void setScannermode() {
		this.towerModes[USER] = OFF;
		this.towerModes[SND] = OFF;
		this.towerMotors[ELEVATIONMOTOR].rotateTo(0);
	}
	/**
	 * This method starts motors, and sets initial speeds and rotation directions for them.
	 */
	public void startMotors() {
		this.towerMotors[GUNMOTOR].backward();
		this.towerMotors[GUNMOTOR].setAcceleration(250);
		this.towerMotors[GUNMOTOR].setSpeed(800);
		this.towerMotors[AZIMUTHMOTOR].setSpeed(800);
	}

	/**
	 * This method stops all motors.
	 */
	public void stopMotors() {
		for (RegulatedMotor motor : towerMotors) {
			motor.stop(true);
		}
	}
	/**
	 * This method turns tower horizontally in set direction and speed
	 * @param direction is either clockwise(true) or counterclockwise(false)
	 * @param speed is the speed which tower will be rotating.
	 */
	public void azimuthTurn(boolean direction, int speed) {
		if (direction == CLOCKWISE) {
			towerMotors[AZIMUTHMOTOR].forward();
		} else if (direction == COUNTERCLOCKWISE) {
			towerMotors[AZIMUTHMOTOR].backward();
		}
		towerMotors[AZIMUTHMOTOR].setSpeed(speed);
	}
	/**
	 * this method turns tower horizontally by set angle. It also sends the new angle to
	 * controlling device.
	 * @param angle is desired turning. Note that it is not degrees.
	 */
	public void azimuthAngleTurn(int angle) {
		towerMotors[AZIMUTHMOTOR].setSpeed(500);
		towerMotors[AZIMUTHMOTOR].rotate(angle);
		try {
			// i = angle?
			data.sendUTF("i" + ((towerMotors[AZIMUTHMOTOR].getTachoCount() / 11) % 360));
		} catch (IOException e) {
			System.out.println("Shiiiiiiet");
		}
	}
	/**
	 * This method adjusts towers elevation angle.
	 * @param angle must be between 0 and 190 higher angle is more elevated.
	 */
	public void setElevation(int angle) { //
		towerMotors[ELEVATIONMOTOR].setSpeed(400);
		if (angle >= 0 && angle < 190) {
			towerMotors[ELEVATIONMOTOR].rotateTo(angle);
		}
	}
	/**
	 * this method launches projectile. It also sends data about firing incident
	 * to controlling device.
	 */
	public void fire() {
		LCD.clear();
		System.out.println("Target locked, firing");
		try {
			data.sendUTF("Shooting at " + towerMotors[AZIMUTHMOTOR].getTachoCount());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Accelerate to firing speed
		towerMotors[GUNMOTOR].setAcceleration(250);
		towerMotors[GUNMOTOR].setSpeed(HIGHSPEED);
		Delay.msDelay(500);
		// feed ammunition to the launcher and rechamber
		towerMotors[RELOADMOTOR].rotate(-360);
		// return to idle state
		towerMotors[GUNMOTOR].setSpeed(MEDIUMSPEED);
	}
	/**
	 * This method launches three projectiles in rapid succession. Also sends data
	 * about firing incident to controlling device.
	 */
	public void burstFire() {
		try {
			data.sendUTF("Commence burstfire");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			data.sendUTF("BURSTFIRE");
		} catch (IOException e) {
		}
		// Accelerate to firing speed
		towerMotors[GUNMOTOR].setSpeed(HIGHSPEED);
		Delay.msDelay(500);
		// feed ammunition to the launcher and rechamber x3
		towerMotors[RELOADMOTOR].rotate(-1080);
		towerMotors[GUNMOTOR].setSpeed(MEDIUMSPEED);

	}
	/**
	 * This method causes the robot to adjust the vertical angle of its body.
	 * @param direction is desired direction of change(up = true or down = false).
	 * @param speed controls how rapidly the angle changes
	 */
	public void elevationChange(boolean direction, int speed) {
		towerMotors[ELEVATIONMOTOR].setSpeed(speed);
		if (direction == UP) {
			towerMotors[ELEVATIONMOTOR].forward();
		} else if (direction == DOWN) {
			towerMotors[ELEVATIONMOTOR].backward();
		}
	}
	/**
	 * This method stops azimuth motor thus ceasing horizontal movement
	 */
	public void azimuthStop() {
		towerMotors[AZIMUTHMOTOR].stop(true);
	}
	/**
	 * This method stop elevation motor thus ceasing elevation movement
	 */
	public void elevationStop() {
		towerMotors[ELEVATIONMOTOR].stop(true);
	}
	/**
	 * This method shuts down motors. they cannot be used before restart.
	 */
	public void closeMotors() {
		towerMotors[AZIMUTHMOTOR].close();
		towerMotors[GUNMOTOR].close();
		towerMotors[RELOADMOTOR].close();
		towerMotors[ELEVATIONMOTOR].close();
	}
	/**
	 * With this method, one can obtain different sample readings from the robot's IR-sensors.
	 * @param sampletype (0 = distance to target, 1 = horizontal angle to target, 2 = vertical angle to target)
	 * @return float[] sample
	 */
	public float[] getSensorSample(int sampletype) {
		float[] sample = new float[sampleproviders[sampletype].sampleSize()];
		sampleproviders[sampletype].fetchSample(sample, 0);
		return sample;
	}
	/**
	 * This method waits for connection on controlling device
	 * and establishes a connection protocol.
	 */
	public void enableConnection() {
		try {
			data.StartConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * This method waits for input from controlling device, and returns it
	 * @return String control input
	 */
	public String readLine() {
		try {
			return data.read();
		} catch (IOException e) {
			return "";
		}
	}

	// settereitä ja gettereitä
	/**
	 * This method returns tacho count from azimuth motor.
	 * @return int tacho count of azimuth motor.
	 */
	public int getAzimuthTacho() {
		return towerMotors[AZIMUTHMOTOR].getTachoCount();
	}
	/**
	 * This method returns tacho count of elevation motor.
	 * @return int tacho count of elevation motor.
	 */
	public int getElevationTacho() {
		return towerMotors[AZIMUTHMOTOR].getTachoCount();
	}

	public boolean getScannermode() {
		return this.towerModes[SCANNER];
	}

	public void setNextAngle(int angle) {
		nextAngle = angle;
	}

	public int getNextAngle() {
		return nextAngle;
	}

	public boolean getUserFire() {
		return towerModes[USERFIRE];
	}

	public void setUserFire(boolean userFire) {
		this.towerModes[USERFIRE] = userFire;
	}

	public boolean getSnDMode() {
		return this.towerModes[SND];
	}

	// käytetäänkö vain päälle laittamiseen?
	public void setSnDMode(boolean snDmode) {
		this.towerModes[USER] = OFF;
		this.towerModes[SND] = snDmode;
	}

	public void resetVerticalTacho() {
		towerMotors[ELEVATIONMOTOR].resetTachoCount();
	}

	public int getVerticalTacho() {
		return towerMotors[ELEVATIONMOTOR].getTachoCount();
	}

	public void elevationChange(int angle) {
		towerMotors[ELEVATIONMOTOR].rotate(angle);
	}

	public boolean getStopCommand() {
		return this.stopCommand;
	}

	public void setStopCommand(boolean stopCommand) {
		this.stopCommand = stopCommand;
	}

	public boolean getBurstfire() {
		return towerModes[BURSTFIRE];
	}

	public void setBurstfire(boolean burstfire) {
		this.towerModes[BURSTFIRE] = burstfire;
	}

	public boolean getUsermode() {
		return towerModes[USER];
	}
    /**
      * This method takes boolean as parameter. If true, it sets scannermode and towermode
      * booleans off, and usermode on. If false, it sets usermode off.
      * @Param usermode is boolean that determines if desired mode is usermode.
      */
	public void setUsermode(boolean usermode) {
		if (usermode) {
			this.towerModes[SCANNER] = OFF;
			this.towerModes[SND] = OFF;
		}
		this.towerModes[USER] = usermode;
	}

    /**
      * This method sends String to controlling device
      * @param line is string that will be sent.
      */
	public void sendLine(String line) {
		try {
			data.sendUTF(line);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
