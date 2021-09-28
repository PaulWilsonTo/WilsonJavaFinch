/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package wilson;

public class LedGrid
{
  private int[] list;

  public LedGrid() {
    list = new int[] {
      0, 0, 0, 0, 0,
      0, 0, 0, 0, 0,
      0, 0, 0, 0, 0,
      0, 0, 0, 0, 0,
      0, 0, 0, 0, 0
    };
  }
  
  public void setLed(int row, int col, boolean led) {
    int index = 5 * row + col;
    list[index] = (led ? 1 : 0);
  }

  public void setLed(int row, int col) {
    setLed(row, col, true);
  }

  public int[] getList() {
    return list;
  }
}
