// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import java.util.List;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CAN;
import frc.robot.Constants.NEO;
import frc.robot.Constants.SWERVE_DRIVE;
import frc.robot.Constants.SWERVE_DRIVE.DRIVE_MOTOR_PROFILE;
import frc.robot.Constants.SWERVE_DRIVE.STEER_MOTOR_PROFILE;
import frc.robot.Constants.SWERVE_MATH;
import frc.robot.util.ConfigUtils;
import frc.robot.util.Logging.Logger;

public class SwerveModule extends SubsystemBase {
  private CANSparkMax driveMotor, steerMotor;
  private RelativeEncoder driveEncoder, steerEncoder;
  private CANCoder absoluteSteerEncoder;
  private SparkMaxPIDController drivePID, steerPID;
  private SwerveModuleState targetState = new SwerveModuleState();
  private SwerveModuleState drivenState = new SwerveModuleState();
  private double wheelAcceleration = 0.0;
  public final int id;
  
  private SimpleMotorFeedforward driveFF = new SimpleMotorFeedforward(
    DRIVE_MOTOR_PROFILE.kS,
    DRIVE_MOTOR_PROFILE.kV,
    DRIVE_MOTOR_PROFILE.kA
  );

  public SwerveModule(int id) {
    this.id = id;

    if (RobotBase.isSimulation()) return;

    driveMotor           = new CANSparkMax(CAN.SWERVE_DRIVE_SPARK_MAX[id], MotorType.kBrushless);
    steerMotor           = new CANSparkMax(CAN.SWERVE_STEER_SPARK_MAX[id], MotorType.kBrushless);
    absoluteSteerEncoder = new CANCoder(CAN.SWERVE_STEER_CANCODERS[id]);
    steerEncoder         = steerMotor.getEncoder();
    driveEncoder         = driveMotor.getEncoder();
    drivePID             = driveMotor.getPIDController();
    steerPID             = steerMotor.getPIDController();

    ConfigUtils.configure(List.of(
      () -> driveMotor.restoreFactoryDefaults(),
      () -> driveMotor.setIdleMode(IdleMode.kBrake),
      () -> driveMotor.enableVoltageCompensation(12.0),
      () -> driveMotor.setSmartCurrentLimit(Math.min(DRIVE_MOTOR_PROFILE.CURRENT_LIMIT, NEO.SAFE_STALL_CURRENT), DRIVE_MOTOR_PROFILE.CURRENT_LIMIT),
      () -> driveMotor.setClosedLoopRampRate(DRIVE_MOTOR_PROFILE.RAMP_RATE),
      () -> driveEncoder.setPositionConversionFactor(SWERVE_DRIVE.DRIVE_MOTOR_METERS_PER_REVOLUTION),
      () -> driveEncoder.setVelocityConversionFactor(SWERVE_DRIVE.DRIVE_MOTOR_METERS_PER_REVOLUTION / 60.0),
      () -> drivePID.setP(DRIVE_MOTOR_PROFILE.kP, 0),
      () -> drivePID.setI(DRIVE_MOTOR_PROFILE.kI, 0),
      () -> drivePID.setD(DRIVE_MOTOR_PROFILE.kD, 0),
      () -> drivePID.setOutputRange(-SWERVE_DRIVE.MOTOR_POWER_HARD_CAP, SWERVE_DRIVE.MOTOR_POWER_HARD_CAP, 0),
      () -> driveMotor.burnFlash(),
      
      () -> steerMotor.restoreFactoryDefaults(),
      () -> { steerMotor.setInverted(true); return true; },
      () -> steerMotor.setIdleMode(IdleMode.kBrake),
      () -> steerMotor.enableVoltageCompensation(12.0),
      () -> steerMotor.setSmartCurrentLimit(Math.min(STEER_MOTOR_PROFILE.CURRENT_LIMIT, NEO.SAFE_STALL_CURRENT), STEER_MOTOR_PROFILE.CURRENT_LIMIT),
      () -> steerMotor.setClosedLoopRampRate(STEER_MOTOR_PROFILE.RAMP_RATE),
      () -> steerEncoder.setPositionConversionFactor(SWERVE_DRIVE.STEER_MOTOR_RADIANS_PER_REVOLUTION),
      () -> steerEncoder.setVelocityConversionFactor(SWERVE_DRIVE.STEER_MOTOR_RADIANS_PER_REVOLUTION / 60.0),
      () -> steerPID.setP(STEER_MOTOR_PROFILE.kP, 0),
      () -> steerPID.setI(STEER_MOTOR_PROFILE.kI, 0),
      () -> steerPID.setD(STEER_MOTOR_PROFILE.kD, 0),
      () -> steerPID.setPositionPIDWrappingEnabled(true),
      () -> steerPID.setPositionPIDWrappingMinInput(-Math.PI),
      () -> steerPID.setPositionPIDWrappingMaxInput(Math.PI),
      () -> steerPID.setOutputRange(-SWERVE_DRIVE.MOTOR_POWER_HARD_CAP, SWERVE_DRIVE.MOTOR_POWER_HARD_CAP, 0),
      () -> steerMotor.burnFlash(),

      () -> absoluteSteerEncoder.configMagnetOffset(SWERVE_DRIVE.STEER_ENCODER_OFFSETS[id]),
      () -> absoluteSteerEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Signed_PlusMinus180)
    ));
    // driveMotor.setClosedLoopRampRate(Math.max((NEO.FREE_SPEED / 60.0) / ((9.80 * SWERVE_DRIVE.COEFFICIENT_OF_FRICTION) / SWERVE_DRIVE.DRIVE_MOTOR_METERS_PER_REVOLUTION), DRIVE_MOTOR_CONFIG.RAMP_RATE))

    seedSteerEncoder();

    String logPath = "module_" + SWERVE_DRIVE.MODULE_NAMES[id] + "/";
    Logger.autoLog(logPath + "current",                 () -> getTotalCurrent());
    Logger.autoLog(logPath + "getAbsoluteSteerDegrees", () -> getTrueSteerDirection().getDegrees());
    Logger.autoLog(logPath + "measuredState",           () -> getMeasuredState());
    Logger.autoLog(logPath + "measuredAngle",           () -> getMeasuredState().angle.getDegrees());
    Logger.autoLog(logPath + "measuredVelocity",        () -> getMeasuredState().speedMetersPerSecond);
    Logger.autoLog(logPath + "drivenState",             () -> getDrivenState());
    Logger.autoLog(logPath + "drivenAngle",             () -> getDrivenState().angle.getDegrees());
    Logger.autoLog(logPath + "drivenVelocity",          () -> getDrivenState().speedMetersPerSecond);
    Logger.autoLog(logPath + "targetState",             () -> getTargetState());
    Logger.autoLog(logPath + "targetAngle",             () -> getTargetState().angle.getDegrees());
    Logger.autoLog(logPath + "targetVelocity",          () -> getTargetState().speedMetersPerSecond);
  }

  public void periodic() {
    if (Math.abs(getMeasuredState().speedMetersPerSecond) < SWERVE_DRIVE.VELOCITY_DEADBAND && SWERVE_MATH.angleDistance(getMeasuredState().angle.getRadians(), getTargetState().angle.getRadians()) < Units.degreesToRadians(1.0)) {
      seedSteerEncoder();
    }

    drive(calculateDrivenState());
  }
  
  public void drive(SwerveModuleState state) {
    double speedMetersPerSecond = state.speedMetersPerSecond;
    double radians = state.angle.getRadians();
    
    if (SWERVE_DRIVE.DO_ANGLE_ERROR_SPEED_REDUCTION) {
      speedMetersPerSecond *= Math.cos(SWERVE_MATH.angleDistance(radians, getMeasuredState().angle.getRadians()));
    }
    
    drivePID.setReference(
      speedMetersPerSecond,
      ControlType.kVelocity,
      0,
      driveFF.calculate(speedMetersPerSecond, getWheelAcceleration()),
      ArbFFUnits.kVoltage
    );
      
    steerPID.setReference(
      radians,
      ControlType.kPosition,
      0
    );
  }
    
  public void setTargetState(SwerveModuleState state) {
    targetState = SwerveModuleState.optimize(state, getMeasuredState().angle);
  }

  public SwerveModuleState calculateDrivenState() {
    SwerveModuleState oldState = new SwerveModuleState(getDrivenState().speedMetersPerSecond, getDrivenState().angle);
    Translation2d currentVelocity = new Translation2d(oldState.speedMetersPerSecond, oldState.angle);
    Translation2d targetVelocity = new Translation2d(targetState.speedMetersPerSecond, targetState.angle);
    Translation2d acceleration = (targetVelocity).minus(currentVelocity).div(0.02);
    if (acceleration.getNorm() > SWERVE_DRIVE.ACCELERATION) {
      acceleration = new Translation2d(SWERVE_DRIVE.ACCELERATION, acceleration.getAngle());
    }
    Translation2d newVelocity = currentVelocity.plus(acceleration.times(0.02));
    drivenState = new SwerveModuleState(newVelocity.getNorm(), newVelocity.getAngle());
    drivenState = SwerveModuleState.optimize(drivenState, getMeasuredState().angle);
    wheelAcceleration = (drivenState.speedMetersPerSecond - oldState.speedMetersPerSecond) / 0.02;
    
    return drivenState;
  }
    
  public void stop() {
    targetState = new SwerveModuleState(0.0, getMeasuredState().angle);
    steerMotor.stopMotor();
    driveMotor.stopMotor();
  }
  
  /**
   * Seeds the position of the built-in relative encoder with the absolute position of the steer CANCoder.
   * This is because the CANCoder polls at a lower rate than we'd like, so we essentially turn the relative encoder into an fast-updating absolute encoder.
   * Also the built-in SparkMaxPIDControllers require a compatible encoder to run the faster 1kHz closed loop 
   */
  public void seedSteerEncoder() {
    steerEncoder.setPosition(getTrueSteerDirection().getRadians());
  }
  
  private Rotation2d getTrueSteerDirection() {
    return Rotation2d.fromDegrees(absoluteSteerEncoder.getAbsolutePosition());
  }

  public SwerveModuleState getTargetState() {
    return targetState;
  }
  
  public SwerveModuleState getMeasuredState() {
    return new SwerveModuleState(driveEncoder.getVelocity(), Rotation2d.fromRadians(MathUtil.angleModulus(steerEncoder.getPosition())));
  }

  public SwerveModuleState getDrivenState() {
    return drivenState;
  }

  public SwerveModulePosition getModulePosition() {
    return new SwerveModulePosition(driveEncoder.getPosition(), getMeasuredState().angle);
  }

  public static double calcWheelVelocity(double power) {
    return (power * 12.0) / DRIVE_MOTOR_PROFILE.kV;
  }

  public static double calcCurrent(double acceleration) {
    return (acceleration * SWERVE_DRIVE.ROBOT_MASS * SWERVE_DRIVE.FRICTION_COEFFICIENT * SWERVE_DRIVE.WHEEL_DIAMETER * SWERVE_DRIVE.DRIVE_MOTOR_GEAR_RATIO * (0.5 * NEO.STALL_CURRENT - 0.5 * NEO.FREE_CURRENT)) / (NEO.STALL_TORQUE * SWERVE_DRIVE.MODULE_COUNT * SWERVE_DRIVE.GEARBOX_EFFICIENCY) + NEO.FREE_CURRENT;
  }

  // public static double calcAcceleration(double current) {
  //   return (2.0 * NEO.STALL_TORQUE * SWERVE_DRIVE.MODULE_COUNT * SWERVE_DRIVE.GEARBOX_EFFICIENCY * current - NEO.STALL_TORQUE * SWERVE_DRIVE.MODULE_COUNT * NEO.FREE_CURRENT * SWERVE_DRIVE.GEARBOX_EFFICIENCY) / (SWERVE_DRIVE.ROBOT_MASS * SWERVE_DRIVE.FRICTION_COEFFICIENT * SWERVE_DRIVE.WHEEL_DIAMETER * SWERVE_DRIVE.DRIVE_MOTOR_GEAR_RATIO * NEO.STALL_CURRENT - SWERVE_DRIVE.ROBOT_MASS * SWERVE_DRIVE.FRICTION_COEFFICIENT * SWERVE_DRIVE.WHEEL_DIAMETER * SWERVE_DRIVE.DRIVE_MOTOR_GEAR_RATIO * NEO.FREE_CURRENT);
  // }

  public double getTotalCurrent() {
    return driveMotor.getOutputCurrent() + steerMotor.getOutputCurrent();
  }

  public double getWheelAcceleration() {
    return wheelAcceleration;
  }

  public void setVolts(double volts) {
    steerPID.setReference(0.0, ControlType.kPosition);
    driveMotor.setVoltage(volts);
  }
  
  public Pose2d getPose(Pose2d robotPose) {
    Pose2d relativePose = new Pose2d();
    if (id == 0) relativePose = new Pose2d(
      SWERVE_DRIVE.WHEELBASE / 2.0,
      SWERVE_DRIVE.TRACKWIDTH / 2.0,
      getMeasuredState().angle
    );
    if (id == 1) relativePose = new Pose2d(
      SWERVE_DRIVE.WHEELBASE / 2.0,
      -SWERVE_DRIVE.TRACKWIDTH / 2.0,
      getMeasuredState().angle
    );
    if (id == 2) relativePose = new Pose2d(
      -SWERVE_DRIVE.WHEELBASE / 2.0,
      SWERVE_DRIVE.TRACKWIDTH / 2.0,
      getMeasuredState().angle
    );
    if (id == 3) relativePose = new Pose2d(
      -SWERVE_DRIVE.WHEELBASE / 2.0,
      -SWERVE_DRIVE.TRACKWIDTH / 2.0,
      getMeasuredState().angle
    );
    return relativePose.relativeTo(new Pose2d(
      new Translation2d(),
      robotPose.getRotation().times(-1.0)
    )).relativeTo( new Pose2d(
      -robotPose.getX(),
      -robotPose.getY(),
      new Rotation2d()
    ));
  }
}