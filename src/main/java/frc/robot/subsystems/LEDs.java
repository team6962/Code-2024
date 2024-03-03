package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.Constants;

import java.awt.Color;

import com.github.tommyettinger.colorful.oklab.ColorTools;

public class LEDs extends SubsystemBase {
  private static AddressableLED strip;
  private static AddressableLEDBuffer buffer;
  private RobotStateController stateController;
  private static int length = 96;
  private static State state = State.OFF;
  
  public static enum State {
    OFF,
    DISABLED,
    NO_NOTE,
    DRIVING_TELEOP,
    HAS_NOTE,
    SHOOTING_WARMUP,
    AIMING,
    AIMED,
    SHOOTING_SPEAKER,
    
    
  }

  public static int[] ANTARES_BLUE = { 36, 46, 68 };
  public static int[] ANTARES_YELLOW = { 255, 100, 0 };
  public static int[] GREEN = { 86, 211, 100 };
  
  public LEDs(RobotStateController stateController) {
    this.stateController = stateController;
    strip = new AddressableLED(1);
    buffer = new AddressableLEDBuffer(length);
    strip.setLength(buffer.getLength());

    strip.setData(buffer);
    strip.start();
  }

  @Override
  public void periodic() {
    //setState(State.SHOOTING_SPEAKER);
    
    switch (state) {
      case OFF:
        setColor(0, length, new int[] {0, 0, 0});
        break;
      case DISABLED:
        setRainbow(0, length);
        break;
      case NO_NOTE:
        setColor(0, length, ANTARES_BLUE);
        break;
      case DRIVING_TELEOP:
        setBumperColorWave(0, length);
        break;
      case HAS_NOTE:
        setColor(0, length, ANTARES_YELLOW);
        break;
      case SHOOTING_WARMUP:
        setColorWave(0, length, ANTARES_YELLOW, this.stateController.getShooterVelocity() / 250);
        break;
      case AIMING:
        setColorWave(0, length, ANTARES_YELLOW, 2.5);
        break;
      case AIMED:
        setColor(0, length, GREEN);
        break;
      case SHOOTING_SPEAKER:
        setColorFlash(0, length, getBumperColor(), 5);
        break;
      
      
    }
    strip.setData(buffer);
    clear();

    state = State.OFF;
  }

  @Override
  public void simulationPeriodic() {
    //setState(State.DISABLED);
  }

  public static Command setStateCommand(State state) {
    return Commands.run(() -> setState(state));
  }

  public static void setState(State state) {
    if (state.ordinal() > LEDs.state.ordinal()) LEDs.state = state;
  }

  private static void setColor(int pixel, int[] RGB) {
    buffer.setRGB(pixel, RGB[0], RGB[1], RGB[2]);
  }

  private static void setColor(int start, int stop, int[] RGB) {
    for (int pixel = start; pixel < stop; pixel++) {
      setColor(pixel, RGB);
    }
  }

  private static void setRainbow(int start, int stop) {
    double time = Timer.getFPGATimestamp();
    for (int pixel = start; pixel < stop; pixel++) {
      int[] rgb = HCLtoRGB(new double[] {(pixel / 100.0 + time * 1.0) % 1.0, 0.2, 0.6});
      setColor(pixel, rgb);
    }
  }

  private static void setColorFlash(int start, int stop, int[] RGB, double speed) {
    double time = Timer.getFPGATimestamp();

    double val = (time * speed) % 1.0;
    if (val < 0.5) {
      setColor(0, length, RGB);
    } else {
      setColor(0, length, new int[] {0, 0, 0});
    }
    
  }

  private static void setColorWave(int start, int stop, int[] RGB, double speed) {
    double time = Timer.getFPGATimestamp();
    for (int pixel = start; pixel < stop; pixel++) {
      double val = (pixel / 200.0 + time * speed) % 1.0;
      if (val < 0.5) {
        setColor(pixel, RGB);
      } else {
        setColor(pixel, new int[] {0, 0, 0});
      }
    }
  }

  private static void setColorWave(int start, int stop, int[] firstRGB, int[] secondRGB, double speed) {
    double time = Timer.getFPGATimestamp();
    for (int pixel = start; pixel < stop; pixel++) {
      double val = (pixel / 200.0 + time * speed) % 1.0;
      if (val < 0.5) {
        setColor(pixel, firstRGB);
      } else {
        setColor(pixel, secondRGB);
      }
    }
  }

  private static int[] getBumperColor() {
    if (Constants.IS_BLUE_TEAM) {
      return ANTARES_BLUE;  
    } else {
      return new int[] {255, 0, 0};
    }
  }

  private static void setBumperColorWave(int start, int stop) {
    if (Constants.IS_BLUE_TEAM) {
      setColorWave(start, stop, getBumperColor(), new int[] {179, 0, 255}, 2.5);
    } else {
      setColorWave(start, stop, new int[] {255, 0, 0},  getBumperColor(), 2.5);
    }
    
  }

  //private static void setAcceleratingColorWav

  private static void clear() {
    setColor(0, length, new int[] {0, 0, 0});
  }

  private static int[] HCLtoRGB(double[] HCL) {
    float OKLAB = ColorTools.oklabByHCL((float) HCL[0], (float) HCL[1], (float) HCL[2], (float) 1.0);
    return new int[] {ColorTools.redInt(OKLAB), ColorTools.greenInt(OKLAB), ColorTools.blueInt(OKLAB)};
  }
}
