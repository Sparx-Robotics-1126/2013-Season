package robot;

import edu.wpi.first.wpilibj.Dashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.DriverStationLCD.Line;

/**
 *
 * @author Ryan
 */
public class SubSystem extends Thread {
    public static final boolean AUTO = true, TELEOP = false;
    protected boolean current_Mode = AUTO;
    private boolean printing = false;
    DriverStationLCD lcd;
    private Line subSystemLine;
    private String prefix;
    
    public SubSystem(String name){
        if (name.equalsIgnoreCase("drives")){
            subSystemLine = DriverStationLCD.Line.kUser1;
            prefix = "Dr: ";
        }
        else if (name.equalsIgnoreCase("ascender")){
            subSystemLine = DriverStationLCD.Line.kUser2;
            prefix = "Ac: ";
        }
        else if (name.equalsIgnoreCase("disccontrol")){
            subSystemLine = DriverStationLCD.Line.kUser3;
            prefix = "DC: ";
        }
        else if (name.equalsIgnoreCase("autonomous")){
            subSystemLine = DriverStationLCD.Line.kUser4;
            prefix = "Au: ";
        }
        else if (name.equalsIgnoreCase("IO")){
            subSystemLine = DriverStationLCD.Line.kUser4;
            prefix = "IO: ";
        }
        else if (name.equalsIgnoreCase("MikesBoard")){
            subSystemLine = DriverStationLCD.Line.kUser4;
            prefix = "MikesB: ";
        }
        else if (name.equalsIgnoreCase("Diagnostics")){
            subSystemLine = DriverStationLCD.Line.kUser4;
            prefix = "Dia: ";
        }
        else {
            subSystemLine = DriverStationLCD.Line.kUser5;
            prefix = "";
        }
    }
    
    /**
     * Returns whether or not the robot module is being run autonomously.
     * @return true if the module is being run by the joysticks.
     */
    public boolean getLocalMode(){
        return current_Mode;
    }
    
    /**
     * Sets the mode of robot module to local or remote
     * @param mode - true if the module is running by the joystick
     */
    public void setLocalMode(boolean mode){
        current_Mode = mode;
    }
    /**
     * Prints the specified String to the User Messages section of the Driver Station
     * Line 1 is drives, Line 2 is Ascenders, Line 3 is DiscControl, Line 4 is for Auto,
     * and Line 6 is unspecifed subsystems
     * @param str - the string to print
     */
    public void printToLCD(String str) {
        lcd = DriverStationLCD.getInstance();
        lcd.println(subSystemLine, 1, prefix + str + "         ");
        lcd.updateLCD();
    }
    
    public void printToLCD(String str, boolean test) {
        lcd = DriverStationLCD.getInstance();
        lcd.println(DriverStationLCD.Line.kUser6, 1, prefix + str + "          ");
        lcd.updateLCD();
    }
    /**
     * Prints the specified String in the SmartDashboard
     * @param str - the string to print 
     */
//    public void printToSmartDashBoard(String str){
//        SmartDashboard.putString(prefix, str);
//    }
    
    public void print(String text){
        if(printing){
            System.out.println(prefix+": "+text);
        }
        sleep(10);
    }
    
    
    /**
     * Main sleep method
     * @param sleepTime - in milliseconds 
     */
    public void sleep(int sleepTime){
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isEnabled(){
        return(DriverStation.getInstance().isEnabled()); 
    }
    
    public void setPrinting(boolean setting){
        printing = setting;
    }
}
