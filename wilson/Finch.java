/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package wilson;

import java.util.HashMap;
import wilson.internal.*;

public class Finch
{ 
  private int target;
  private String device;
  private String name;
  private int version;
  private double speed;
  private int[] beakRGB;
  private int[] tail1RGB;
  private int[] tail2RGB;
  private int[] tail3RGB;
  private int[] tail4RGB;
  public HashMap<String, Object> status;
  public HashMap<String, Object> start;

  public Finch(String prompt, double initX, double initY) {
    this.target = Worker.nextTarget();
    Common.debug("FINCH init", prompt, this.target);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("prompt", prompt);
    params.put("initX", initX);
    params.put("initY", initY);
    Command command = new Command(target, "init", params);
    Worker.queueCommand(command);

    HashMap<String, Object> response = Worker.getResponse();
    this.device = (String)response.get("device");
    this.name = (String)response.get("name");
    this.version = (int)response.get("version");

    this.speed = 5.0;
    this.beakRGB = new int[] {0, 0, 0};
    this.tail1RGB = new int[] {0, 0, 0};
    this.tail2RGB = new int[] {0, 0, 0};
    this.tail3RGB = new int[] {0, 0, 0};
    this.tail4RGB = new int[] {0, 0, 0};

    Worker.trackFinch(target, this);
    Worker.queueStatus(command);
    this.resetTicks();
  }
  
  public Finch() {
    this("Connect Finch", 0, 0);
  }

  public int getTarget() {
    return this.target;
  }

  public String getDevice() {
    return this.device;
  }

  public String getName() {
    return this.name;
  }

  public int getVersion() {
    return this.version;
  }

  public double speed() {
    Common.debug("FINCH speed", this.target);
    return this.speed;
  }

  public void speed(double factor) {
    Common.debug("FINCH speed", this.target, factor);
    this.speed = Math.max(1.0, Math.min(10.0, factor));
  }

  public void stopAll() {
    Common.debug("FINCH stopAll", this.target);
    int[] cmdBytes = Device.getStopAllBytes();
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    Command command = new Command(this.target, "stop", params);
    Worker.queueCommand(command);
  }

  public void forward(double distance) {
    Common.debug("FINCH forward", this.target, distance);
    this.start = this.status;
    int[] cmdBytes = Device.getForwardBytes(distance, this.speed);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    params.put("distance", distance);
    params.put("speed", this.speed);
    Command command = new Command(this.target, "forward", params);
    Worker.queueCommand(command);
    Worker.queueStatus(command);
  }

  public void backward(double distance) {
    this.forward(-distance);
  }

  public void right(double angle) {
    Common.debug("FINCH right", this.target, angle);
    this.start = this.status;
    int[] cmdBytes = Device.getRightBytes(angle, this.speed);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    params.put("angle", angle);
    params.put("speed", this.speed);
    Command command = new Command(this.target, "right", params);
    Worker.queueCommand(command);
    Worker.queueStatus(command);
  }

  public void left(double angle) {
    this.right(-angle);
  }

  public void runMotors(double leftSpeed, double rightSpeed) {
    Common.debug("FINCH runMotors", this.target, leftSpeed, rightSpeed);
    double leftFactor = Math.max(-10.0, Math.min(10.0, leftSpeed));
    double rightFactor = Math.max(-10.0, Math.min(10.0, rightSpeed));
    int[] cmdBytes = Device.getRunMotorsBytes(leftSpeed, rightSpeed);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    params.put("left", leftFactor);
    params.put("right", rightFactor);
    Command command = new Command(this.target, "motors", params);
    Worker.queueCommand(command);
  }

  public Sensor getSensors() {
    return new Sensor(this.status, this.version);
  }

  public void beakColor(int red, int green, int blue) {
    Common.debug("FINCH beakColor", this.target, red, green, blue);
    this.setLight(-1, red, green, blue);
    this.setLights();
  }

  public void tailColor(int index, int red, int green, int blue) {
    Common.debug("FINCH beakColor", this.target, index, red, green, blue);
    this.setLight(index, red, green, blue);
    this.setLights();
  }

  private void setLight(int index, int red, int green, int blue) {
    switch (index) {
      case 1: this.tail1RGB = new int[] {red, green, blue}; break;
      case 2: this.tail2RGB = new int[] {red, green, blue}; break;
      case 3: this.tail3RGB = new int[] {red, green, blue}; break;
      case 4: this.tail4RGB = new int[] {red, green, blue}; break;
      case -1: this.beakRGB = new int[] {red, green, blue}; break;
      case 0:
        for (int light = 1; light <= 4; light++) {
          this.setLight(light, red, green, blue);
        }
        break;
      default:
        this.setLight(-1, red, green,blue);
        for (int light = 1; light <= 4; light++) {
          this.setLight(light, red, green, blue);
        }
    }
  }

  private void setLights() {
    int[] cmdBytes = Device.getLightBytes(this.beakRGB,
      this.tail1RGB, this.tail2RGB, this.tail3RGB, this.tail4RGB
    );
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    params.put("beak", this.beakRGB);
    params.put("tail1", this.tail1RGB);
    params.put("tail2", this.tail2RGB);
    params.put("tail3", this.tail3RGB);
    params.put("tail4", this.tail4RGB);
    Command command = new Command(this.target, "lights", params);
    Worker.queueCommand(command);
  }

  public void flashLeds(String phrase) {
    Common.debug("FINCH flashLeds", this.target, phrase);
    int[][] cmdBytes = Device.getLedFlashBytes(phrase);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    params.put("phrase", phrase);
    Command command = new Command(this.target, "flash", params);
    Worker.queueCommand(command);
  }

  public void customLeds(LedGrid ledGrid) {
    Common.debug("FINCH customLeds", this.target, ledGrid);
    int[] cmdBytes = Device.getLedGridBytes(ledGrid);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    params.put("ledGrid", ledGrid.getList());
    Command command = new Command(this.target, "leds", params);
    Worker.queueCommand(command);
  }

  public void playSound(int midiNote, int duration) {
    Common.debug("FINCH playSound", this.target, midiNote, duration);
    Music music = new Music();
    music.addNote(midiNote, duration);
    this.playMusic(music);
  }

  public void playMusic(Music music) {
    Common.debug("FINCH playMusic", this.target, music);
    int[][] cmdBytes = Device.getMusicBytes(music);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    params.put("music", music.getList());
    Command command = new Command(this.target, "sound", params);
    Worker.queueCommand(command);
  }

  public void resetTicks() {
    Common.debug("FINCH resetTicks", this.target);
    int[] cmdBytes = Device.getResetTicksBytes();
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    Command command = new Command(this.target, "reset", params);
    Worker.queueCommand(command);
    Worker.queueStatus(command);
  }

  public void calibrate() {
    Common.debug("FINCH calibrate", this.target);
    int[] cmdBytes = Device.getCalibrationBytes();
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("bytes", cmdBytes);
    Command command = new Command(this.target, "calibrate", params);
    Worker.queueCommand(command);
    Worker.queueStatus(command);
  }

  public static String input(String prompt) {
    Common.debug("FINCH input", prompt);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("prompt", prompt);
    Command command = new Command(0, "input", params);
    Worker.queueCommand(command);
    HashMap<String, Object> response = Worker.getResponse();
    return (String)response.get("input");
  }

  public static void print(Object... message) {
    Common.debug("FINCH print", message);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("message", message);
    Command command = new Command(0, "print", params);
    Worker.queueCommand(command);
  }

  public static void debug(Object... message) {
    Common.debug("FINCH debug", message);
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("message", message);
    Command command = new Command(0, "debug", params);
    Worker.queueCommand(command);
  }

  public static void sleep(int milliseconds) {
    Common.debug("FINCH sleep", milliseconds);
    try {
      Thread.sleep(milliseconds);
    }
    catch (Exception exception) {
      // Do Nothing
    }
  }
}