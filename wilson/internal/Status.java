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

public class Status
{
  public UUID sourceId;
  public int target;
  public HashMap<String, Object> status;

  public Status() {} // For JSON Deserialization

  public Status(HashMap<String, Object> status) {
    this.sourceId = (UUID)status.get("sourceId");
    this.target = (int)status.getOrDefault("target", 0);
    this.status = (HashMap<String, Object>)status.getOrDefault("status", null);
  }
  
  public String toString() {
    return "sourceId: " + this.sourceId
      + ", target: " + this.target
      + ", status: " + this.status;
  }

  public static boolean getIsWorking(HashMap<String, Object> status) {
    return ((int)status.get("4") / 128 == 1);
  }

  public static int getLeftTicks(HashMap<String, Object> status) {
    return combineBytes((int)status.get("9"), (int)status.get("8"), (int)status.get("7"));
  }

  public static int getRightTicks(HashMap<String, Object> status) {
    return combineBytes((int)status.get("12"), (int)status.get("11"), (int)status.get("10"));
  }

  public static int getDistance(HashMap<String, Object> status, int version) {
    if (version == 1) return combineBytes((int)status.get("1"), (int)status.get("0"));
    else if (version == 2) return (int)status.get("1");
    else return 0;
  }
  
  public static int getLeftLine(HashMap<String, Object> status) {
    return (int)status.get("4") % 128;
  }

  public static int getRightLine(HashMap<String, Object> status) {
    return (int)status.get("5");
  }
  
  public static int getLeftLight(HashMap<String, Object> status) {
    return (int)status.get("2");
  }

  public static int getRightLight(HashMap<String, Object> status) {
    return (int)status.get("3");
  }

  public static int getSound(HashMap<String, Object> status, int version) {
    if (version == 2) return (int)status.get("0");
    else return 0;
  }

  public static int getBattery(HashMap<String, Object> status, int version) {
    if (version == 1) return (int)status.get("6");
    else if (version == 2) return (int)status.get("6") % 4;
    else return 0;
  }

  public static int getTemperature(HashMap<String, Object> status, int version) {
    if (version == 2) return (int)status.get("6") / 4;
    else return 0;
  }

  public static double getAccelerometer(HashMap<String, Object> status, String axis) {
    double rads = Math.toRadians(40);
    if (axis.equalsIgnoreCase("X")) {
      return (int)status.get("13");
    }
    else if (axis.equalsIgnoreCase("Y")) {
      return Math.cos(rads) * (int)status.get("14") - Math.sin(rads) * (int)status.get("15");
    }
    else if (axis.equalsIgnoreCase("Z")) {
      return Math.sin(rads) * (int)status.get("14") + Math.cos(rads) * (int)status.get("15");
    }
    else return 0;
  }

  public static double getMagnetometer(HashMap<String, Object> status, String axis) {
    double rads = Math.toRadians(40);
    if (axis.equalsIgnoreCase("X")) {
      return (int)status.get("17");
    }
    else if (axis.equalsIgnoreCase("Y")) {
      return Math.cos(rads) * (int)status.get("18") + Math.sin(rads) * (int)status.get("19");
    }
    else if (axis.equalsIgnoreCase("Z")) {
      return Math.cos(rads) * (int)status.get("19") - Math.sin(rads) * (int)status.get("18");
    }
    else return 0;
  }

  public static boolean getTouch(HashMap<String, Object> status, int version) {
    if (version == 2) return (((int)status.get("16") % 4) / 2 == 0);
    else return false;
  }

  public static boolean getButton(HashMap<String, Object> status, String button) {
    if (button.equalsIgnoreCase("A")) return (((int)status.get("16") % 32) / 16 == 0);
    else if (button.equalsIgnoreCase("B")) return (((int)status.get("16") % 64) / 32 == 0);
    else return false;
  }
  
  public static int combineBytes(int lowByte, int medByte, int highByte) {
    return highByte * 256 * 256 + medByte * 256 + lowByte;
  }
  
  public static int combineBytes(int lowByte, int medByte) {
    return combineBytes(lowByte, medByte, 0);
  }

  public static boolean getIsShaking(HashMap<String, Object> status) {
    return ((int)status.get("16") % 2 == 1);
  }

  public static boolean getIsCalibrated(HashMap<String, Object> status) {
    return (((int)status.get("16") % 8) / 4 == 1);
  }

  public static int getCompass(HashMap<String, Object> status) {
    double xAccel = Status.getAccelerometer(status, "X");
    double yAccel = Status.getAccelerometer(status, "Y");
    double zAccel = Status.getAccelerometer(status, "Z");

    double xMagnet = Status.getMagnetometer(status, "X");
    double yMagnet = Status.getMagnetometer(status, "Y");
    double zMagnet = Status.getMagnetometer(status, "Z");

    double phi;
    if (zAccel != 0) {
      phi = Math.atan(-yAccel / zAccel);
    }
    else {
      phi = Math.PI / 2;
    }
      
    double denom = yAccel * Math.sin(phi) + zAccel * Math.cos(phi);
    double theta;
    if (denom != 0) {
      theta = Math.atan(xAccel / denom);
    }
    else {
      theta = Math.PI / 2;
    }

    double xp = xMagnet;
    double yp = yMagnet * Math.cos(phi) - zMagnet * Math.sin(phi);
    double zp = yMagnet * Math.sin(phi) + zMagnet * Math.cos(phi);
    double xpp = xp * Math.cos(theta) + zp * Math.sin(theta);
    double ypp = yp;
    
    double angle = 180 + Math.toDegrees(Math.atan2(xpp, ypp));
    int compass = (int)(Math.round(angle) + 180) % 360;
    return compass;
  }
}
