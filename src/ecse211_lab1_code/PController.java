package ecse211_lab1_code;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 40;

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
      // distance went below 255: reset filter and leave
      // distance alone.
      filterControl = 0;
      this.distance = distance;
    }
    
    PathError = Math.abs(this.distance - this.bandCenter);
    if (PathError > 30) {
    	PathError = 30;
    }
    AdjustedMotorSpeedUp = ((PathError * PConst) + MOTOR_SPEED);
    AdjustedMotorSpeedDown = (MOTOR_SPEED - (PathError * PConst));
    if (AdjustedMotorSpeedDown <= 0) {
    	AdjustedMotorSpeedDown = 5;
    }
    System.out.println(AdjustedMotorSpeedUp+" "+AdjustedMotorSpeedDown+" "+this.distance);

    if(this.distance < (this.bandCenter - this.bandWidth)) {
    	WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    	WallFollowingLab.rightMotor.setSpeed(AdjustedMotorSpeedDown);
    	if(this.distance < 10) {
    		WallFollowingLab.leftMotor.backward();
    		WallFollowingLab.rightMotor.backward();
    		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    		WallFollowingLab.rightMotor.setSpeed(AdjustedMotorSpeedUp);
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
    
    if(this.distance > (this.bandCenter + this.bandWidth)) {
    	WallFollowingLab.rightMotor.setSpeed(AdjustedMotorSpeedUp);
    	WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    }
    
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
