package ca.mcgill.ecse211.lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
/** Navigation class calculates the distances and turning angles needed for navigation */
public class Navigation {
	/* Declaring all the necessary variables */
	private static OdometerData odometer;
	private static double deltaX;
	private static double deltaY;
	public static double turnToTheta;
	private static double currentTheta;
	private static double deltaTheta;
	private static double distance;
	public static int increment;
	private static double[] robotPosition = new double[3];
	public static double[] nextWayPoint = new double[2];

	/*
	 * @param leftMotor
	 * @param rightMotor
	 * @param leftRadius
	 * @param rightRadius
	 * @param track
	 * @param positionWaypoints
	 * @param us_Distance
	 * @param obstacle_Avoidance
	 * @throws OdometerExceptions
	 */
	
	/** This method is the main method of this class, it calculates the necessary distances 
	 * and turning angles and calls the appropriate methods */
	public static void navigationControl(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
		      double leftRadius, double rightRadius, double track, double[] positionWaypoints) throws OdometerExceptions {
		/* Here we get the odometer instance for use in the calculations, and begin a for loop which 
		 * calculates the necessary operations for every waypoint
		 */
		odometer = Odometer.getOdometer();
		for(increment = 0; increment < 1; increment++) {
			//Robot position determined
			robotPosition = odometer.getXYT();
			//Next waypoint is set to the nextWayPoint variable
			nextWayPoint[0] = positionWaypoints[increment*2] * 30.48;
			nextWayPoint[1] = positionWaypoints[increment*2 + 1] * 30.48;
			//DeltaX and DeltaY are determined
			deltaX = nextWayPoint[0] - robotPosition[0];
			deltaY = nextWayPoint[1] - robotPosition[1];
			//Turning angle is determined
			turnToTheta = Math.atan(deltaX/deltaY);
			turnToTheta = Math.toDegrees(turnToTheta);
			//The next 4 if statements are used to account for certain
			//situations in which the angle calculation isnt totally correct
			if(deltaX > 0 && deltaY < 0) {
				turnToTheta = turnToTheta + 180;
			}
			if(deltaX < 0 && deltaY < 0) {
				turnToTheta = turnToTheta - 180;
			}
			if(turnToTheta < 0) {
				turnToTheta = turnToTheta + 360;
			}
			if(deltaX == 0) {
				if(deltaY > 0) {
					turnToTheta = 0;
				}
				else if(deltaY < 0) {
					turnToTheta = 180;
				}
			}
			//Distance we need to travel is determined
			distance = Math.sqrt(Math.pow(deltaX, 2)+Math.pow(deltaY, 2));
			currentTheta = robotPosition[2];
			//Turnto class is called first, followed by the travel to class
			turnTo(leftMotor, rightMotor, leftRadius, rightRadius, track, turnToTheta, currentTheta);
			//The ultrasonic instance and obstacle avoidance boolean are passed here as well
			travelTo(leftMotor, rightMotor, leftRadius, rightRadius, track, distance);
		}
		
	}
	/** The travelTo method determined whether obstacle avoidance is necessary, and directs the robot */
	public static void travelTo(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
		      double leftRadius, double rightRadius, double track, double distance) throws OdometerExceptions {
		Driver.drive(leftMotor, rightMotor, leftRadius, rightRadius, track, distance);
	}
	
	/** turnTo method calculates the deltaTheta required and calls the turning method in driver */
	public static void turnTo(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
		      double leftRadius, double rightRadius, double track, double turnToTheta, double currentTheta) {
		if(currentTheta >= 355 || currentTheta <= 5) {
			currentTheta = 0;
		}
		deltaTheta  = turnToTheta - currentTheta;
		
		/*
		 * The next statements are using to fix theta in certain cases where the theta calculated would be incorrect by a known
		 * number of degrees
		 */
		
		if(deltaTheta > 180) {
			deltaTheta = deltaTheta - 360;
		}
		else if(deltaTheta < -180) {
			deltaTheta = deltaTheta + 360;
		}
		
		/*
		 * Based on the value of delta theta, we turn in a certain direction
		 */
		
		if (deltaTheta < 0) {
			Driver.turn(leftMotor, rightMotor, leftRadius, rightRadius, track, deltaTheta);
		}
		else if (deltaTheta > 0) {
			Driver.turn(leftMotor, rightMotor, leftRadius, rightRadius, track, deltaTheta);
		}
		
	}
}
