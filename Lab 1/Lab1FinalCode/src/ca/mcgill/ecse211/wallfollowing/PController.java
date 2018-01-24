package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 22;

  private final int bandCenter;
  private final int bandWidth;
  private int distance;
  private int filterControl;
  private float PathError;
  private float AdjustedMotorSpeedUp;
  private float AdjustedMotorSpeedDown;
  
  private final float PConst = (float) 13;

  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;

    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Initalize motor rolling forward
    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {

    // rudimentary filter - toss out invalid samples corresponding to null
    // signal.
    // (n.b. this was not included in the Bang-bang controller, but easily
    // could have).
    //
    if (distance >= 80 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the
      // filter value
      filterControl++;
    } else if (distance >= 80) {
      // We have repeated large values, so there must actually be nothing
      // there: leave the distance alone
      this.distance = distance;
    } else {
      // distance went below 80: reset filter and leave
      // distance alone.
      filterControl = 0;
      this.distance = distance;
    }
    
    //This function below determines how off course the robot is, and determines the adjusted motor speeds accordingly 
    PathError = Math.abs(this.distance - this.bandCenter);
    if (PathError > 30) {
    	PathError = 20;
    }
    AdjustedMotorSpeedUp = ((PathError * PConst) + MOTOR_SPEED);
    AdjustedMotorSpeedDown = (MOTOR_SPEED - (PathError * PConst));
    if (AdjustedMotorSpeedDown <= 0) {
    	AdjustedMotorSpeedDown = 5;
    }
    
    //If this distance is too small, the robot starts turning away
    if(this.distance < (this.bandCenter - this.bandWidth)) {
    	WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    	WallFollowingLab.rightMotor.setSpeed(AdjustedMotorSpeedDown);
    	//The while loop was implemented because at times the robot would continue turning 
    	//when the distance was low and then jumped above the filter. This prevents the robot from over
    	//turning and keeps it on track
    	while(filterControl >= 2) {
    		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    		WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    		filterControl++;
    		if(filterControl >= 10) {
    			filterControl = 22;
    			break;
    		}
    	}
    	//This statement backs up the robot if it senses it is too close to the wall
    	//allowing it to perform the turn without hitting the wall
    	if(this.distance < 10) {
    		WallFollowingLab.leftMotor.backward();
    		WallFollowingLab.rightMotor.backward();
    		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    		WallFollowingLab.rightMotor.setSpeed(AdjustedMotorSpeedUp);
    		//We used a thread sleep to be able to control how long the robot would
    		//back up and turn for, in this case 500ms. Otherwise it only implemented for a split second
    		//before going bacj on its previous course which generally led to a crash or otherwise
    		//jerky behavior.
    		try {
    			Thread.sleep(500);
    		}
    		catch (Exception e) {
    		}
    		WallFollowingLab.leftMotor.forward();
    		WallFollowingLab.rightMotor.forward();
    		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    		WallFollowingLab.rightMotor.setSpeed(AdjustedMotorSpeedDown);
    		}
    		try {
    			Thread.sleep(500);
    		}
    		catch (Exception e) {
    		}
    	}
    
    //This statement performs a left turn when the distance is too high 
    if(this.distance > (this.bandCenter + this.bandWidth)) {
    	WallFollowingLab.rightMotor.setSpeed(AdjustedMotorSpeedUp);
    	WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    }
    
    //This statement allows the robot to just continue straight if is within the bandcenter
    if((this.distance < this.bandCenter + (this.bandWidth)) && (this.distance > this.bandCenter - (this.bandWidth))) {
    	WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    	WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    }
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
