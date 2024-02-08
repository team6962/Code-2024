// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.CAN;
import frc.robot.Constants.DEVICES;
import frc.robot.commands.drive.XBoxSwerve;
import frc.robot.subsystems.Intake;
import frc.robot.commands.intake.XBoxIntake;
import frc.robot.subsystems.drive.SwerveDrive;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.transfer.Transfer;
import frc.robot.util.Logging.Logger;


/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

  // The robot's subsystems and commands
  private final XboxController xboxController = new XboxController(DEVICES.USB_XBOX_CONTROLLER);
  private final SwerveDrive swerveDrive = new SwerveDrive();
  private final Shooter shooter = new Shooter(swerveDrive);
  private final Transfer elevator = new Transfer();
  private final Intake intake = new Intake(elevator);

  private final SendableChooser<Command> calibrationChooser = new SendableChooser<>();
  private final Intake intake = new Intake();
  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog(), true);
    Logger.log("constants", this, Constants.class);
    Logger.autoLog("PDH", new PowerDistribution(CAN.PDH, ModuleType.kRev));
    Logger.startLog();

    swerveDrive.setDefaultCommand(new XBoxSwerve(swerveDrive, () -> xboxController));
    intake.setDefaultCommand(new XBoxIntake(intake, xboxController));
    
    calibrationChooser.setDefaultOption("Calibrate Drive Motor (FL)", swerveDrive.modules[0].calibrateDriveMotor());
    calibrationChooser.setDefaultOption("Calibrate Steer Motor (FL)", swerveDrive.modules[0].calibrateSteerMotor());
    SmartDashboard.putData("Swerve Module Calibration", calibrationChooser);
    
    // Configure the trigger bindings
    configureBindings();

    SwerveDrive.printChoreoConfig();
  }

  private void configureBindings() {
    new JoystickButton(xboxController, XboxController.Button.kX.value).whileTrue(Commands.startEnd(() -> intake.setState(Intake.IntakeState.FORWARD), () -> intake.setState(Intake.IntakeState.OFF)));
  }

  public Command getAutonomousCommand() {
    // return swerveDrive.goTo(new Translation2d(5.0, 5.0), Rotation2d.fromDegrees(90.0));
    return swerveDrive.followChoreoTrajectory("simple", true);
  }

  public void disabledPeriodic() {
  }

  public void testInit() {
    calibrationChooser.getSelected().schedule();
  }

}
