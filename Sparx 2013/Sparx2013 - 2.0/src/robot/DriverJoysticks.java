package robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Ryan
 */
public class DriverJoysticks {
    private static DriverJoysticks driverjoystick;
    private static Joystick leftDriverJoy;
    private static Joystick rightDriverJoy;
    private final static double ERROR = .1;
    
    private DriverJoysticks(){
        leftDriverJoy = new Joystick(IO.driverJoystickLeftPort);
        rightDriverJoy = new Joystick(IO.driverJoystickRightPort);
        
    }
    
    public static DriverJoysticks getInstance(){
        if(driverjoystick == null){
            driverjoystick = new DriverJoysticks();
        }
        return driverjoystick;
    }
    
    public boolean getRightTrigger(){
        return rightDriverJoy.getRawButton(IO.ATTACK3_TRIGGER);
    }
    
    public double getLeftJoyZ(){
      if(Math.abs(leftDriverJoy.getRawAxis(IO.ATTACK3_Z_AXIS)) < ERROR){
          return 0;
      }
        return leftDriverJoy.getRawAxis(IO.ATTACK3_Z_AXIS);
    }
        
    public double getRightJoyZ(){
      if(Math.abs(rightDriverJoy.getRawAxis(IO.ATTACK3_Z_AXIS)) < ERROR){
          return 0;
      }
        return rightDriverJoy.getRawAxis(IO.ATTACK3_Z_AXIS);
    }
           
    public double getLeftJoyY(){
      if(Math.abs(leftDriverJoy.getRawAxis(IO.ATTACK3_Y_AXIS)) < ERROR){
          return 0;
      }
        return leftDriverJoy.getRawAxis(IO.ATTACK3_Y_AXIS);
    }
        
    public double getRightJoyY(){
      if(Math.abs(rightDriverJoy.getRawAxis(IO.ATTACK3_Y_AXIS)) < ERROR){
          return 0;
        }
        
        return rightDriverJoy.getRawAxis(IO.ATTACK3_Y_AXIS);
    }
    
    public boolean getClimbing(){
        return rightDriverJoy.getRawButton(IO.ATTACK3_TRIGGER);
    }
    
    public boolean getStayInLowGear(){
        return leftDriverJoy.getRawButton(IO.stayInLowGear);
    }
    
    public boolean getSwitchGears(){
        return leftDriverJoy.getRawButton(IO.switchGears);
    }
    
    public boolean getDriveStraight(){
        return rightDriverJoy.getRawButton(IO.driveStraight);
    }

}