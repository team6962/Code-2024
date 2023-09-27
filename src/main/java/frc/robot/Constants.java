// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot;

import com.pathplanner.lib.auto.PIDConstants;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import frc.robot.subsystems.*;

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
  public static final class EnabledSystems {
    public static final boolean ENABLE_DRIVE = true;
    public static final boolean ENABLE_LIMELIGHT = false;
    public static final boolean ENABLE_DASHBOARD = true;
  }

  public static final class Logging {
    public static final boolean ENABLE_DRIVE = true;
    public static final boolean ENABLE_PDP = true;
    public static final boolean ENABLE_ROBOT_CONTROLLER = true;
    public static final boolean ENABLE_DRIVER_STATION = true;
    public static final double LOGGING_PERIOD = 0.02; // in seconds
  }

  // DEVICES
  public static final class Devices {
    public static final int USB_XBOX_CONTROLLER = 0;
  }

  // DASHBOARD (ShuffleBoard)
  public static final class DashboardConstants {
    public static final String TAB_NAME = "SwerveDrive";
  }

  // LIMELIGHT
  public static final class LimelightConstants {
    public static final String NAME = "limelight";
  }

  // SWERVE DRIVE
  public static final class SwerveDriveConstants {

    /*
      -------------------------------------
      | SIMPLE CONFIG, FEEL FREE TO EDIT! |
      -------------------------------------
    */

    public static final double TELEOP_DRIVE_POWER = 0.3; // Percent driving power (0.2 = 20%), left trigger bypasses this value
    public static final double TELEOP_SLOW_DRIVE_POWER = 0.1; // Percent driving power when using the DPad
    public static final double TELEOP_ROTATE_POWER = 0.3; // Percent rotating power (0.4 = 40%)

    public static final double TELEOP_MAX_ACCELERATION = 25.0; // Measured in m/s^2
    public static final double TELEOP_MAX_ANGULAR_ACCELERATION = 25.0; // Measured in rad/s^2
    public static final double WHEEL_MAX_ACCELERATION = 25.0; // Measured in m/s^2

    public static final double MOTOR_POWER_HARD_CAP = 1.0; // Only use for testing, otherwise set to 1.0

    public static final double JOYSTICK_DEADZONE = 0.1; // If joystick values are less than this (0.2 = 20%) than we just read 0
    public static final double VELOCITY_DEADZONE = 0.25; // speed at which we stop moving all together (meters)

    public static final int TOTAL_CURRENT_LIMIT = 300; // [TODO] Default is around 640 Amps (also drive motors have two times more current allocation than steer motors)
    public static final double MOTOR_RAMP_RATE_SECONDS = 0.05; // [TODO] Seconds that it takes to go from 0 - 100% motor power

    public static final Pose2d STARTING_POSE = new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0));
    public static final double STARTING_ANGLE_OFFSET = 0.0;

    public static final String MOTION_RECORDING_WRITE_FILE = "/u/test.csv";
    public static final String MOTION_RECORDING_READ_FILE = "/u/test.csv";

    /*
      -------------------------------------------------------------------
      | ADVANCED CONFIG, DO NOT EDIT UNLESS YOU KNOW WHAT YOU'RE DOING! |
      -------------------------------------------------------------------
    */

    // PHYSICAL
    public static final double TRACKWIDTH_METERS = 0.6477; // left-to-right distance between the drivetrain wheels
    public static final double WHEELBASE_METERS = 0.62865; // front-to-back distance between the drivetrain wheels
    public static final double WHEEL_DIAMETER = 0.1016; // measured in meters
    public static final double DRIVE_GEAR_REDUCTION = 1.0 / 6.75;
    public static final double STEER_GEAR_REDUCTION = 7.0 / 150.0;
    public static final double[] STEER_ENCODER_OFFSETS = { -124.805, -303.047, -101.602, -65.215 };

    public static final double FULL_POWER_DRIVE_MOTOR_RPM = 5676.0; // VERY IMPORTANT DO NOT CHANGE
    public static final double FULL_POWER_STEER_MOTOR_RPM = 5676.0; // VERY IMPORTANT DO NOT CHANGE

    public static final double DRIVE_METERS_PER_MOTOR_ROTATION = DRIVE_GEAR_REDUCTION * WHEEL_DIAMETER * Math.PI;
    public static final double STEER_RADIANS_PER_MOTOR_ROTATION = STEER_GEAR_REDUCTION * Math.PI * 2.0;

    public static final double FULL_POWER_DRIVE_VELOCITY = (FULL_POWER_DRIVE_MOTOR_RPM / 60) * DRIVE_METERS_PER_MOTOR_ROTATION; // m/s (should be around 4.4196)
    public static final double FULL_POWER_ROTATE_VELOCITY = SwerveMath.wheelVelocityToRotationalVelocity(FULL_POWER_DRIVE_VELOCITY); // rad/s (should be around 4.4196)
    public static final double FULL_POWER_STEER_VELOCITY = (FULL_POWER_STEER_MOTOR_RPM / 60) * STEER_RADIANS_PER_MOTOR_ROTATION; // rad/s

    // PID
    public static final double[] MODULE_STEER_PID = { 10.0, 0.0, 0.0 }; // [TODO]
    public static final double MODULE_STEER_PID_TOLERANCE = 1.0; // In degrees

    public static final double[] TELEOP_ROTATE_PID = { 4.0, 0.0, 0.0 }; // [TODO]
    public static final double TELEOP_ROTATE_PID_TOLERANCE = 1.0; // In degrees

    public static final PIDConstants AUTO_ROTATE_PID = new PIDConstants(4.0, 0.0, 0.0); // [TODO]
    public static final PIDConstants AUTO_MOVE_PID = new PIDConstants(3.5, 0.0, 0.0); // [TODO]

    // AUTONOMOUS
    public static final double AUTO_MAX_VELOCITY = 4.0; // [TODO] measured in meters/sec
    public static final double AUTO_MAX_ACCELERATION = 3.0; // [TODO] measured in meters/sec^2
    public static final double AUTO_MAX_ANGULAR_VELOCITY = SwerveMath.wheelVelocityToRotationalVelocity(AUTO_MAX_VELOCITY); // measured in radians/sec
    public static final double AUTO_MAX_ANGULAR_ACCELERATION = SwerveMath.wheelVelocityToRotationalVelocity(AUTO_MAX_ACCELERATION); // measured in rad/sec^2
    public static final TrapezoidProfile.Constraints AUTO_ANGLE_CONSTRAINTS = new TrapezoidProfile.Constraints(AUTO_MAX_ANGULAR_VELOCITY, AUTO_MAX_ANGULAR_ACCELERATION);

    // MODULES
    // In order of: front left, front right, back left, back right, where the battery is in the back
    public static final String[] MODULE_NAMES = { "FL", "FR", "BL", "BR" };
  }

  public static final class SwerveMath {

    // Convert wheel velocity in m/s to rotational velocity in rad/s
    public static double wheelVelocityToRotationalVelocity(double maxDriveVelocity) {
      return maxDriveVelocity / Math.hypot(SwerveDriveConstants.TRACKWIDTH_METERS / 2.0, SwerveDriveConstants.WHEELBASE_METERS / 2.0);
    }

    // Convert rotation velocity in rad/s to wheel velocity in m/s
    public static double rotationalVelocityToWheelVelocity(double maxAngularVelocity) {
      return maxAngularVelocity * Math.hypot(SwerveDriveConstants.TRACKWIDTH_METERS / 2.0, SwerveDriveConstants.WHEELBASE_METERS / 2.0);
    }

    // Convert steer velocity in rad/s to motor power from 0 - 1
    public static double steerVelocityToMotorPower(double velocity) {
      return velocity / SwerveDriveConstants.FULL_POWER_STEER_VELOCITY;
    }

    // Convert motor power from 0 - 1 to steer velocity in rad/s
    public static double motorPowerToSteerVelocity(double power) {
      return power * SwerveDriveConstants.FULL_POWER_STEER_VELOCITY;
    }

    // Convert drive velocity in m/s to motor power from 0 - 1
    public static double wheelVelocityToMotorPower(double velocity) {
      return velocity / SwerveDriveConstants.FULL_POWER_DRIVE_VELOCITY;
    }

    // Convert motor power from 0 - 1 to drive velocity in m/s
    public static double motorPowerToWheelVelocity(double power) {
      return power * SwerveDriveConstants.FULL_POWER_DRIVE_VELOCITY;
    }

    // Calculate swerve drive kinematics
    public static SwerveDriveKinematics getKinematics() {
      return new SwerveDriveKinematics(new Translation2d(SwerveDriveConstants.TRACKWIDTH_METERS / 2.0, SwerveDriveConstants.WHEELBASE_METERS / 2.0), new Translation2d(SwerveDriveConstants.TRACKWIDTH_METERS / 2.0, -SwerveDriveConstants.WHEELBASE_METERS / 2.0), new Translation2d(-SwerveDriveConstants.TRACKWIDTH_METERS / 2.0, SwerveDriveConstants.WHEELBASE_METERS / 2.0), new Translation2d(-SwerveDriveConstants.TRACKWIDTH_METERS / 2.0, -SwerveDriveConstants.WHEELBASE_METERS / 2.0));
    }
  }

  public static final class InputMath {
    public static double addLinearDeadzone(double input, double deadzone) { // input ranges from -1 to 1
      if (Math.abs(input) <= deadzone) {
        return 0.0;
      }

      if (input > 0) {
        return map(input, deadzone, 1.0, 0.0, 1.0);
      }

      return map(input, -deadzone, -1.0, 0.0, -1.0);
    }

    public static double mapBothSides(double X, double A, double B, double C, double D) {
      if (X > 0.0) {
        return map(X, A, B, C, D);
      }
      if (X < 0.0) {
        return map(X, -A, -B, -C, -D);
      }
      return 0.0;
    }

    public static double[] addCirculuarDeadzone(double[] input, double deadzone) { // input ranges from -1 to 1
      double magnitude = Math.hypot(input[0], input[1]);
      double direction = Math.atan2(input[1], input[0]);

      if (Math.abs(magnitude) <= deadzone) {
        return new double[] { 0.0, 0.0 };
      }

      magnitude = map(magnitude, deadzone, 1.0, 0.0, 1.0);

      return new double[] { magnitude * Math.cos(direction), magnitude * Math.sin(direction) };
    }
  }

  public static final class CAN {
    // In order of: front left, front right, back left, back right, where the battery is in the back
    public static final int[] SWERVE_DRIVE = { 10, 20, 30, 40 };
    public static final int[] SWERVE_STEER = { 11, 21, 31, 41 };
    public static final int[] SWERVE_STEER_CANCODER = { 12, 22, 32, 42 };
    public static final int PDP = 5;
  }

  public static double map(double X, double A, double B, double C, double D) {
    return (X - A) / (B - A) * (D - C) + C;
  }
}
