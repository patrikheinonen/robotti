package model;

import lejos.hardware.Button;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

/**
 * Stop class implements lejos Behavior interface. It stops motors,
 * and exits program.
 * @author Joonas Taivalmaa, Patrik Heinonen
 *
 */
public class Stop implements Behavior {
	
	private volatile boolean suppressed = false;
	
	Tower tower;
	
	/**
	 * This is the constructor method
	 * @param tower is the guard tower.
	 */
	public Stop (Tower tower) {
		this.tower = tower;
	}
    
	@Override
	/**
	 * This method reports to arbitrator if stop behavior wants to
	 * take control. It returns the value of towers stopCommand boolean, which
	 * can be set by user.
	 */
	public boolean takeControl() {
		return tower.getStopCommand();
	}
    
	@Override
	/**
	 * This method stops motors and closes them. Then program will stop running.
	 */
	public void action() {
		System.out.println("lopetetaan");
		tower.stopMotors();
		Delay.msDelay(1000);
		tower.closeMotors();
		System.exit(0);
	}
    
	@Override
	/**
	 * This method suppresses the stop behavior. It is required by interface
	 * but should never be used.
	 */
	public void suppress() {
		this.suppressed = true;

	}

}
