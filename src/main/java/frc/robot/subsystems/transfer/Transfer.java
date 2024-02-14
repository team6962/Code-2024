package frc.robot.subsystems.transfer;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import java.util.List;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.commands.*;
import frc.robot.subsystems.notes.NoteDetector;
import frc.robot.util.ConfigUtils;
import frc.robot.util.StatusChecks;
import frc.robot.util.Logging.Logger;
import frc.robot.Constants;
import frc.robot.Constants.AMP.PIVOT;
import frc.robot.Constants.CAN;
import frc.robot.Constants.ENABLED_SYSTEMS;
import frc.robot.Constants.NEO;
import frc.robot.Constants.TRANSFER;
import frc.robot.Presets;



public class Transfer extends SubsystemBase {
  private TransferInWheels transferIn;
  private TransferOutWheels transferOut;
 
  public static enum State {
    IN,
    AMP,
    SHOOTER,
    OFF,
  }

  public Transfer() {
    if (!ENABLED_SYSTEMS.ENABLE_TRANSFER) return;
    
    transferIn = new TransferInWheels();
    transferOut = new TransferOutWheels();
  }

  public Command setState(State state) {
    switch(state) {
      case OFF:
        return Commands.sequence( 
          transferIn.setState(TransferInWheels.State.OFF),
          transferOut.setState(TransferOutWheels.State.OFF)
        );
      case IN:
        return Commands.sequence( 
          transferIn.setState(TransferInWheels.State.IN),
          transferOut.setState(TransferOutWheels.State.OFF)
        );
      case AMP:
        return Commands.sequence( 
          transferIn.setState(TransferInWheels.State.IN),
          transferOut.setState(TransferOutWheels.State.AMP)
        );
      case SHOOTER:
        return Commands.sequence( 
          transferIn.setState(TransferInWheels.State.IN),
          transferOut.setState(TransferOutWheels.State.SHOOTER)
        );
    }
    return null;
  }
  
  @Override
  public void periodic() {
    if (!ENABLED_SYSTEMS.ENABLE_TRANSFER) return;
  }

  public boolean hasNote() {
    return transferIn.hasNote();
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
