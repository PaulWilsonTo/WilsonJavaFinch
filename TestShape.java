/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
import wilson.Finch;

public class TestShape
{
  public static void main(String[] args) {
    int sides = Integer.parseInt(Finch.input("Number of sides?"));
    double distance = Double.parseDouble(Finch.input("Length of sides?"));
    double speed = Double.parseDouble(Finch.input("Speed (1-10)?"));
    
    Finch finch = new Finch();
    finch.speed(speed);
    for (int side = 0; side < sides; side++) {
      finch.forward(distance);
      finch.right(360 / sides);
    }
  }
}
