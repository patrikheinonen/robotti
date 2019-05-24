package model;

import lejos.robotics.subsumption.Behavior;
/**
 * Launch class implements lejos Behavior interface. It activates
 * when IR-sensor detects an anomaly, and launches a single projectile
 * towards it.
 * @author Joonas Taivalmaa, Patrik Heinonen, Torsti Laine
 *
 */
public class Launch implements Behavior {
	
	Tower tower;
	private volatile boolean suppressed = false;
	/**
	 * This is the constructor method. It takes tower as a parameter.
	 * @param tower is the guard tower.
	 */
	public Launch (Tower tower) {
		this.tower = tower;
	}

	@Override
	/**
	 * This method reports to arbitrator if it wants to take control. It returns true, if IR-sensor
	 * returns a float with value less than 50.0.
	 * @returns boolean 
	 */
	public boolean takeControl() {
		if (this.tower.getSensorSample(Tower.HORIZONTALDISTANCE)[Tower.DISTANCETOTARGET] < 50.0f) {
			return true; 
		}
		else return false;
	}

	@Override
	/**
	 * When this method activates, Guard tower will stop and fire. After
	 * firing, It will continue turning.
	 */
	public void action() {
		this.suppressed = false;
		tower.azimuthStop();
		tower.fire();
		tower.azimuthTurn(Tower.CLOCKWISE, Tower.MEDIUMSPEED);

	}

	@Override
	/**
	 * This method suppresses the launch behavior
	 */
	public void suppress() {
		this.suppressed = true;
	}

}
