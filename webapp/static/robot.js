/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
class Robot
{
  static constructor() {
    Robot.currentTarget = 0;
    Robot.map = new Map();
  }

  static $(target) {
    if (!Robot.map.has(target)) {
      let robot = new Robot(target);
      Robot.map.set(target, robot);
    }
    return Robot.map.get(target);
  }

  constructor(target) {
    this.target = target;
    this.heading = 90;
    this.xCurrent = 0;
    this.yCurrent = 0;
    this.isWorking = false;
    this.leftTicks = 0;
    this.rightTicks = 0;
    this.motorCount = 0;
    this.flashCount = 0;
    this.soundCount = 0;
    this.ctxRobot = Canvas.addRobotCanvas(target);

    this.ledsFinch = "";
    this.lightsFinch = null;
    this.lightFinch0 = [0,0,0];
    this.lightFinch1 = [0,0,0];
    this.lightFinch2 = [0,0,0];
    this.lightFinch3 = [0,0,0];
    this.lightFinch4 = [0,0,0];

    switch (target) {
      case 1: this.color = "red"; break;
      case 2: this.color = "green"; break;
      case 3: this.color = "blue"; break;
      case 4: this.color = "orange"; break;
      case 5: this.color = "yellow"; break;
      case 6: this.color = "purple"; break;
      default: this.color = "black";
    }
  }
}
Robot.constructor();
