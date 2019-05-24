package model;

import java.io.IOException;

import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;
/**
 * UserMode class implements lejos Behavior interface.
 * It stops other modes, and gives control of guard tower
 * to the user.
 * @author Joonas Taivalmaa, Patrik Heinonen
 *
 */
public class UserMode implements Behavior {
	
	private Tower tower;
	private volatile boolean suppressed = false;
	
	/**
	 * This is the constructor method
	 * @param tower is the guard tower
	 */
	public UserMode(Tower tower) {
		this.tower = tower;
	}

	@Override
	/**
	 * This method reports to arbitrator if it wants to take control
	 * It returns the value from towers usermode boolean. It can be set
	 * by DataThread class.
	 */
	public boolean takeControl() {
		return tower.getUsermode();
	}
	
	@Override
	/**
	 * This method stops motors, reports to controlling device and
	 * waits for user input. Depending on input, it either fires, turns,
	 * sets elevation or retuns from usermode.
	 */
	public void action() {
		
		tower.azimuthStop();
		tower.setUserFire(false);
		tower.setBurstfire(false);
		tower.setNextAngle(0);
		//lähettää Appiin tiedon että usermode on päällä
		
			
			tower.sendLine("usermodeOn");
		
		System.out.println("Usermode on");
		while (!suppressed && tower.getUsermode()) {
			if (tower.getNextAngle() != 0) {
				tower.azimuthAngleTurn(tower.getNextAngle());
				tower.setNextAngle(0);
			}
			if (tower.getUserFire()) {
				tower.fire();
				tower.setUserFire(false);
			}
			if (tower.getBurstfire()) {
				tower.burstFire();
				tower.setBurstfire(false);
			}
		}
		
			tower.sendLine("usermodeOff");
		
	}

	@Override
	/**
	 * This method suppresses the usermode behavior.
	 */
	public void suppress() {
		
		this.suppressed = true;
		
	}
	
}
