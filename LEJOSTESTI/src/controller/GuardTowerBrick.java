package controller;

import model.*;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class GuardTowerBrick {

	public static void main(String[] args) {

		// Alustetaan Torni
		Tower tower = new Tower();
		DataThread reader = new DataThread(tower);
		/*
		 * A: gunMotor
		 * B: elevationMotor
		 * C: azimuthMotor
		 * D: loadMotor
		 * S1: radarg
		 */

		LineMap lineMap = new LineMap();

		Behavior scanning = new Scanning(tower);
		Behavior launch = new Launch(tower);
		Behavior stop = new Stop(tower);
		Behavior userMode = new UserMode(tower);
		Behavior snd = new SearchAndDestroyMode(tower);

		System.out.println("Torni luotu");
		
		

		tower.startMotors();

		

		//setElevation metodin parametrina 200 vitun bonari.

		
		System.out.println("Waiting for connection");
		tower.enableConnection();
		System.out.println("Connected to PC");
		
		
		

		Behavior[] behaviors = new Behavior[] { scanning, launch, snd, userMode, stop };

		Arbitrator arb = new Arbitrator(behaviors);
		
		reader.start();
		arb.go();
		

		//reader.setDaemon(true);
	}

	
}
