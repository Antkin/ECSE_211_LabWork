package ca.mcgill.ecse211.lab4;

import java.util.Arrays;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class LightLocalizer {
	
	/* Initializing sensor and variables */
	private static Odometer odometer;
	private static EV3ColorSensor light_Sensor;
	private static float[] current_Light_Value = new float[1];
	private static float[] light_samples = new float[50];
	private static int increment;
	private static float median;
	private static double[] final_position = {0,0};
	
	/** This is our median finder method, for increasing light sensor reliability */
	public static float median_Finder(float[] light_samples) {
		  float median;
		  Arrays.sort(light_samples);
		  median = light_samples[(light_samples.length/2)];
		  return median;
	  }
	
	/**
	 * The light localizer uses a light sensor to detect black lines and find the x and y location
	 * of the robot
	 */
	public static void light_Localizer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius, double rightRadius, double track) throws OdometerExceptions {
		/*
		 * Here we instance the sensor and odometer
		 */
		Port light_Sensor_Port = LocalEV3.get().getPort("S1");
		light_Sensor = new EV3ColorSensor(light_Sensor_Port);
		light_Sensor.setFloodlight(6);
		odometer = Odometer.getOdometer();
		
		/*
		 * We take 50 light samples, find the median, and compare our current light value to the median
		 * This method allows the light detection to work for any kind of lighting, except lighting
		 * that changes during the trials
		 */
		
		for(increment = 0; increment < 50; increment++) {
			light_Sensor.getRedMode().fetchSample(current_Light_Value, 0);
			light_samples[increment] = current_Light_Value[0];
		}
		
		/*
		 * Set motor acceleration and begin moving forward
		 */
		
		leftMotor.setAcceleration(50);
		rightMotor.setAcceleration(50);
		
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.setSpeed(50);
		rightMotor.setSpeed(50);
		
		/*
		 * The sensor continously takes samples while driving, 
		 * once a black line is detected we stop and back up to the original 
		 * tile
		 */
		while(true) {
			light_Sensor.getRedMode().fetchSample(current_Light_Value, 0);
			if(increment >= 50) {
				increment = 0;
			}
			light_samples[increment] = current_Light_Value[0];
			increment++;
			median = median_Finder(light_samples);
			
			if(Math.abs(current_Light_Value[0] - median) >= .03) {
				odometer.setY(0);
				Sound.beep();
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				break;
			}
		}
		
		while(leftMotor.getRotationSpeed() != 0 && rightMotor.getRotationSpeed() != 0);
		
		Driver.drive(leftMotor, rightMotor, leftRadius, rightRadius, track, -15);
		
		/*
		 * We turn 90 degrees, and repeat the same process for the x = 0 line
		 */
		Driver.turn(leftMotor, rightMotor, leftRadius, rightRadius, track, 90);
		
		for(increment = 0; increment < 50; increment++) {
			light_Sensor.getRedMode().fetchSample(current_Light_Value, 0);
			light_samples[increment] = current_Light_Value[0];
		}
		
		leftMotor.setAcceleration(50);
		rightMotor.setAcceleration(50);
		
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.setSpeed(50);
		rightMotor.setSpeed(50);
		
		while(true) {
			light_Sensor.getRedMode().fetchSample(current_Light_Value, 0);
			if(increment >= 50) {
				increment = 0;
			}
			light_samples[increment] = current_Light_Value[0];
			increment++;
			median = median_Finder(light_samples);
			
			if(Math.abs(current_Light_Value[0] - median) >= .03) {
				odometer.setY(0);
				Sound.beep();
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				break;
			}
		}
		
		while(leftMotor.getRotationSpeed() != 0 && rightMotor.getRotationSpeed() != 0);
		
		Driver.drive(leftMotor, rightMotor, leftRadius, rightRadius, track, -15);
		
		/*
		 * We had some odometer problems so we have the robot end up in a known position
		 * and we reset the x and y coordinates
		 */
		
		odometer.setX(-15);
		odometer.setY(-15);
		
		/*
		 * Finish off the routine by travelling to 0,0 and turning to 0 degrees
		 */
		
		Navigation.navigationControl(leftMotor, rightMotor, leftRadius, rightRadius, track, final_position);
		Navigation.turnTo(leftMotor, rightMotor, leftRadius, rightRadius, track, 0, odometer.getTheta());
		
	}
}
