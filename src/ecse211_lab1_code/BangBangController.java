package ecse211_lab1_code;

import java.awt.Button;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int distance;
  private int filterControl;
  
  private static final int FILTER_OUT = 50;

  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    // Default Constructor
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    this.filterControl = 0;
    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
    WallFollowingLab.rightMotor.setSpeed(motorHigh);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {
	  if (distance >= 150 && filterControl < FILTER_OUT) {
	      // bad value, do not set the distance var, however do increment the
	      // filter value
	      filterControl++;
	    } else if (distance >= 150) {
	      // We have repeated large values, so there must actually be nothing
	      // there: leave the distance alone
	      this.distance = distance;
	    } else {
	      // distance went below 255: reset filter and leave
	      // distance alone.
	      filterControl = 0;
	      this.distance = distance;
	    }
	  
	  
    System.out.println(this.distance);
    
    if(this.distance < (this.bandCenter - this.bandwidth)) {
    	WallFollowingLab.leftMotor.setSpeed(motorHigh);
    	WallFollowingLab.rightMotor.setSpeed(motorLow);
    	if(this.distance < 10) {
    		WallFollowingLab.leftMotor.backward();
    		WallFollowingLab.rightMotor.backward();
    		WallFollowingLab.leftMotor.setSpeed(motorLow);
    		WallFollowingLab.rightMotor.setSpeed(motorHigh);
    		try {
    			Thread.sleep(1000);
    		}
    		catch (Exception e) {
    		}
    		WallFollowingLab.leftMotor.forward();
    		WallFollowingLab.rightMotor.forward();
    		WallFollowingLab.leftMotor.setSpeed(motorHigh);
    		WallFollowingLab.rightMotor.setSpeed(motorLow);
    		}
    		try {
    			Thread.sleep(1000);
    		}
    		catch (Exception e) {
    		}
    	}
    
    if(this.distance > (this.bandCenter + this.bandwidth)) {
    	WallFollowingLab.rightMotor.setSpeed(motorHigh);
    	WallFollowingLab.leftMotor.setSpeed(motorLow);
    }
    
    if((this.distance < this.bandCenter + (this.bandwidth)) && (this.distance > this.bandCenter - (this.bandwidth))) {
    	WallFollowingLab.leftMotor.setSpeed(motorHigh);
    	WallFollowingLab.rightMotor.setSpeed(motorHigh);
    }
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
