// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.


package frc.robot.commands.vision;

import frc.robot.subsystems.*;
import frc.robot.subsystems.drive.SwerveDrive;
import frc.robot.subsystems.vision.Camera;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.RobotContainer;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;


/** An example command that uses an example subsystem. */
public class AutoOrient extends Command {
  private final SwerveDrive sDrive;
  private final Camera camera;

  public AutoOrient(Camera camera, SwerveDrive sDrive) {
    
    this.sDrive = sDrive;
    this.camera = camera;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(camera, sDrive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    sDrive.setTargetHeading(Rotation2d.fromDegrees(90));


    //TESTING ON FIDUCIAL ID 6
    if (camera.getFiducialId() == 6){

      Pose2d currentPose = sDrive.getPose();

      Translation2d currentTrans2d = currentPose.getTranslation();

      Translation2d targetTrans2d = new Translation2d(1.85, 7.24);

      Rotation2d currentRot2d =  currentPose.getRotation();


      //double currentAngle = Math.acos(currentRot2d.getQuaternion().getX()) * 180/Math.PI;

      //System.out.println("Angle: " + currentAngle);


      double targetAngle = 90.0;


      double xDiff = currentTrans2d.getX() - targetTrans2d.getX();
      double yDiff = currentTrans2d.getY()-targetTrans2d.getY();

      
      double distance = Math.sqrt(Math.pow(xDiff,2) + Math.pow(yDiff,2));



      double initialDist = 4;
      double initialSpeed = 0.9;
      double finalDist = 0.8;
      double finalSpeed = 0.36;
      double maxSpeed = ((finalSpeed - initialSpeed)/(finalDist - initialDist)) *(distance - initialDist) + initialSpeed;

      maxSpeed = maxSpeed * 2; 


      sDrive.driveFieldRelative(xDiff/distance*-maxSpeed, yDiff/distance*-maxSpeed, 0);

      }
      
    
    }





  

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}