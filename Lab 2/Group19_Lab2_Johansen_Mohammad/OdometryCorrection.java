/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;
  private Odometer odometer;
  private float[] sample;
  private float[] prev_sample;
  private EV3ColorSensor sensor;
  long line_detected_time;
  public int line_count;
  private double x,y,theta;

  /**
   * This is the default class constructor. An existing instance of the odometer is used. This is to
   * ensure thread safety.
   * 
   * @throws OdometerExceptions
   */
  public OdometryCorrection() throws OdometerExceptions {
	//Here we set up the sensor, set up a float array to hold the value of the double, and make a line_count variable
    this.odometer = Odometer.getOdometer();
    Port port = LocalEV3.get().getPort("S1");
    sensor = new EV3ColorSensor(port);
    sample = new float[1];
    prev_sample = new float[1];
    line_count = 0;
  }

  /**
   * Here is where the odometer correction code should be run.
   * 
   * @throws OdometerExceptions
   */
  // run method (required for Thread)
  public void run() {
    long correctionStart, correctionEnd;
    //Setting the backlight
    sensor.setFloodlight(6);
    while (true) {
      correctionStart = System.currentTimeMillis();
      //In order to make a "filter" for the values, we import the current x and y value to know how close we are to the next line
      y = odometer.getY();
      x = odometer.getX();
      //The sensor fetches the current value and assigns it to sample
      sensor.getRedMode().fetchSample(sample, 0);
      //Here we have our detection setup, if the light sensor falls below .26 it is likely to be a black line.
      //Also included some extra code to try and prevent instances where the sensor would beep several times
      if(sample[0] < 0.26 && prev_sample[0] > 0.26 && prev_sample[0] != sample[0]) {
    	  line_count++;
    	  //Using a case system based off of the line count to update the position. By knowing what each line will represent
    	  //we are able to make the cases accordingly.
    	  //Most cases also include an if statement, this is to improve the reliability of the sensor as sometimes 
    	  //the sensor reads a black line twice in a row and our whole system is off. Only when the x and y values are close to expected values
    	  //will the filter allow to odo to update. 
    	  switch(line_count) {
        	case 1: y = 0; theta = 0; odometer.setY(y); odometer.setTheta(theta); Sound.beep(); break;
        	case 2: if(y >= 20) { 
        			y = 30.48; theta = 0; odometer.setY(y); Sound.beep(); odometer.setTheta(theta); break;
        			}
        			else line_count--; break; 
        	case 3: if(y >= 50) {
        			y = 60.96; theta = 0; odometer.setY(y); Sound.beep(); odometer.setTheta(theta); break;
        			}
        			else line_count --; break;
        	case 4: if(y >= 60.96) {
        			x = 0; theta = 90; odometer.setX(x); odometer.setTheta(theta); Sound.beep(); break;
        			}
        			else line_count--; break;
        	case 5: if(x >= 20) {
        			x = 30.48; theta = 90; odometer.setX(x); Sound.beep(); odometer.setTheta(theta); break;
        			}
        			else line_count--; break;
        	case 6: if(x >= 50) {
        			x = 60.96; theta = 90; odometer.setX(x); Sound.beep(); odometer.setTheta(theta); break;
        			}
        			else line_count--; break;
        	case 7: if(x >= 60.96) {
        			y = 60.96; theta = 180; odometer.setY(y); odometer.setTheta(theta); Sound.beep(); break;
        			}
        			else line_count--; break;
        	case 8: if(y <= 40) {
        			y = 30.48; theta = 180; odometer.setY(y); Sound.beep(); odometer.setTheta(theta); break;
        			}
        			else line_count--; break;
        	case 9: if(y <= 10) {
        			//y is set to 21 here, as opposed to 0, to account for the offset of the sensor
        			y = 0 + 21; theta = 180; odometer.setY(y); Sound.beep(); odometer.setTheta(theta); break;
        			}
        			else line_count--; break;
        	case 10:if(y <= 0) {
        			x = 60.96; theta = 270; odometer.setX(x); odometer.setTheta(theta); Sound.beep(); break;
        			}
        			else line_count--; break;
        	case 11: if(x <= 40) {
        			x = 30.48; theta = 270; odometer.setX(x); Sound.beep(); odometer.setTheta(theta); break;
        			}
        			else line_count--; break;
        	case 12: if(x <= 10) {
        			//x is set to 12 here, as opposed to 0, to account for the offset of the sensor
        			x = 0+12; theta = 270; odometer.setX(x); Sound.beep(); odometer.setTheta(theta); break;
        			}
        			else line_count--; break;
        	default: break;
    	  }
      }
      //Part of the filter, used to help us prevent the sensor from detecting the same line twice
      prev_sample[0] = sample[0];
 
      // this ensure the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        try {
          Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
        } catch (InterruptedException e) {
          // there is nothing to be done here
        }
      }
    }
  }
}
