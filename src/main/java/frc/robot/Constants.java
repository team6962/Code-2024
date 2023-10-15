// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import frc.robot.subsystems.SwerveDrive;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

  // ENABLED SYSTEMS
  public static final class ENABLED_SYSTEMS {
    public static final boolean ENABLE_DRIVE     = true;
    public static final boolean ENABLE_LIMELIGHT = false;
    public static final boolean ENABLE_DASHBOARD = true;
  }

  public static final class LOGGING {
    public static final boolean ENABLE_SWERVE_DRIVE     = false;
    public static final boolean ENABLE_PDH              = false;
    public static final boolean ENABLE_ROBOT_CONTROLLER = false;
    public static final boolean ENABLE_DRIVER_STATION   = false;
  }

  // DEVICES
  public static final class DEVICES {
    public static final int USB_XBOX_CONTROLLER = 0;
  }

  // DASHBOARD (ShuffleBoard)
  public static final class DASHBOARD {
    public static final String TAB_NAME = "SwerveDrive";
  }

  // LIMELIGHT
  public static final class LIMELIGHT {
    public static final String NAME = "limelight";
  }

  // SWERVE DRIVE
  public static final class SWERVE_DRIVE {

    /*
      -------------------------------------
      | SIMPLE CONFIG, FEEL FREE TO EDIT! |
      -------------------------------------
    */
    
    public static final double   ROBOT_MASS                         = 30; // kg
    public static final double   COEFFICIENT_OF_FRICTION            = 1.0; // 1.0 when on carpet 0.5 on KLS flooring

    // TELEOPERATED POWER
    public static final double   TELEOPERATED_DRIVE_POWER           = 0.5; // Percent driving power (0.2  = 20%)
    public static final double   TELEOPERATED_SLOW_DRIVE_POWER      = 0.1; // Percent driving power when using the DPad
    public static final double   TELEOPERATED_ROTATE_POWER          = 0.25; // Percent rotating power (0.4 = 40%)
    
    // TELEOPERATED ACCELERATION
    public static final double   TELEOPERATED_ACCELERATION          = 15.0; // Measured in m/s^2
    public static final double   TELEOPERATED_ANGULAR_ACCELERATION  = SwerveDrive.wheelVelocityToRotationalVelocity(TELEOPERATED_ACCELERATION); // Measured in rad/s^2
    
    // INPUT TUNING
    public static final double   JOYSTICK_DEADBAND                  = 0.05; // Inputs that we read zero at
    public static final double   VELOCITY_DEADBAND                  = 0.05; // Velocity we stop moving at

    // AUTONOMOUS
    public static final double   AUTONOMOUS_VELOCITY                = 3.0; // [TODO] measured in meters/sec
    public static final double   AUTONOMOUS_ACCELERATION            = 3.0; // [TODO] measured in meters/sec^2
    public static final double   AUTONOMOUS_ANGULAR_VELOCITY        = Math.PI; // [TODO] measured in rad/sec
    public static final double   AUTONOMOUS_ANGULAR_ACCELERATION    = Math.PI; // [TODO] measured in rad/sec^2

    // BROWNOUT PREVENTION
    public static final int      DRIVE_MOTOR_CURRENT_LIMIT          = 40;
    public static final int      STEER_MOTOR_CURRENT_LIMIT          = 20;
    public static final double   DRIVE_MOTOR_RAMP_RATE              = 0.1;
    public static final double   STEER_MOTOR_RAMP_RATE              = 0.05;
    
    // ODOMETER
    public static final Pose2d   STARTING_POSE                      = new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0));
    public static final double   STARTING_ANGLE_OFFSET              = 0.0;
    
    // TESTING
    public static final double   MOTOR_POWER_HARD_CAP               = 1.0; // Only use for testing, otherwise set to 1.0

    /*
      -------------------------------------------------------------------
      | ADVANCED CONFIG, DO NOT EDIT UNLESS YOU KNOW WHAT YOU'RE DOING! |
      -------------------------------------------------------------------
    */

    // PHYSICAL
    public static final double   CHASSIS_WIDTH                      = Units.inchesToMeters(30);
    public static final double   CHASSIS_LENGTH                     = Units.inchesToMeters(30);
    public static final double   WHEEL_FRAME_DISTANCE               = Units.inchesToMeters(2.625);
    public static final double   TRACKWIDTH                         = CHASSIS_WIDTH - WHEEL_FRAME_DISTANCE * 2.0; // left-to-right distance between the drivetrain wheels
    public static final double   WHEELBASE                          = CHASSIS_LENGTH - WHEEL_FRAME_DISTANCE * 2.0; // front-to-back distance between the drivetrain wheels
    public static final double   WHEEL_DIAMETER                     = Units.inchesToMeters(4.0); // measured in meters
    public static final double   DRIVE_MOTOR_GEAR_REDUCTION         = 1.0 / 6.75;
    public static final double   STEER_MOTOR_GEAR_REDUCTION         = 7.0 / 150.0;
    public static final double[] STEER_ENCODER_OFFSETS              = { -124.805, -303.047, -101.602, -65.215 };
    
    // GEAR AND WHEEL RATIOS
    public static final double   DRIVE_MOTOR_METERS_PER_REVOLUTION  = DRIVE_MOTOR_GEAR_REDUCTION * WHEEL_DIAMETER * Math.PI;
    public static final double   STEER_MOTOR_RADIANS_PER_REVOLUTION = STEER_MOTOR_GEAR_REDUCTION * Math.PI * 2.0;
    
    // TIP COMPENSATION
    public static final double   TIP_COMPENSATION_MIN_TILT          = 5.0;

    // REDUCE DRIVE VELOCITY WHEN FAR FROM ANGLE
    public static final boolean  DO_ANGLE_ERROR_SPEED_REDUCTION     = true;

    /*
     * MOTION PROFILING
     * kFF -> FeedForward term, multiplied by desired velocity to get an approximate motor power
     * kP -> Proportional term, multiplied by the measured error to get additional motor power
     * kI -> Integral term, uses the slope of the measured error. Essentially if the measured error isn't going away from just kP, the integral term will slowly build up additional motor power until it works out. Only really useful when there is external load on the system, like with an Arm.
     * kD -> Derivative term, essentially a damping factor to reduce oscillations produced by kP or kI
     */
    public static final class DRIVE_MOTOR_MOTION_PROFILE {
      public static final double kFF = 1.0 / (NEO.FREE_SPEED / 60 * DRIVE_MOTOR_METERS_PER_REVOLUTION);
      public static final double kP  = 0.1;
      public static final double kI  = 0.0;
      public static final double kD  = 0.0;
    }
    public static final class STEER_MOTOR_MOTION_PROFILE {
      public static final double kP  = 0.005;
      public static final double kI  = 0.0;
      public static final double kD  = 0.0;
    }
    public static final class ABSOLUTE_ROTATION_GAINS {
      public static final double kP  = 3.0;
      public static final double kI  = 0.0;
      public static final double kD  = 0.0;
    }

    // AUTONOMOUS
    public static final class AUTONOMOUS_TRANSLATION_GAINS {
      public static final double kP = 8.0;
      public static final double kI = 0.0;
      public static final double kD = 0.0;
    }
    public static final class AUTONOMOUS_ROTATION_GAINS {
      public static final double kP = 8.0;
      public static final double kI = 0.0;
      public static final double kD = 0.0;
    }
    
    // MODULES
    // In order of: front left, front right, back left, back right, where the battery is in the back
    public static final String[] MODULE_NAMES = { "FL", "FR", "BL", "BR" };
  }

  public static final class CAN {
    // In order of: front left, front right, back left, back right, where the battery is in the back
    public static final int[] SWERVE_DRIVE_SPARK_MAX = { 10, 20, 30, 40 };
    public static final int[] SWERVE_STEER_SPARK_MAX = { 11, 21, 31, 41 };
    public static final int[] SWERVE_STEER_CANCODERS = { 12, 22, 32, 42 };
    public static final int PDH = 5;
  }

  public static final class NEO {
    public static final double kV               = 493.5; // rpm / V
    public static final double kT               = 0.0181; // Nm / A
    public static final double STALL_TORQUE     = 3.28; // Nm
    public static final double STALL_CURRENT    = 181; // A
    public static final double FREE_CURRENT     = 1.3; // A
    public static final double FREE_SPEED       = 5880; // rpm
    public static final double RESISTANCE       = 0.066; // Ω
    public static final int SAFE_STALL_CURRENT  = 40; // A
    public static final double SAFE_TEMPERATURE = 65.0; // °C
  }

  public static final class SWERVE_MATH {
    // Calculate swerve drive kinematics
    public static SwerveDriveKinematics getKinematics() {
      return new SwerveDriveKinematics(
        new Translation2d( SWERVE_DRIVE.TRACKWIDTH / 2.0, SWERVE_DRIVE.WHEELBASE  / 2.0), 
        new Translation2d( SWERVE_DRIVE.TRACKWIDTH / 2.0, -SWERVE_DRIVE.WHEELBASE / 2.0), 
        new Translation2d(-SWERVE_DRIVE.TRACKWIDTH / 2.0, SWERVE_DRIVE.WHEELBASE  / 2.0), 
        new Translation2d(-SWERVE_DRIVE.TRACKWIDTH / 2.0, -SWERVE_DRIVE.WHEELBASE / 2.0));
    }

    public static double clampRadians(double radians) {
      return Units.degreesToRadians(clampDegrees(Units.radiansToDegrees(radians)));
    }

    public static double clampDegrees(double degrees) {
      return mod(degrees + 180.0, 360.0) - 180.0;
    }

    public static double angleDistance(double alpha, double beta) {
      double phi = Math.abs(beta - alpha) % (2.0 * Math.PI);
      return phi > Math.PI ? (2.0 * Math.PI) - phi : phi;
    }
  }

  public static final class INPUT_MATH {
    public static double addLinearDeadband(double input, double deadband) { // input ranges from -1 to 1
      if (Math.abs(input) <= deadband) return 0.0;
      if (input > 0) return map(input, deadband, 1.0, 0.0, 1.0);
      return map(input, -deadband, -1.0, 0.0, -1.0);
    }

    public static double mapBothSides(double X, double A, double B, double C, double D) {
      if (X > 0.0) return map(X, A, B, C, D);
      if (X < 0.0) return map(X, -A, -B, -C, -D);
      return 0.0;
    }

    public static Translation2d circular(Translation2d input, double deadband, double snapRadians) { // input ranges from -1 to 1
      double magnitude = input.getNorm();
      double direction = input.getAngle().getRadians();
      if (mod(direction, Math.PI / 2.0) <= snapRadians / 2.0 || mod(direction, Math.PI / 2.0) >= (Math.PI / 2.0) - (snapRadians / 2.0)) direction = Math.round(direction / (Math.PI / 2.0)) * (Math.PI / 2.0);
      if (Math.abs(magnitude) <= deadband) return new Translation2d();
      magnitude = nonLinear(map(magnitude, deadband, 1.0, 0.0, 1.0));
      return new Translation2d(magnitude * Math.cos(direction), magnitude * Math.sin(direction));
    }

    public static double nonLinear(double x) {
      return (1 - Math.cos(Math.abs(x) * Math.PI / 2.0)) * Math.signum(x);
    }
  }

  public static double map(double X, double A, double B, double C, double D) {
    return (X - A) / (B - A) * (D - C) + C;
  }

  public static double mod(double x, double r) {
    return ((x % r) + r) % r;
  }
}
