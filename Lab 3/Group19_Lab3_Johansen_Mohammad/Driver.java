/*
 * SquareDriver.java
 */
package ca.mcgill.ecse211.lab3;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * This class is used to drive the robot on the demo floor.
 */
public class Driver {
  private static final int FORWARD_SPEED = 300;
  private static final int ROTATE_SPEED = 120;
  private static OdometerData odometer;
  private static int us_Detected_Distance;
  /**
   * This method runs the collision avoidance code
   * 
   * @param leftMotor
   * @param rightMotor
   * @param leftRadius
   * @param rightRadius
   * @param width
 * @throws OdometerExceptions 
   */
  public static void obstacle_Driver(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      double leftRadius, double rightRadius, double track, double distance, SampleProvider us_Distance) throws OdometerExceptions{
	  for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
	      motor.stop();
	      //We changed the acceleration to keep the robots motion smoother
	      motor.setAcceleration(500);
	    }

	    // Sleep for 2 seconds
	    try {
	      Thread.sleep(500);
	    } catch (InterruptedException e) {
	      // There is nothing to be done here
	    }
	  boolean currently_Navigating = true;
	  float[] sample = new float[us_Distance.sampleSize()];
	  odometer = Odometer.getOdometer();
	  leftMotor.setSpeed(FORWARD_SPEED);
	  rightMotor.setSpeed(FORWARD_SPEED);
	  //Start the robot moving foward
	  leftMotor.rotate(convertDistance(leftRadius, distance), true);
      rightMotor.rotate(convertDistance(rightRadius, distance), true);
      try {
    	  Thread.sleep(100);
      }
      catch (InterruptedException e) {
    	  
      }
      //This loop runs while the robot moves, continously scanning for obstacles
	  while(currently_Navigating) {
		  //Sensor distance is fetched
		  us_Distance.fetchSample(sample, 0);
		  us_Detected_Distance = (int) (sample[0] *100);
		  
		  //If the rotation speeds of both motors are 0, it must be done traveling. Breaks the loop
		  // and returns to nav class.
		  if(leftMotor.getRotationSpeed() == 0 && rightMotor.getRotationSpeed() == 0) {
			  odometer.setX(Navigation.nextWayPoint[0]);
			  odometer.setY(Navigation.nextWayPoint[1]);
			  odometer.setTheta(Navigation.turnToTheta);
			  break;
		  }
		  
		  //This if statement runs if the distance is lower than 10
		  if(us_Detected_Distance < 10) {
			  System.out.println("Executing block avoidance");
			  rightMotor.setSpeed(10);
			  leftMotor.setSpeed(10);
			  try {
		    	  Thread.sleep(1000);
		      }
		      catch (InterruptedException e) {
		      }
			  leftMotor.setSpeed(ROTATE_SPEED);
			  rightMotor.setSpeed(ROTATE_SPEED);
			  leftMotor.rotate(convertAngle(leftRadius, track, 90), true);
		      rightMotor.rotate(-convertAngle(rightRadius, track, 90), false);
		      try {
		    	  Thread.sleep(1000);
		      }
		      catch (InterruptedException e) {
		      }
			  leftMotor.setSpeed(FORWARD_SPEED);
			  rightMotor.setSpeed(FORWARD_SPEED);
			  leftMotor.rotate(convertDistance(leftRadius, 20), true);
		      rightMotor.rotate(convertDistance(rightRadius, 20), false);
		      try {
		    	  Thread.sleep(1000);
		      }
		      catch (InterruptedException e) {
		      }
		      leftMotor.setSpeed(ROTATE_SPEED);
		      rightMotor.setSpeed(ROTATE_SPEED);
		      leftMotor.rotate(-convertAngle(leftRadius, track, 90), true);
		      rightMotor.rotate(convertAngle(rightRadius, track, 90), false);
		      try {
		    	  Thread.sleep(1000);
		      }
		      catch (InterruptedException e) {
		      }
		      leftMotor.setSpeed(FORWARD_SPEED);
		      rightMotor.setSpeed(FORWARD_SPEED);
		      leftMotor.rotate(convertDistance(leftRadius, 30), true);
		      rightMotor.rotate(convertDistance(rightRadius, 30), false);
		      try {
		    	  Thread.sleep(1000);
		      }
		      catch (InterruptedException e) {
		      }
		      //The increment variable from the navigation class is decremented by 1 so that it
		      //recalculates for the same waypoint as before following the obstacle avoidance
		      currently_Navigating = false;
		      Navigation.increment = Navigation.increment - 1;
		      break;
		  }
	  }
  }
  
  /** The normal drive method. No obstacle avoidance */
  public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      double leftRadius, double rightRadius, double track, double distance) {
    // reset the motors
    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
      motor.stop();
      //We changed the acceleration to keep the robots motion smoother
      motor.setAcceleration(100);
    }

    // Sleep for 2 seconds
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }

      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      
      //Changed the tile size so that it would go the proper distance for our lab
      leftMotor.rotate(convertDistance(leftRadius, distance), true);
      rightMotor.rotate(convertDistance(rightRadius, distance), false);
  }
  
  /** The turn method, its directs the motors to turn by a certain theta. Can turn both 
   clockwise and counter clockwise */
  public static void turn(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
	      double leftRadius, double rightRadius, double track, double theta) {
	    // reset the motors
	    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
	      motor.stop();
	      //We changed the acceleration to keep the robots motion smoother
	      motor.setAcceleration(500);
	    }

	    // Sleep for 2 seconds
	    try {
	      Thread.sleep(500);
	    } catch (InterruptedException e) {
	      // There is nothing to be done here
	    }

	 // turn theta degrees counter-clockwise
	      leftMotor.setSpeed(ROTATE_SPEED);
	      rightMotor.setSpeed(ROTATE_SPEED);

	      leftMotor.rotate(convertAngle(leftRadius, track, theta), true);
	      rightMotor.rotate(-convertAngle(rightRadius, track, theta), false);
	  }
  
  /**
   * This method allows the conversion of a distance to the total rotation of each wheel need to
   * cover that distance.
   * 
   * @param radius
   * @param distance
   * @return
   */
  private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }
}
