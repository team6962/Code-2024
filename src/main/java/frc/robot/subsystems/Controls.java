package frc.robot.subsystems;

import java.util.Map;
import java.util.function.BooleanSupplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.RobotContainer;
import frc.robot.Constants.Constants;
import frc.robot.Constants.Constants.DEVICES;
import frc.robot.Constants.Constants.LIMELIGHT;
import frc.robot.Constants.Preferences;
import frc.robot.commands.drive.XBoxSwerve;
import frc.robot.subsystems.amp.Amp;
import frc.robot.subsystems.amp.AmpPivot;
import frc.robot.subsystems.amp.AmpWheels;
import frc.robot.subsystems.drive.SwerveDrive;
import frc.robot.subsystems.hang.Hang;
import frc.robot.subsystems.shooter.FeedWheels;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterPivot;
import frc.robot.subsystems.shooter.ShooterWheels;
import frc.robot.subsystems.transfer.Transfer;
import frc.robot.subsystems.transfer.TransferInWheels;
import frc.robot.subsystems.transfer.TransferOutWheels;
import frc.robot.subsystems.vision.Notes;

public class Controls {
  public static final CommandXboxController operator = new CommandXboxController(DEVICES.OPERATOR_XBOX_CONTROLLER);
  public static final CommandXboxController driver = new CommandXboxController(DEVICES.DRIVE_XBOX_CONTROLLER);

  public static void configureBindings(
      RobotStateController stateController,
      SwerveDrive swerveDrive, 
      Transfer transfer, 
      TransferInWheels transferInWheels, 
      TransferOutWheels transferOutWheels, 
      Shooter shooter, 
      ShooterWheels shooterWheels, 
      ShooterPivot shooterPivot, 
      FeedWheels feedWheels,
      Amp amp, 
      AmpPivot ampPivot, 
      AmpWheels ampWheels,
      Hang hang
      )
    {

    driver.a();
    driver.b();
    driver.x();
    driver.y(); // USED
    driver.start().whileTrue(swerveDrive.goTo(frc.robot.Constants.Field.AUTO_MOVE_POSITIONS.get("AMP").get()));;
    driver.back().whileTrue(stateController.setState(RobotStateController.State.AIM_MORTAR));
    driver.leftBumper();
    driver.rightBumper();
    driver.leftStick().whileTrue(stateController.setState(RobotStateController.State.AIM_SPEAKER));
    driver.rightStick().whileTrue(swerveDrive.facePointCommand(() -> Notes.getNotePosition(LIMELIGHT.NOTE_CAMERA_NAME, LIMELIGHT.NOTE_CAMERA_PITCH, swerveDrive, swerveDrive.getFieldVelocity(), LIMELIGHT.NOTE_CAMERA_POSITION), new Rotation2d()));
    driver.povCenter(); // USED
    driver.povUp(); // USED
    driver.povDown(); // USED
    driver.povLeft(); // USED
    driver.povRight(); // USED
    driver.leftTrigger(); // USED
    driver.rightTrigger(); // USED
    swerveDrive.setDefaultCommand(new XBoxSwerve(swerveDrive, driver.getHID(), stateController));    

    if (RobotBase.isSimulation()) {
      // driver.button(1).whileTrue(stateController.setState(RobotStateController.State.AIM_SPEAKER).alongWith(stateController.setState(RobotStateController.State.SPIN_UP)));
    }

    driver.a().whileTrue(shooter.aim(() -> {
      Translation2d point = swerveDrive.getPose().getTranslation().plus(new Translation2d(
        4.0,
        0.0
      ).rotateBy(swerveDrive.getHeading().plus(Rotation2d.fromDegrees(180.0))));
      SwerveDrive.getField().getObject("point").setPose(new Pose2d(point, new Rotation2d()));
      return new Translation3d(
        point.getX(),
        point.getY(),
        0.0
      );
    }, 1.0).alongWith(stateController.setState(RobotStateController.State.SPIN_UP)));


    operator.a().onTrue(shooterPivot.setTargetAngleCommand(() -> Rotation2d.fromDegrees(30.0)));
    operator.b();
    operator.x();
    operator.y().onTrue(shooterPivot.setTargetAngleCommand(() -> Preferences.SHOOTER_PIVOT.MAX_ANGLE));
    operator.start().whileTrue(stateController.setState(RobotStateController.State.LEAVE_AMP));
    operator.back().onTrue(stateController.setState(RobotStateController.State.CENTER_NOTE).andThen(Controls.rumbleBoth()));
    operator.leftBumper();
    operator.rightBumper().whileTrue(stateController.setState(RobotStateController.State.INTAKE));
    operator.leftStick().whileTrue(stateController.setState(RobotStateController.State.PREPARE_AMP));
    operator.rightStick().whileTrue(stateController.setState(RobotStateController.State.PLACE_AMP));
    operator.povCenter();
    operator.povUp().whileTrue(hang.setState(Hang.State.EXTEND));
    operator.povDown().whileTrue(hang.setState(Hang.State.RETRACT));
    operator.povLeft().whileTrue(stateController.setState(RobotStateController.State.INTAKE_OUT));
    operator.povRight();
    operator.leftTrigger().toggleOnTrue(stateController.setState(RobotStateController.State.SPIN_UP));
    operator.rightTrigger().whileTrue(stateController.setState(RobotStateController.State.SHOOT));

    ShuffleboardTab driverTab = Shuffleboard.getTab("Driver Dashboard");

    driverTab.addBoolean("Is Aimed", () -> shooter.isAimed())
      .withWidget(BuiltInWidgets.kBooleanBox)
      .withPosition(3, 0)
      .withSize(2, 2)
      .withProperties(Map.of("min", 0, "max", 100));
    
    driverTab.addBoolean("Has Note", stateController::hasNote)
      .withWidget(BuiltInWidgets.kBooleanBox)
      .withPosition(0, 2)
      .withSize(3, 1);

    driverTab.addDouble("Battery Capacity", () -> ((RobotContainer.getVoltage() - Constants.SWERVE_DRIVE.BATTERY_VOLTAGE) / (Constants.SWERVE_DRIVE.BATTERY_RESISTANCE * RobotContainer.getTotalCurrent())) * 100.0)
      .withWidget(BuiltInWidgets.kDial)
      .withPosition(3, 2)
      .withSize(2, 2)
      .withProperties(Map.of("min", 0, "max", 100));

    driverTab.addBoolean("Flywheel Status", () -> shooterWheels.getState() == ShooterWheels.State.SPIN_UP)
      .withWidget(BuiltInWidgets.kBooleanBox)
      .withPosition(0, 3)
      .withSize(3, 1);

    driverTab.addString("Current State", () -> stateController.getState().name())
      .withWidget(BuiltInWidgets.kTextView)
      .withPosition(1, 4)
      .withSize(3, 1);
  }

  private static Command rumble(CommandXboxController controller) {
    return Commands.runEnd(() -> {
      controller.getHID().setRumble(RumbleType.kBothRumble, 1.0);
      LEDs.setState(LEDs.State.GOOD);
    },
    () -> {
      controller.getHID().setRumble(RumbleType.kBothRumble, 0.0);
    }
    ).withTimeout(0.25);
  }

  private static Command rumble(CommandXboxController controller, BooleanSupplier booleanSupplier) {
    return Commands.runEnd(() -> {
      if (booleanSupplier.getAsBoolean()) {
        controller.getHID().setRumble(RumbleType.kBothRumble, 1.0);
        LEDs.setState(LEDs.State.GOOD);
      } else {
        controller.getHID().setRumble(RumbleType.kBothRumble, 0.0);
      }
    },
    () -> {
      controller.getHID().setRumble(RumbleType.kBothRumble, 0.0);
    }
    );
  }

  public static Command rumbleDriver() {
    return rumble(driver);
  }

  public static Command rumbleDriver(BooleanSupplier booleanSupplier) {
    return rumble(driver, booleanSupplier);
  }

  public static Command rumbleOperator() {
    return rumble(operator);
  }

  public static Command rumbleOperator(BooleanSupplier booleanSupplier) {
    return rumble(operator, booleanSupplier);
  }

  public static Command rumbleBoth() {
    return rumbleOperator().alongWith(rumbleDriver());
  }

  public static Command rumbleBoth(BooleanSupplier booleanSupplier) {
    return rumbleOperator(booleanSupplier).alongWith(rumbleDriver(booleanSupplier));
  }
}
