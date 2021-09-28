/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
import wilson.Finch;

public class TestLights
{
  public static void main(String[] args) {
    Finch finch = new Finch();
    finch.tailColor(5, 255, 0, 0);
    Finch.sleep(2000);
    finch.beakColor(255, 255, 0);
    finch.tailColor(1, 0, 255, 0);
    finch.tailColor(2, 0, 255, 255);
    finch.tailColor(3, 0, 0, 255);
    finch.tailColor(4, 255, 0, 255);
    Finch.sleep(2000);
    finch.tailColor(0, 63, 127, 255);
    Finch.sleep(2000);
  }
}
