/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package wilson;

import wilson.internal.Status;
import java.util.HashMap;

public class Sensor
{
  private HashMap<String, Object> status;
  private int version;

  public Sensor(HashMap<String, Object> status, int version) {
    this.status = status;
    this.version = version;
  }

  public boolean isWorking() {
    return Status.getIsWorking(status);
  }
  
  public double wallDistance() {
    return 0.091 * Status.getDistance(status, version);
  }
  
  public double leftDistance() {
    return Status.getLeftTicks(status) / 49.700;
  }
  
  public double rightDistance() {
    return Status.getRightTicks(status) / 49.700;
  }
  
  public int leftLine() {
    return Status.getLeftLine(status);
  }
  
  public int rightLine() {
    return Status.getRightLine(status);
  }
  
  public int leftLight() {
    return Status.getLeftLight(status);
  }
  
  public int rightLight() {
    return Status.getRightLight(status);
  }
  
  public int sound() {
    return Status.getSound(status, version);
  }
  
  public int battery() {
    return Status.getBattery(status, version);
  }
  
  public int temperature() {
    return Status.getTemperature(status, version);
  }
  
  public double accelerometer(String axis) {
    return Status.getAccelerometer(status, axis);
  }
  
  public double magnetometer(String axis) {
    return Status.getMagnetometer(status, axis);
  }
  
  public boolean touch() {
    return Status.getTouch(status, version);
  }
  
  public boolean button(String button) {
    return Status.getButton(status, button);
  }
  
  public boolean isShaking() {
    return Status.getIsShaking(status);
  }
  
  public boolean calibrated() {
    return Status.getIsCalibrated(status);
  }
  
  public int compass() {
    return Status.getCompass(status);
  }
  
  public Object[] rawData() {
    Object[] data = new Object[20];
    for (int index = 0; index < 20; index++) {
      data[index] = status.get(Integer.toString(index));
    }
    return data;
  }
}