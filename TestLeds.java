/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
import wilson.Finch;
import wilson.LedGrid;

public class TestLeds
{
  public static void main(String[] args) {
    LedGrid ledGrid = new LedGrid();
    ledGrid.setLed(0, 0);
    ledGrid.setLed(0, 2);
    ledGrid.setLed(0, 4);
    ledGrid.setLed(1, 1);
    ledGrid.setLed(1, 3);
    ledGrid.setLed(2, 0);
    ledGrid.setLed(2, 4);
    ledGrid.setLed(3, 1);
    ledGrid.setLed(3, 3);
    ledGrid.setLed(4, 0);
    ledGrid.setLed(4, 2);
    ledGrid.setLed(4, 4);

    Finch finch = new Finch();
    finch.customLeds(ledGrid);
    Finch.sleep(3000);
    finch.flashLeds("Wilson Finch");
    Finch.sleep(12000);
  }
}
