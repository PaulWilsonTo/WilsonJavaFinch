/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
import wilson.Finch;
import wilson.Sensor;

public class TestControl
{
  public static void main(String[] args) {
    Finch.print("Control by Shining Light");
    Finch.print("Shine on Left or Right Side");
    
    Finch finch = new Finch();
    while (true) {
      finch.runMotors(2, 2);
      Sensor sensors = finch.getSensors();

      if (sensors.leftLight() >= 10) {
        finch.stopAll();
        finch.left(45);
      }
      else if (sensors.rightLight() >= 10) {
        finch.stopAll();
        finch.right(45);
      }
      else if (sensors.wallDistance() < 2) {
        finch.stopAll();
        break;
      }
    }
  }
}
