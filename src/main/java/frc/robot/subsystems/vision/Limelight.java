// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.vision;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.LimelightHelpers;

public class Limelight extends SubsystemBase {
  private LimelightHelpers.LimelightResults limelightData;
  private String name;
  private ShuffleboardTab dashboard = Shuffleboard.getTab("Dashboard");

  public Limelight(String name) {
    this.name = name;
    dashboard.addCamera(name, name, "http://" + name + ".local:5800");
  }

  @Override
  public void periodic() {
    limelightData = LimelightHelpers.getLatestResults(name);
  }

  public LimelightHelpers.Results getTargetingResults() {
    return limelightData.targetingResults;
  }

  public String getName() {
    return name;
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}