package robot;

import edu.wpi.first.wpilibj.Joystick;

public class OperatorJoystick2 extends SubSystem{
    
    private static Joystick operatorJoystick;
    private static OperatorJoystick2 operatorjoystick2;

    private IO master;
    
    public OperatorJoystick2(){
        super("Operator");
        
        master = master.getInstance();    
        operatorJoystick = new Joystick(master.OperatorJoystickPort);//port 3
    }
    
    public static OperatorJoystick2 getInstance(){
        if(operatorjoystick2 == null){
            operatorjoystick2 = new OperatorJoystick2();
        }
        return operatorjoystick2;
    }
    
    public boolean getRampButton(){
        return(operatorJoystick.getRawButton(IO.acquireButton));
    }
    
    public boolean highFrontGoalPreset(){
        return(operatorJoystick.getRawAxis(IO.RIGHT_Y_AXIS) < -0.6 || operatorJoystick.getRawButton(IO.frontTopPreset));//NEED TO CHANGE TO OPERATOR IN IO
    }
    
    public boolean highBackGoalPreset(){
        return(operatorJoystick.getRawAxis(IO.RIGHT_Y_AXIS) > 0.6);//NEED TO CHANGE TO OPERATOR IN IO
    }
    
    public boolean middleFrontGoalPreset(){
        return(operatorJoystick.getRawAxis(IO.LEFT_Y_AXIS) < -0.6);//NEED TO CHANGE TO OPERATOR IN IO
    }
    
    public boolean middleBackGoalPreset(){
        return(operatorJoystick.getRawAxis(IO.LEFT_Y_AXIS) > 0.6 || operatorJoystick.getRawButton(IO.backMiddlePreset));//NEED TO CHANGE TO OPERATOR IN IO
    }
    
    public boolean lowGoalPreset(){
        return(false);//NEED TO CHANGE TO OPERATOR IN IO
    }
    
    public boolean fireDisk(){
        return(operatorJoystick.getRawButton(IO.fireButton));
    }
    
    public boolean getShootingButton(){
        return(operatorJoystick.getRawButton(IO.shootButton));
    }
    
    public boolean getInbounderButton(){
        return(operatorJoystick.getRawButton(IO.inbounderButton));
    }
    
    public boolean getOffButton(){
        return(operatorJoystick.getRawButton(IO.offButton));
    }
    
    public boolean getRollerReverse(){
        return(operatorJoystick.getRawButton(IO.rollerReverse));
    }
    
    public boolean getVoltageMode(){
        return(operatorJoystick.getRawButton(IO.RPMShootingModeButton));
    }
    
    public boolean increaseSpeedTrim(){
        return (operatorJoystick.getRawAxis(IO.DPAD_Y_AXIS) < -0.6);
    }
    
    public boolean decreaseSpeedTrim(){
        return (operatorJoystick.getRawAxis(IO.DPAD_Y_AXIS) > 0.6);
    }
    
    public boolean getInDiagnosticsMode(){
        return (operatorJoystick.getRawButton(IO.inDiagnosticsButton));
    }
    
    public boolean getAutoStart(){
        return (operatorJoystick.getRawButton(IO.autoStart));
    }
    
    public boolean getPunchyManual(){
        return operatorJoystick.getRawButton(IO.punchyAuto);
    }
    
}
