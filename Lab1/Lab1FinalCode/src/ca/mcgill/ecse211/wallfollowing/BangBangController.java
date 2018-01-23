package ca.mcgill.ecse211.wallfollowing;

import java.awt.Button;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int distance;
  private int filterControl;
  private int TurnCount;
  
  private static final int FILTER_OUT = 22;

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
    
	//Begins a right turn when the distance is below the bandcenter, same safety features
	// as the PController such as a reverse when the distance is very low
    if(this.distance < (this.bandCenter - this.bandwidth)) {
    	WallFollowingLab.leftMotor.setSpeed(motorHigh);
    	WallFollowingLab.rightMotor.setSpeed(motorLow);
    	while(filterControl >= 2) {
    		WallFollowingLab.leftMotor.setSpeed(motorHigh);
    		WallFollowingLab.rightMotor.setSpeed(motorHigh);
    		filterControl++;
    		if(filterControl >= 10) {
    			filterControl = 22;
    			break;
    		}
    	}
    	if(this.distance < 10) {
    		WallFollowingLab.leftMotor.backward();
    		WallFollowingLab.rightMotor.backward();
    		WallFollowingLab.leftMotor.setSpeed(motorLow);
    		WallFollowingLab.rightMotor.setSpeed(motorHigh);
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
    		WallFollowingLab.leftMotor.setSpeed(motorHigh);
    		WallFollowingLab.rightMotor.setSpeed(motorLow);
    		}
    		try {
    			Thread.sleep(500);
    		}
    		catch (Exception e) {
    		}
    	}
    
    //This statement commences a left turn when the distance is above the bandcenter
    if(this.distance > (this.bandCenter + this.bandwidth)) {
    	WallFollowingLab.rightMotor.setSpeed(motorHigh);
    	WallFollowingLab.leftMotor.setSpeed(motorLow);
    	//This statement is to help the robot perform sharper 180 degree turns. When the distance is above 60
    	// for an extended period of time it begins a count. Once the count reaches 200 the robot will begin a turn in place
    	//allowing it to perform sharp turns.
    	if(this.distance >= 60) {
    		TurnCount++;
    		if(TurnCount >= 200) {
    			WallFollowingLab.leftMotor.backward();
        		WallFollowingLab.rightMotor.backward();
        		WallFollowingLab.leftMotor.setSpeed(motorHigh);
        		WallFollowingLab.rightMotor.setSpeed(motorLow);
        		try {
        			Thread.sleep(1000);
        		}
        		catch (Exception e) {
        		}
        		WallFollowingLab.leftMotor.forward();
        		WallFollowingLab.rightMotor.forward();
        		WallFollowingLab.leftMotor.setSpeed(motorLow);
        		WallFollowingLab.rightMotor.setSpeed(motorHigh);
        		try {
        			Thread.sleep(2000);
        		}
        		catch (Exception e) {
        		}
        		TurnCount = 0;
    		}
    	}
    }
    
    //This statement keeps the robot on track when it is within the bandwidth, also reset the "turncount" so as to prevent it
    //from continously building every time the robot turns left for too long. 
    if((this.distance < this.bandCenter + (this.bandwidth)) && (this.distance > this.bandCenter - (this.bandwidth))) {
    	WallFollowingLab.leftMotor.setSpeed(motorHigh);
    	WallFollowingLab.rightMotor.setSpeed(motorHigh);
    	TurnCount = 0;
    }
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
