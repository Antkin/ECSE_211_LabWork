package ca.mcgill.ecse211.lab4;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
/**
 * 
 *	This is the ultrasonic localizer class, it takes 2 ultrasonic measurements 
 *	and finds out its correct theta
 *
 */
public class UltrasonicLocalizer {
	
	/* Initializing the sensor and any variables */
	private static int rotate_Speed = 150;
	private static double[] position = new double[3];
	private static OdometerData odometer;
	private static final Port us_Port = LocalEV3.get().getPort("S2");
	private static double alpha, beta;
	private static int detected_Distance;
	private static double fix_Theta;
	

	static SensorModes us_Sensor = new EV3UltrasonicSensor(us_Port);
	final static SampleProvider us_Distance = us_Sensor.getMode("Distance");
	public static float[] us_sample = new float[us_Distance.sampleSize()];
	
	/**
	 * The falling edge method localizes the robot if it is facing away from the wall
	 */
	public static void falling_Edge(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius, double rightRadius, double track) throws OdometerExceptions {
		/* Get an instance of the odo, and prepare to start fetching samples */
		odometer = Odometer.getOdometer();
		us_Distance.fetchSample(us_sample, 0);
		detected_Distance = (int) (us_sample[0] * 100);
		
		/*
		 * Set acceleration to 125 and begin rotating
		 */
		leftMotor.setAcceleration(125);
		rightMotor.setAcceleration(125);
		
		leftMotor.forward();
		rightMotor.backward();
		leftMotor.setSpeed(rotate_Speed);
		rightMotor.setSpeed(rotate_Speed);
		
		/*
		 * Once the distance falls below 40, we record the position and start turning the other way
		 */
		while(detected_Distance > 40) {
			us_Distance.fetchSample(us_sample, 0);
			detected_Distance = (int) (us_sample[0] * 100);
		}
		Sound.beep();
		
		position = odometer.getXYT();
		alpha = position[2];
		
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		/*
		 * This was included to prevent us from reading the same wall twice
		 */
		Driver.turn(leftMotor, rightMotor, leftRadius, rightRadius, track, -60);
		
		rightMotor.forward();
		leftMotor.backward();
		rightMotor.setSpeed(rotate_Speed);
		leftMotor.setSpeed(rotate_Speed);
		
		/*
		 * Fetch another sample before we begin searching for a distance > 40
		 */
		us_Distance.fetchSample(us_sample, 0);
		detected_Distance = (int) (us_sample[0] * 100);
		
		while(detected_Distance > 40) {
			us_Distance.fetchSample(us_sample, 0);
			detected_Distance = (int) (us_sample[0] * 100);
		}
		Sound.beep();
		
		position = odometer.getXYT();
		beta = position[2];
		
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		/*
		 * We run the correction code next and update the odo
		 * Finish by turning to 0 degrees
		 */
		
		if(alpha < beta) {
			fix_Theta = 225 - (alpha+beta)/2;
		}
		else if(alpha >= beta) {
			fix_Theta = 45 - (alpha+beta)/2;
		}
		
		odometer.setTheta(odometer.getTheta() + fix_Theta);
		
		Navigation.turnTo(leftMotor, rightMotor, leftRadius, rightRadius, track, 0.0, odometer.getTheta());
	}
	
	/**
	 * The rising edge method localizes if the robot is facing towards the wall
	 */
	public static void rising_Edge(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius, double rightRadius, double track) throws OdometerExceptions {
		/*
		 * Rising method works the same as above, just in the other direction 
		 */
		odometer = Odometer.getOdometer();
		us_Distance.fetchSample(us_sample, 0);
		detected_Distance = (int) (us_sample[0] * 100);
		
		leftMotor.setAcceleration(125);
		rightMotor.setAcceleration(125);
		
		leftMotor.forward();
		rightMotor.backward();
		leftMotor.setSpeed(rotate_Speed);
		rightMotor.setSpeed(rotate_Speed);
		
		while(detected_Distance < 40) {
			us_Distance.fetchSample(us_sample, 0);
			detected_Distance = (int) (us_sample[0] * 100);
		}
		Sound.beep();
		
		position = odometer.getXYT();
		alpha = position[2];
		
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		Driver.turn(leftMotor, rightMotor, leftRadius, rightRadius, track, -60);
		
		rightMotor.forward();
		leftMotor.backward();
		rightMotor.setSpeed(rotate_Speed);
		leftMotor.setSpeed(rotate_Speed);
		
		
		
		us_Distance.fetchSample(us_sample, 0);
		detected_Distance = (int) (us_sample[0] * 100);
		
		while(detected_Distance < 40) {
			us_Distance.fetchSample(us_sample, 0);
			detected_Distance = (int) (us_sample[0] * 100);
		}
		Sound.beep();
		
		position = odometer.getXYT();
		beta = position[2];
		
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		/*
		 * The correction is opposite for rising method however.
		 */
		
		if(alpha < beta) {
			fix_Theta = 40 - (alpha+beta)/2;
		}
		else if(alpha >= beta) {
			fix_Theta = 220 - (alpha+beta)/2;
		}
		
		odometer.setTheta(odometer.getTheta() + fix_Theta);
		
		Navigation.turnTo(leftMotor, rightMotor, leftRadius, rightRadius, track, 0.0, odometer.getTheta());
		
		while(Button.waitForAnyPress() != Button.ID_UP);
		LightLocalizer.light_Localizer(leftMotor, rightMotor, 1.60, 1.60, 18.55);
	}
	
}
