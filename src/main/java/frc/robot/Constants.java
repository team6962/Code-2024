// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.util.Units;
import frc.robot.subsystems.drive.SwerveDrive;

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
    public static final int LOGGING_PERIOD_MS = 20;
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

    ///////////////////////// CONFIG /////////////////////////
  
    public static final double   ROBOT_MASS                         = 25; // kg
    public static final double   FRICTION_COEFFICIENT               = 1.0; // 1.0 when on carpet 0.5 on KLS flooring
    public static final int      MODULE_COUNT                       = 4;
    public static final double   CHASSIS_WIDTH                      = Units.inchesToMeters(28);
    public static final double   CHASSIS_LENGTH                     = Units.inchesToMeters(28);
    public static final double   WHEEL_FRAME_DISTANCE               = Units.inchesToMeters(2.625);
    public static final double   WHEEL_RADIUS                       = Units.inchesToMeters(2.0); // measured in meters
    public static final double   WHEEL_WIDTH                        = Units.inchesToMeters(2.0); // measured in meters
    public static final double   DRIVE_MOTOR_GEARING                = 6.75;
    public static final double   STEER_MOTOR_GEARING                = 150.0 / 7.0;
    public static final double   GEARBOX_EFFICIENCY                 = 0.8;
    public static final double[] STEER_ENCODER_OFFSETS              = { -124.805, -303.047, -101.602, -65.215 };

    // DRIVING OPTIONS
    public static final double   TELEOPERATED_DRIVE_POWER           = 0.4; // Percent driving power (0.2  = 20%)
    public static final double   TELEOPERATED_SLOW_DRIVE_POWER      = 0.2; // Percent driving power when using the DPad
    public static final double   TELEOPERATED_BOOST_DRIVE_POWER     = 1.0; // Percent driving power when using the DPad
    public static final double   TELEOPERATED_ROTATE_POWER          = 0.4; // Percent rotating power (0.4 = 40%)
    public static final double   VELOCITY_DEADBAND                  = 0.15; // Velocity we stop moving at
    
    // ODOMETER
    public static final Pose2d   STARTING_POSE                      = new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0));

    // TESTING
    public static final double   MOTOR_POWER_HARD_CAP               = 1.0; // Only use for testing, otherwise set to 1.0
    
    // REDUCE DRIVE VELOCITY WHEN FAR FROM ANGLE
    public static final boolean  DO_ANGLE_ERROR_SPEED_REDUCTION     = true;
    public static final double   DISCRETIZED_TIME_STEP              = 0.1; // Keeps movement in straight lines when rotating
    

    ///////////////////////// CALCUALTED /////////////////////////

    // PHYSICAL
    public static final double   TRACKWIDTH                         = CHASSIS_WIDTH - WHEEL_FRAME_DISTANCE * 2.0; // left-to-right distance between the drivetrain wheels
    public static final double   WHEELBASE                          = CHASSIS_LENGTH - WHEEL_FRAME_DISTANCE * 2.0; // front-to-back distance between the drivetrain wheels
    
    // GEAR AND WHEEL RATIOS
    public static final double   DRIVE_ENCODER_CONVERSION_FACTOR  = (WHEEL_RADIUS * 2.0 * Math.PI) / DRIVE_MOTOR_GEARING;
    public static final double   STEER_ENCODER_CONVERSION_FACTOR = (Math.PI * 2.0) / STEER_MOTOR_GEARING;
    
    public static class PHYSICS {
      public static final double ROTATIONAL_INERTIA = (1.0 / 12.0) * ROBOT_MASS * (Math.pow(CHASSIS_WIDTH, 2.0) + Math.pow(CHASSIS_LENGTH, 2.0));
      public static final double SLIPLESS_ACCELERATION = 9.80 * FRICTION_COEFFICIENT;
      public static final double SLIPLESS_CURRENT_LIMIT = (SLIPLESS_ACCELERATION * NEO.STALL_CURRENT * ROBOT_MASS * WHEEL_RADIUS) / (4 * DRIVE_MOTOR_GEARING * NEO.STALL_TORQUE);
      
      public static final double DRIVE_RADIUS = Math.hypot(WHEELBASE / 2.0, TRACKWIDTH / 2.0);
      
      public static final double MAX_WHEEL_VELOCITY = (NEO.FREE_SPEED * GEARBOX_EFFICIENCY * (Math.PI * 2.0)) / 60 / DRIVE_MOTOR_GEARING;
      public static final double MAX_LINEAR_VELOCITY = MAX_WHEEL_VELOCITY * WHEEL_RADIUS;
      public static final double MAX_LINEAR_FORCE = (4.0 * NEO.maxTorqueCurrentLimited((int) SLIPLESS_CURRENT_LIMIT) * DRIVE_MOTOR_GEARING) / WHEEL_RADIUS; // N
      public static final double MAX_LINEAR_ACCELERATION = MAX_LINEAR_FORCE / ROBOT_MASS;
      public static final double MAX_CHASSIS_TORQUE = MAX_LINEAR_FORCE * DRIVE_RADIUS;
      public static final double MAX_ANGULAR_ACCELERATION = SwerveDrive.toAngular(MAX_LINEAR_ACCELERATION); // MAX_CHASSIS_TORQUE / ROTATIONAL_INERTIA;
      public static final double MAX_ANGULAR_VELOCITY = (MAX_WHEEL_VELOCITY * WHEEL_RADIUS) / DRIVE_RADIUS;
    }

    public static final class AUTONOMOUS {
      public static final double MAX_LINEAR_VELOCITY = PHYSICS.MAX_LINEAR_VELOCITY;
      public static final double MAX_LINEAR_ACCELERATION = PHYSICS.MAX_LINEAR_ACCELERATION;
      public static final double MAX_ANGULAR_VELOCITY = PHYSICS.MAX_ANGULAR_VELOCITY;
      public static final double MAX_ANGULAR_ACCELERATION = PHYSICS.MAX_ANGULAR_ACCELERATION;

      public static final class TRANSLATION_GAINS {
        public static final double kP = 10.0;
        public static final double kI = 0.0;
        public static final double kD = 0.0;
      }
      public static final class ROTATION_GAINS {
        public static final double kP = 2.0;
        public static final double kI = 0.0;
        public static final double kD = 0.0;
      }
    }

    public static final class DRIVE_MOTOR_PROFILE {
      // FROM WPILIB SYSTEM IDENTIFICATION, FREE SPINNING
      public static final double kP                 = 0.00010; // Proportion Gain
      public static final double kI                 = 0.00000; // Integral Gain
      public static final double kD                 = 0.00000; // Derivative Gain
      public static final double kS                 = 0.00000; // volts
      public static final double kA                 = 0.27734; // volts per m/s^2, free spinning
      
      // CALCULATED
      public static final double kV                 = 12.0 / (PHYSICS.MAX_LINEAR_VELOCITY); // volts per m/s
      public static final int    CURRENT_LIMIT      = (int) (PHYSICS.SLIPLESS_CURRENT_LIMIT); // Amps
      public static final double RAMP_RATE          = (12.0 / kV) / PHYSICS.SLIPLESS_ACCELERATION; // Seconds it takes to reach full power
      
      // PREFERENCE
      public static final int[]  STATUS_FRAMES      = { 10, 10, 10, 500, 500, 500, 500 }; // ms
    }

    public static final class STEER_MOTOR_PROFILE {
      // FROM WPILIB SYSTEM IDENTIFICATION
      public static final double kP                 = 0.72776; // Proportion Gain
      public static final double kI                 = 0.00000; // Integral Gain
      public static final double kD                 = 0.06514; // Derivative Gain
      public static final double kS                 = 0.06684; // volts
      public static final double kA                 = 0.01968; // volts per rad/s^2

      // CALCULATED
      public static final double kV                 = 12.0 / (NEO.FREE_SPEED / 60.0 * (1.0 / STEER_MOTOR_GEARING) * Math.PI * 2.0);
      public static final int    CURRENT_LIMIT      = 30; // Amps
      public static final double RAMP_RATE          = 0.1; // Seconds it takes to reach full power
      
      // PREFERENCE
      public static final int[]  STATUS_FRAMES      = { 10, 10, 10, 500, 500, 500, 500 }; // ms
    }

    // TELEOPERATED
    public static final class ABSOLUTE_ROTATION_GAINS {
      public static final double kP  = 4.0;
      public static final double kI  = 0.0;
      public static final double kD  = 0.0;
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
    public static final double FREE_SPEED = 5880;
    public static final double STALL_TORQUE = 3.28;
    public static final double STALL_CURRENT = 181;
    public static final double SAFE_TEMPERATURE = 60;
    public static final int SAFE_STALL_CURRENT = 60;

    public static double maxTorqueCurrentLimited(int currentLimit) {
      return STALL_TORQUE / STALL_CURRENT * currentLimit;
    }
  }
}
