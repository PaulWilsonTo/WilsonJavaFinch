/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
import wilson.Finch;

public class TestInfo
{
  public static void main(String[] args) {
    Finch finch = new Finch();
    Finch.print("Target: " + finch.getTarget());
    Finch.print("Device: " + finch.getDevice());
    Finch.print("Name: " + finch.getName());
    Finch.print("Version: " + finch.getVersion());
  }
}
