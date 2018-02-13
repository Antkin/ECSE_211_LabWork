/*
 * SquareDriver.java
 */
package ca.mcgill.ecse211.lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;

/**
 * This class is used to drive the robot on the demo floor.
 */
public class Driver {
  private static final int FORWARD_SPEED = 150;
  private static final int ROTATE_SPEED = 150;
  
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
      Thread.sleep(1000);
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
	      motor.setAcceleration(75);
	    }

	    // Sleep for 2 seconds
	    try {
	      Thread.sleep(1000);
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
