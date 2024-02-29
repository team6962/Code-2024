// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.amp;

import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants.Constants.AMP_PIVOT;
import frc.robot.Constants.Constants.CAN;
import frc.robot.Constants.Constants.DIO;
import frc.robot.Constants.Constants.ENABLED_SYSTEMS;
import frc.robot.Robot;
import frc.robot.Constants.Preferences;
import frc.robot.util.hardware.SparkMaxUtil;
import frc.robot.util.hardware.MotionControl.PivotController;

public class AmpPivot extends SubsystemBase {
  private CANSparkMax motor;
  private PivotController controller;
  private boolean isCalibrating = false;

  public AmpPivot() {
    motor = new CANSparkMax(CAN.AMP_PIVOT, MotorType.kBrushless);
    SparkMaxUtil.configureAndLog(this, motor, false, CANSparkMax.IdleMode.kBrake);
    SparkMaxUtil.configureCANStatusFrames(motor, false, false);

    controller = new PivotController(
      this,
      motor,
      DIO.AMP_PIVOT,
      AMP_PIVOT.ABSOLUTE_POSITION_OFFSET,
      AMP_PIVOT.PROFILE.kP,
      AMP_PIVOT.GEARING,
      AMP_PIVOT.PROFILE.MAX_ACCELERATION,
      Preferences.AMP_PIVOT.MIN_ANGLE,
      Preferences.AMP_PIVOT.MAX_ANGLE,
      false
    );

    SparkMaxUtil.save(motor);
  }

  @Override
  public void periodic() {
    if (!ENABLED_SYSTEMS.ENABLE_AMP) return;
    if (isCalibrating) return;
    if (RobotState.isDisabled()) {
      controller.setTargetAngle(controller.getPosition());
    }
    controller.run();
  }

  public Rotation2d getPosition() {
    if (Robot.isSimulation() && controller.getTargetAngle() != null) return controller.getTargetAngle();
    return controller.getPosition();
  }

  public Command setTargetAngleCommand(Rotation2d angle) {
    return runOnce(() -> {
      System.out.println("PIVOT ANGLE SET");
      setTargetAngle(angle);
    });
  }

  public void setTargetAngle(Rotation2d angle) {
    controller.setTargetAngle(angle);
  }

  public boolean doneMoving() {
    return controller.doneMoving();
  }

  public void setMaxAngle(Rotation2d angle) {
    controller.setMaxAngle(angle);
  }

  // public Command calibrate() {
  //   SysIdRoutine calibrationRoutine = new SysIdRoutine(
  //     new SysIdRoutine.Config(),
  //     new SysIdRoutine.Mechanism(
  //       (Measure<Voltage> volts) -> {
  //         motor.setVoltage(volts.in(Volts));
  //       },
  //       log -> {
  //         log.motor("amp-pivot")
  //           .voltage(Volts.of(motor.getAppliedOutput() * motor.getBusVoltage()))
  //           .angularPosition(Radians.of(controller.getPosition().getRadians()))
  //           .angularVelocity(RadiansPerSecond.of(controller.getVelocity().getRadians()));
  //       },
  //       this
  //     )
  //   );

  //   return Commands.sequence(
  //     Commands.runOnce(() -> isCalibrating = true),
  //     calibrationRoutine.quasistatic(SysIdRoutine.Direction.kForward).until(controller::isPastLimit),
  //     Commands.runOnce(() -> motor.stopMotor()),
  //     Commands.waitSeconds(1.0),
  //     calibrationRoutine.quasistatic(SysIdRoutine.Direction.kReverse).until(controller::isPastLimit),
  //     Commands.runOnce(() -> motor.stopMotor()),
  //     Commands.waitSeconds(1.0),
  //     calibrationRoutine.dynamic(SysIdRoutine.Direction.kForward).until(controller::isPastLimit),
  //     Commands.runOnce(() -> motor.stopMotor()),
  //     Commands.waitSeconds(1.0),
  //     calibrationRoutine.dynamic(SysIdRoutine.Direction.kReverse).until(controller::isPastLimit),
  //     Commands.runOnce(() -> motor.stopMotor()),
  //     Commands.waitSeconds(1.0),
  //     Commands.runOnce(() -> isCalibrating = false)
  //   );
  // }
}
