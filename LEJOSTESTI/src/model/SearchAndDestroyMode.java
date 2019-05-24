package model;

import lejos.robotics.subsumption.Behavior;
/**
 * 
 * SearchAndDestroyMode-class one of the robot's behavioral modes, and
 * as such it implements Behavior interface. In this mode the robot searches
 * for an IR signal originating from a beacon (remote controller on frequency 1).
 * Once the signal has been found, the robot starts tracking in an effort to get a
 * target lock. Once the target (beacon) has been successfully locked, the robot
 * fires a three round burst at it.  
 * 
 * @author Torsti
 *
 */

public class SearchAndDestroyMode implements Behavior{
	private boolean[] targetAcquired;
	private volatile boolean suppressed = false;
	private Tower tower;
	private final int[] elevationLimits;
	private final float angleCorrection;
	//References for samples in list samples[] in action method
	private final static int HORIZONTALSAMPLE = 0, VERTICALSAMPLE = 1;
	//References for target status in list targetAcquired[]
	private final static int NEWTARGET = 0, HORIZONTALLOCK = 1, VERTICALLOCK =2;
	//References for extreme vertical angles in list elevationLimits[]
	private final static int MINANGLE = 0, MAXANGLE = 1;
	
	/**
	 * Constructor creates a list used for locking the target, sets safe elevation limits
	 * for the tower, and angle correction on horizontal plane (due to the sensors' location in
	 * felation to the main gun)
	 * @param tower
	 */
	public SearchAndDestroyMode(Tower tower) {
		this.tower = tower;
		targetAcquired = new boolean[] {false, false, false};
		elevationLimits = new int[] {0, 180};
		angleCorrection = 0.5f;
	}
	
	/**
	 * Determines the situations in which the behaviour takes control of the robot.
	 * Requires to be switched on from the remote client
	 */
	@Override
	public boolean takeControl() {
		return tower.getSnDMode();
	}

	/**
	 * The tower turns clockwise at predetermined speed while, also, taking readings with both of its IR sensors (samples[][])
	 * Once a new target has been acquired the first item on targetAcquired-list is set to true, and tracking begins. Once horizontal
	 * and vertical locks have been acquired, the second and third items' values are set to true respectively. The tower engages its target
	 * and fires a burst of 3 rounds before resetting targetAquired-list's items to false. If the target survives the initial burst,
	 * the robot keeps firing until the target is destroyed. 
	 * 
	 */
	@Override
	public void action() {
		this.suppressed = false;
		tower.resetVerticalTacho();
		System.out.println("Search n Destroy!!");
		float[][] samples = new float[][] {tower.getSensorSample(Tower.HORIZONTALANGLE), tower.getSensorSample(Tower.VERTICALANGLE)};
		
		while(!suppressed && tower.getSnDMode()) {
			
			if(!targetAcquired[NEWTARGET]) {
				tower.azimuthTurn(Tower.CLOCKWISE, Tower.MEDIUMSPEED);
			}
			//Search for targets on horizontal plane
			if(tower.getSensorSample(Tower.HORIZONTALANGLE)[Tower.ANGLETOBEACON] < samples[HORIZONTALSAMPLE][Tower.ANGLETOBEACON] - angleCorrection ||tower.getSensorSample(Tower.HORIZONTALANGLE)[Tower.ANGLETOBEACON] > samples[HORIZONTALSAMPLE][Tower.ANGLETOBEACON] + angleCorrection) {
				samples[HORIZONTALSAMPLE] = tower.getSensorSample(Tower.HORIZONTALANGLE);
				targetAcquired[NEWTARGET] = true;
			}
			//Search for new targets on vertical plane
			if(tower.getSensorSample(Tower.VERTICALANGLE)[Tower.ANGLETOBEACON] < samples[VERTICALSAMPLE][Tower.ANGLETOBEACON] - angleCorrection ||tower.getSensorSample(Tower.VERTICALANGLE)[Tower.ANGLETOBEACON] > samples[VERTICALSAMPLE][Tower.ANGLETOBEACON] + angleCorrection) {
				samples[VERTICALSAMPLE] = tower.getSensorSample(Tower.VERTICALANGLE);
				targetAcquired[NEWTARGET] = true;
			}
			
			//Track target on horizontal plane
			if(samples[HORIZONTALSAMPLE][Tower.ANGLETOBEACON] < -7f && targetAcquired[NEWTARGET]) {
				tower.azimuthTurn(Tower.COUNTERCLOCKWISE, Tower.SLOWSPEED);;
				targetAcquired[HORIZONTALLOCK] = false;
			}else if(samples[HORIZONTALSAMPLE][Tower.ANGLETOBEACON] > -4f && targetAcquired[NEWTARGET]) {
				tower.azimuthTurn(Tower.CLOCKWISE, Tower.SLOWSPEED);
				targetAcquired[HORIZONTALLOCK] = false;
			}else if (targetAcquired[NEWTARGET]) {
				tower.azimuthStop();
				targetAcquired[HORIZONTALLOCK] = true;
			}
			
			//Track target on vertical plane
			if(samples[VERTICALSAMPLE][Tower.ANGLETOBEACON] < -1 && tower.getVerticalTacho() < elevationLimits[MAXANGLE] && targetAcquired[NEWTARGET]) {
				tower.elevationChange(Tower.UP, Tower.SLOWSPEED);
				targetAcquired[VERTICALLOCK] = false;
			}else if(samples[VERTICALSAMPLE][Tower.ANGLETOBEACON] > 1 && tower.getVerticalTacho() > elevationLimits[MINANGLE] && targetAcquired[NEWTARGET]) {
				tower.elevationChange(Tower.DOWN, Tower.SLOWSPEED);
				targetAcquired[VERTICALLOCK] = false;
			}else if(targetAcquired[NEWTARGET]){
				tower.elevationStop();
				targetAcquired[VERTICALLOCK] = true;
			}
			
			//Fire at the target once it has been locked
			if(targetAcquired[HORIZONTALLOCK] && targetAcquired[VERTICALLOCK]) {
				tower.burstFire();
				for(boolean temp : targetAcquired) {
					temp = false;
				}
			}
		}
	}
	/**
	 * The method used to suppress SearchAndDestroyMode-beviour
	 */
	@Override
	public void suppress() {
		this.suppressed = true;
	}
}