/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
import wilson.Finch;

public class TestMultiple
{
  public static void main(String[] args) {
    Finch finch1 = new Finch("Connect Finch 1 at Left 10", -10, 0);
    Finch finch2 = new Finch("Connect Finch 2 at Right 10", 10, 0);
    Finch finch3 = new Finch("Connect Finch 3 at Up 10 and Left 10", -10, 10);
    Finch finch4 = new Finch("Connect Finch 4 at Up 10 and Right 10", 10, 10);

    finch1.beakColor(255, 0, 0);
    finch2.beakColor(0, 255, 0);
    finch3.beakColor(0, 0, 255);
    finch4.beakColor(255, 165, 0);

    finch1.playSound(48, 1000);
    finch2.playSound(60, 1000);
    finch3.playSound(72, 1000);
    finch4.playSound(84, 1000);

    finch1.runMotors(-5, -10);
    finch2.runMotors(-10, -5);
    finch3.runMotors(5, 10);
    finch4.runMotors(10, 5);

    Finch.sleep(2000);

    finch1.stopAll();
    finch2.stopAll();
    finch3.stopAll();
    finch4.stopAll();
  }
}
