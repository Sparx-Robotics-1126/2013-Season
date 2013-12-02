package robot;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class SparX extends SimpleRobot {
    private Drives drives;
    private Ascender ascender;
    private IO master;
    private DiskControl2 discController;
    private Diagnostics diagnostics;
    private Autonomous auto;
    private MikesBoard mikesBoard;
    private boolean enteredTestMode = false;

    
    public static final int THREAD_SLEEP_TIME = 30;


    public void robotInit(){
        
        master = IO.getInstance();
        master.start(); 
        
        drives = Drives.getInstance();
        drives.start();
                               
        discController = DiskControl2.getInstance();
        discController.start();
        
        ascender = Ascender.getInstance();
        ascender.start();
        
        mikesBoard = MikesBoard.getInstance();
        mikesBoard.start();
        
        
        diagnostics = Diagnostics.getInstance();
        diagnostics.start();
        
        auto = Autonomous.getInstance(); 
        auto.start();
       }
    
    

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        auto.setLocalMode(SubSystem.AUTO);  
        auto.runAuto(true);
        drives.setLocalMode(SubSystem.AUTO);
        discController.setLocalMode(SubSystem.AUTO);
    }
    

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        auto.runAuto(false);
        auto.setLocalMode(SubSystem.TELEOP);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
         drives.setLocalMode(SubSystem.TELEOP);
         ascender.setLocalMode(SubSystem.TELEOP);
         discController.setLocalMode(SubSystem.TELEOP);
         mikesBoard.setLocalMode(SubSystem.TELEOP);  
         diagnostics.setLocalMode(SubSystem.TELEOP);
    }
    
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {
        if (isEnabled()) {
            LiveWindow.setEnabled(true);
            master.startTestMode();
            LiveWindow.run();
            enteredTestMode = true;
        }
    }
    
    public void disabled(){
        if (enteredTestMode) {
            master.stopTestMode();
            LiveWindow.setEnabled(false);
        }
        drives.yield();
        diagnostics.yield();
        discController.yield();
        ascender.yield();
        auto.yield();
    }

}

