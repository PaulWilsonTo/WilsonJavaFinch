/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
class Command
{
  constructor(command) {
    this.id = command.id;
    this.target = command.target;
    this.type = command.type;
    this.params = command.params;
  }

  toString() {
    return "Command id: " + this.id
      + ", target: " + this.target
      + ", type: " + this.type
      + ", params: " + this.params
  }
}