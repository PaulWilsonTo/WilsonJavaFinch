/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
import wilson.Finch;

public class TestInput
{
  public static void main(String[] args) {
    Finch.print("Control by Keyboard Input");
    Finch.print("Enter for Forward, Q for Quit,");
    Finch.print("L for Left, or R for Right");
    
    Finch finch = new Finch();
    while (true) {
      finch.runMotors(5, 5);  
      String command = Finch.input("Enter, L, R, or Q");
      if (command.equalsIgnoreCase("L")) {
        finch.stopAll();
        finch.left(45);
      }
      else if (command.equalsIgnoreCase("R")) {
        finch.stopAll();
        finch.right(45);
      }
      else if (command.equalsIgnoreCase("Q")) {
        finch.stopAll();
        break;
      }
    }
  }
}
