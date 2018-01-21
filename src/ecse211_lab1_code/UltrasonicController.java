package ecse211_lab1_code;

public interface UltrasonicController {

  public void processUSData(int distance);

  public int readUSDistance();
}
