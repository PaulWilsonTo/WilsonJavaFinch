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

public class Message
{
  public UUID sourceId;
  public String command;
  public HashMap<String, Object> response;

  public Message() {} // For JSON Deserialization

  public Message(HashMap<String, Object> message) {
    this.sourceId = (UUID)message.get("sourceId");
    this.command = (String)message.getOrDefault("command", null);
    this.response = (HashMap<String, Object>)message.getOrDefault("response", null);
  }
  
  public String toString() {
    return "sourceId: " + this.sourceId
      + ", command: " + this.command
      + ", response: " + this.response;
  }
}
