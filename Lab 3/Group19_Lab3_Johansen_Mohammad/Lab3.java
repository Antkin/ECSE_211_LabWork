package ca.mcgill.ecse211.lab3;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import ca.mcgill.ecse211.lab3.OdometerExceptions;

public class Lab3 {
	
	/*Initializing any variables this class may need */
	public static final double WHEEL_RAD = 1.61;
	public static final double TRACK = 18.4;
	public static final double[] positionWaypoints = {1,0,2,1,2,2,0,2,1,1};
	public static boolean obstacle_Avoidance = false;
	
	/*Initializing Motors, and LCD */
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	public static final Port us_Port = LocalEV3.get().getPort("S2");
	
	/**Main Class, runs upon robot start*/
	public static void main(String[] args) throws ca.mcgill.ecse211.lab3.OdometerExceptions {
		int buttonChoice;
		/* Setting up the Odometer, display, and US Sensor */
		Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		
		Display odometryDisplay = new Display(lcd);
		
		@SuppressWarnings("resource")
		SensorModes us_Sensor = new EV3UltrasonicSensor(us_Port);
		final SampleProvider us_Distance = us_Sensor.getMode("Distance");
		
		do {
			/* Hear we clear the display, and set up the menu options we want */
			lcd.clear();
			
			lcd.drawString("< Left | Right>", 0, 0);
			lcd.drawString("       |       ", 0, 1);
			lcd.drawString("Run Nav| Run w/", 0, 2);
			lcd.drawString("w/o bb | bb", 0, 3);
			
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		/* Begin the Odometer and Odometer Correction threads */
		Thread odoThread = new Thread(odometer);
		odoThread.start();
		Thread odoDisplayThread = new Thread(odometryDisplay);
		odoDisplayThread.start();
		
		/* If the user decides to run obstacle avoidance, this boolean will be set to true for use later on */
		if (buttonChoice == Button.ID_RIGHT) {
			obstacle_Avoidance = true;
		}
		
		/* This new threads starts the navigation thread. The obstacle avoidance boolean variable and Ultrasonic sensor instance is also passed */
		new Thread() {
			public void run() {
				try {
					Navigation.navigationControl(leftMotor, rightMotor, WHEEL_RAD, WHEEL_RAD, TRACK, positionWaypoints, us_Distance, obstacle_Avoidance);
				} catch (OdometerExceptions e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		
		System.exit(0);
	}

}
