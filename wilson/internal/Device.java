/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package wilson.internal;

import java.util.ArrayList;
import wilson.*;

public class Device
{
  public static int[] getCalibrationBytes() {
    return new int[] {0xCE, 0xFF, 0xFF, 0xFF};
  }

  public static int[] getResetTicksBytes() {
    return new int[] {0xD5};
  }

  public static int[] getStopAllBytes() {
    return new int[] {0xDF};
  }

  public static int[] getRunMotorsBytes(double leftSpeedFactor, double rightSpeedFactor) {
    int leftMotorSpeed = Device.getMotorSpeed(Math.abs(leftSpeedFactor), leftSpeedFactor > 0);
    int rightMotorSpeed = Device.getMotorSpeed(Math.abs(rightSpeedFactor), rightSpeedFactor > 0);
    return new int[] {0xD2, 0x40,
      leftMotorSpeed, 0, 0, 0,
      rightMotorSpeed, 0, 0, 0
    };
  }

  public static int[] getForwardBytes(double distance, double speedFactor) {
    int motorSpeed = Device.getMotorSpeed(speedFactor, distance >= 0);
    int moveTicks = Device.getMoveTicks(distance);
    int[] tickBytes = Device.splitBytes(moveTicks);
    return new int[] {0xD2, 0x40,
      motorSpeed, tickBytes[2], tickBytes[1], tickBytes[0],
      motorSpeed, tickBytes[2], tickBytes[1], tickBytes[0]
    };
  }

  public static int[] getRightBytes(double angle, double speedFactor) {
    int leftSpeed = Device.getMotorSpeed(speedFactor, angle > 0);
    int rightSpeed = Device.getMotorSpeed(speedFactor, angle < 0);
    int turnTicks = Device.getTurnTicks(angle);
    int[] tickBytes = Device.splitBytes(turnTicks);
    return new int[] {0xD2, 0x40,
      leftSpeed , tickBytes[2], tickBytes[1], tickBytes[0],
      rightSpeed, tickBytes[2], tickBytes[1], tickBytes[0]
    };
  }

  public static int[] getLightBytes(int[] beak, int[] tail1, int[] tail2, int[] tail3, int[] tail4) {
    return new int[] {0xD0, beak[0], beak[1], beak[2],
      tail1[0], tail1[1], tail1[2],
      tail2[0], tail2[1], tail2[2],
      tail3[0], tail3[1], tail3[2],
      tail4[0], tail4[1], tail4[2],
      0x00, 0x00, 0x00, 0x00
    };
  }

  public static int[][] getLedFlashBytes(String phrase) {
    int[][] listBytes = new int[phrase.length()][];
    for (int index = 0; index < phrase.length(); index++) {
      listBytes[index] = new int[] {0xD2, 0x01, (int)phrase.charAt(index)};
    }
    return listBytes;
  }

  public static int[] getLedGridBytes(LedGrid ledGrid) {
    try {
      int[] leds = ledGrid.getList();
      int led25only = leds[24];
      int led24to17 = 0;
      for (int index = 16; index < 24; index++) {
        led24to17 += leds[index] * Math.pow(2, index - 16);
      }
      int led16to09 = 0;
      for (int index = 8; index < 16; index++) {
        led16to09 += leds[index] * Math.pow(2, index - 8);
      }
      int led08to01 = 0;
      for (int index = 0; index < 8; index++) {
        led08to01 += leds[index] * Math.pow(2, index);
      }
      return new int[] {0xD2, 0x20, led25only, led24to17, led16to09, led08to01};
    }
    catch (Exception exception) {
      return new int[] {0xD2, 0x20, 0, 0, 0, 0};
    }
  }

  public static int[][] getMusicBytes(Music music) {
    ArrayList<Music.Note> notes = music.getList();
    int[][] listBytes = new int[notes.size()][];
    for (int index = 0; index < notes.size(); index++) {
      Music.Note note = notes.get(index);
      int period = 0;
      if (note.midiNote > 0) {
        double frequency = 440.0 * Math.pow(2, (note.midiNote - 69) / 12.0);
        period = (int)Math.round(1000000.0 / frequency);
      }

      int[] noteBytes = Device.splitBytes(period);
      int[] timeBytes = Device.splitBytes(note.duration);
      listBytes[index] = new int[] {0xD0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        noteBytes[1], noteBytes[0], timeBytes[1], timeBytes[0]
      };
    }
    return listBytes;
  }

  public static int getMotorSpeed(double speedFactor, boolean isForward) {
    int motorSpeed = (int)Math.round(36.0 * speedFactor / 10.0);
    if (motorSpeed < 3) motorSpeed = 0;
    if (motorSpeed > 0 && isForward) motorSpeed += 128;
    return motorSpeed;
  }

  public static int getMoveTicks(double distance) {
    return (int)Math.floor(49.7 * Math.abs(distance));
  }

  public static int getTurnTicks(double angle) {
    return (int)Math.floor(4.335 * Math.abs(angle));
  }

  public static int[] splitBytes(int number) {
    int lowByte = number % 256;
    int medByte = (number / 256) % 256;
    int highByte = (number / (256*256)) % 256;
    return new int[] {lowByte, medByte, highByte};
  }
}
