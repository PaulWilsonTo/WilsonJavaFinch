/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package wilson.internal;

import java.util.HashMap;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Command
{
  public UUID id;
  public int target;
  public String type;
  public HashMap<String, Object> params;

  public Command(int target, String type, HashMap<String, Object> params) {
    this.id = UUID.randomUUID();
    this.target = target;
    this.type = type;
    this.params = params;
  }

  public String toJson() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(this);
    }
    catch (Exception exception) {
      return "";
    }
  }
  
  public String toString() {
    return "id: " + this.id
      + ", target: " + this.target
      + ", type: " + this.type
      + ", params: " + this.params;
  }
}