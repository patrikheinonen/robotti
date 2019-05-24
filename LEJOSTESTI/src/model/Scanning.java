package model;

import lejos.robotics.subsumption.Behavior;
import java.util.Random;
/**
 * Scanning class implements Behavior interface.
 * Scanning is the default tower behavior. 
 * @author Joonas Taivalmaa, Patrik Heinonen
 *
 */
public class Scanning implements Behavior {
	
	Tower tower;
	private volatile boolean suppressed = false;
	
	public Scanning (Tower tower) {
		this.tower = tower;
	}
	@Override
	/**
	 * This method determines the situations, where scanning behavior
	 * wants to take control. It always returns true.
	 * @returns boolean true
	 */
	public boolean takeControl() {
		return true;
	}

	@Override
	/**
	 * This method makes the tower spin in clockwise direction.
	 */
	public void action() {
		this.suppressed = false;
		while(!suppressed) { 
			tower.azimuthTurn(Tower.CLOCKWISE, Tower.MEDIUMSPEED);;			
			
		}
	}

	@Override
	/**
	 * This method suppresses scanning behavior.
	 */
	public void suppress() {
		
		this.suppressed = true;
		
	}
	
	

}
