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

public class TestSensors
{
  public static void main(String[] args) {
    Finch finch = new Finch();
    // ??? not work ??? finch.calibrate()

    finch.runMotors(5, 4);
    while (true) {
      Finch.sleep(5000);
      Sensor sensors = finch.getSensors();
      Finch.print("isWorking:", sensors.isWorking());
      Finch.print("wallDistance:", sensors.wallDistance());
      Finch.print("leftDistance:", sensors.leftDistance());
      Finch.print("rightDistance:", sensors.rightDistance());
      Finch.print("leftLine:", sensors.leftLine());
      Finch.print("rightLine:", sensors.rightLine());
      Finch.print("leftLight:", sensors.leftLight());
      Finch.print("rightLight:", sensors.rightLight());
      Finch.print("sound:", sensors.sound());
      Finch.print("battery:", sensors.battery());
      Finch.print("temperature:", sensors.temperature());
      Finch.print("touch:", sensors.touch());
      Finch.print("buttonA:", sensors.button("A"));
      Finch.print("buttonB:", sensors.button("B"));
      Finch.print("isShaking:", sensors.isShaking());
      Finch.print("calibrated:", sensors.calibrated()); // ???
      Finch.print("compass:", sensors.compass()); // ??? not work
      Finch.print("rawData:", sensors.rawData());
      if (sensors.leftDistance() > 200) break;
    }

    finch.stopAll();
  }
}
