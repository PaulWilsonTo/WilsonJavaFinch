/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package wilson.internal;

public class Common
{
  private static final boolean IS_DEBUG = false;

  public static void debug(Object... params) {
    if (IS_DEBUG) {
      String output = "";
      for (int index = 0; index < params.length; index++) {
        output += params[index] + " ";
      }
      System.out.println(output);
    }
  }
}
