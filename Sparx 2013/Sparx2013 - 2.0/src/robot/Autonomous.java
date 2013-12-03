package robot;

public class Autonomous extends SubSystem{
    private static Autonomous auto;  
//    private SendableChooser autoChooser;
//    private DiscControl diskControl;
    private DiskControl2 diskControl2;
    private Drives drives;
    private IO master;
    private Ascender ascender;
    private int[][] currentAutonomous;
    
    public static final int FIRST_OFFSET                    = 0;
    public static final int SECOND_OFFSET                   = 100;
    public static final int THIRD_OFFSET                    = 100;
    public static final int CLIMBERS_UP                 = 0;
    public static final int CLIMBERS_DOWN                   = 1;

    public static final boolean TURNING_LEFT                = false;
    public static final boolean TURNING_RIGHT               = true;
    public static final boolean DRIVING_FORWARDS            = true;
    public static final boolean DRIVING_REVERSE             = false;
    public static boolean runAutonomous                     = true;
    public static final int LOWER_GOAL                      = 1;
    public static final int MIDDLE_GOAL                     = 2;
    public static final int HIGH_GOAL                       = 3;
    public static final int FRONT_OF_PYRAMID                = 0;
    public static final int BACK_OF_PYRAMID                 = 1;
    /**************************************************************************/
    /*************************Manual Switch Voltages **************************/
    /**************************************************************************/
    public static final double AUTO_SETTING_1 = 3.208;
    public static final double AUTO_SETTING_2 = 3.126;
    public static final double AUTO_SETTING_3 = 3.036;
    public static final double AUTO_SETTING_4 = 2.935;
    public static final double AUTO_SETTING_5 = 2.824;
    public static final double AUTO_SETTING_6 = 2.701;
    public static final double AUTO_SETTING_7 = 2.563;
    public static final double AUTO_SETTING_8 = 2.405;
    public static final double AUTO_SETTING_9 = 2.225;
    /**************************************************************************/
    /************************ Autonomous commands *****************************/
    /**************************************************************************/
    
    /* Drives */
    public static final int DRIVES_GO_FORWARD               = 1;//distance(inches), speed (int)
    public static final int DRIVES_GO_REVERSE               = 2;
    public static final int DRIVES_TURN_RIGHT               = 3;
    public static final int DRIVES_TURN_LEFT                = 4;
    public static final int DRIVES_STOP                     = 5;
    public static final int DRIVES_DONE                     = 9;
    /* Shooter */
    public static final int SHOOTER_ENABLE                  = 20;
    public static final int SHOOTER_DISABLE                 = 21;
    public static final int SHOOTER_FIRE                    = 22;
    public static final int SHOOTER_PRESETS                 = 23;
    public static final int SHOOTER_DONE                    = 29;
    public static final int SHOOTER_IN_POSITION             = 24;
    
    /* Acquisitions */
    public static final int ACQUISITIONS_ON                 = 30;
    public static final int ACQUISITIONS_OFF                = 31;
    public static final int ACQUISITIONS_IN_POSITION        = 32;
    public static final int ACQUISITION_DONE                = 39;
    
    /* Ascenders */
    public static final int ASCENDERS_MOVE_HORNS            = 40;
    
    /* Misc */
    public static final int WAIT                            = 90;
    public static final int END                             = 99;
    /**************************************************************************/
    /**************************** End of commands *****************************/
    /**************************************************************************/
    
    public static final int[][] noAuto = { 
        {WAIT, 2000},
        {ASCENDERS_MOVE_HORNS, CLIMBERS_DOWN},
        {END}
    };
            
    private static final int[][] backPyramidToMiddle = {
        {ASCENDERS_MOVE_HORNS, CLIMBERS_DOWN},
        {SHOOTER_ENABLE},
        {SHOOTER_PRESETS, MIDDLE_GOAL, BACK_OF_PYRAMID, FIRST_OFFSET},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_DISABLE},
        {END}
    };
    
    private static final int[][] frontPyramidToMiddle = {
        {ASCENDERS_MOVE_HORNS, CLIMBERS_DOWN},      
        {SHOOTER_PRESETS, MIDDLE_GOAL, FRONT_OF_PYRAMID, FIRST_OFFSET},
        {SHOOTER_ENABLE},
        {WAIT, 4000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_DISABLE},
        {END}
    };
    
    private static final int[][] backPyramidToTop = {
        {ASCENDERS_MOVE_HORNS,CLIMBERS_DOWN},
        {SHOOTER_PRESETS, HIGH_GOAL, BACK_OF_PYRAMID, FIRST_OFFSET},
        {SHOOTER_ENABLE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {SHOOTER_PRESETS, HIGH_GOAL, BACK_OF_PYRAMID, -700},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {SHOOTER_PRESETS, HIGH_GOAL, BACK_OF_PYRAMID, -900},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_DISABLE},
        {END}
    };
    
    private static final int[][] frontPyramidToTop = {
        {ASCENDERS_MOVE_HORNS, CLIMBERS_DOWN},      
        {SHOOTER_PRESETS, HIGH_GOAL, FRONT_OF_PYRAMID, FIRST_OFFSET},
        {SHOOTER_ENABLE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_FIRE},
        {WAIT, 2000},
        {SHOOTER_DISABLE},
        {END}
    };
    
    public static final int [][] fiveDiskAutoToTop = {
        {ASCENDERS_MOVE_HORNS, CLIMBERS_DOWN},            
        {SHOOTER_PRESETS, HIGH_GOAL, BACK_OF_PYRAMID, FIRST_OFFSET},
        {SHOOTER_ENABLE},
        {SHOOTER_IN_POSITION}, 
        {WAIT, 1000},
        {SHOOTER_FIRE},
        {SHOOTER_PRESETS, HIGH_GOAL, BACK_OF_PYRAMID, SECOND_OFFSET},
        {WAIT, 700},
        {SHOOTER_FIRE},
        {SHOOTER_PRESETS, HIGH_GOAL, BACK_OF_PYRAMID, THIRD_OFFSET},
        {WAIT, 700},
        {SHOOTER_FIRE},
        {WAIT, 700},
        {SHOOTER_DISABLE},
        {ACQUISITIONS_ON},
        {DRIVES_GO_FORWARD, 18},
        {DRIVES_DONE},
        {DRIVES_TURN_LEFT, 80},
        {DRIVES_DONE},
        {ACQUISITIONS_IN_POSITION},
        {DRIVES_GO_FORWARD, 45},
        {DRIVES_DONE},
        {DRIVES_TURN_RIGHT, 90},
        {SHOOTER_PRESETS, MIDDLE_GOAL, BACK_OF_PYRAMID, FIRST_OFFSET},
        {DRIVES_DONE},
        {WAIT, 1000},
        {ACQUISITIONS_OFF},
        {DRIVES_DONE},
        {SHOOTER_ENABLE},
        {SHOOTER_IN_POSITION}, 
        {WAIT, 1000},
        {SHOOTER_FIRE},
        {WAIT, 1200},
        {SHOOTER_FIRE},
        {WAIT, 500},
        {SHOOTER_DISABLE},
        {END},
        
    };
    
     public static final int [][] fiveDiskAutoToMiddle = {
        {ASCENDERS_MOVE_HORNS, CLIMBERS_DOWN},            
        {SHOOTER_PRESETS, MIDDLE_GOAL, BACK_OF_PYRAMID, FIRST_OFFSET},
        {SHOOTER_ENABLE},
        {SHOOTER_IN_POSITION}, 
        {WAIT, 1000},
        {SHOOTER_FIRE},
        {SHOOTER_PRESETS, MIDDLE_GOAL, BACK_OF_PYRAMID, SECOND_OFFSET},
        {WAIT, 700},
        {SHOOTER_FIRE},
        {SHOOTER_PRESETS, MIDDLE_GOAL, BACK_OF_PYRAMID, THIRD_OFFSET},
        {WAIT, 700},
        {SHOOTER_FIRE},
        {WAIT, 800},
        {SHOOTER_DISABLE},
        {DRIVES_GO_FORWARD, 18},
        {DRIVES_DONE},
        {DRIVES_TURN_LEFT, 55},
        {DRIVES_DONE},
        {ACQUISITIONS_IN_POSITION},
        {ACQUISITIONS_ON},
        {DRIVES_GO_FORWARD, 12},//
//        {DRIVES_DONE},
//        {DRIVES_TURN_RIGHT, 90},
//        {SHOOTER_PRESETS, MIDDLE_GOAL, BACK_OF_PYRAMID, FIRST_OFFSET},
//        {DRIVES_DONE},
//        {WAIT, 1000},
//        {ACQUISITIONS_OFF},
//        {DRIVES_DONE},
//        {SHOOTER_ENABLE},
//        {SHOOTER_IN_POSITION}, 
//        {WAIT, 1000},
//        {SHOOTER_FIRE},
//        {WAIT, 1200},
//        {SHOOTER_FIRE},
//        {WAIT, 500},
//        {SHOOTER_DISABLE},
        {END},
     };
    
    
    public Autonomous(){
        super("Autonomous");
        diskControl2 = DiskControl2.getInstance();
        drives = Drives.getInstance();
        master = IO.getInstance();
        ascender = Ascender.getInstance();
        
        //SMARTDASHBOARD
//        autoChooser = new SendableChooser();
//        autoChooser.addDefault("Back of Pyramid To Middle", new Integer(1));
//        autoChooser.addObject("Front of Pyramid To Middle", new Integer(2));
//        autoChooser.addObject("No Auto", new Integer(3));
//        SmartDashboard.putData("Autonomous", autoChooser);
//        SmartDashboard.putBoolean("Using Manual Autonomous choosing", false);
    }
   
    
    public static Autonomous getInstance(){
         if (auto == null){
             auto = new Autonomous();
         }
         return auto;
    }
    
    public void init(){
//        Integer autonomousMode = (Integer)autoChooser.getSelected();//Gets Dashboard Value
//        if (!SmartDashboard.getBoolean("Using Manual Autonomous choosing")){
//            switch(autonomousMode.intValue()){
//                case 1:
//                  currentAutonomous = backPyramidToMiddle;
//                   break;
//             case 2: 
//                   currentAutonomous = frontPyramidToMiddle;
//                   break;
//             case 3:
//                 currentAutonomous = noAuto;
//                default:
//                    currentAutonomous = noAuto;
//                     print("Please insert a proper value");      
//            }
//        }else{
           double voltage = master.getVoltage();
           if (voltage >= AUTO_SETTING_1){
               currentAutonomous = backPyramidToMiddle;
               print("backPyramidToMiddle");
           }else if (voltage >= AUTO_SETTING_2){
               currentAutonomous = frontPyramidToMiddle;
               print("frontPyramidToMiddle");
           }else if (voltage >= AUTO_SETTING_3){
               currentAutonomous = backPyramidToTop;
               print("backPyramidToTop");
           }else if (voltage >= AUTO_SETTING_4){
               currentAutonomous = frontPyramidToTop;
               print("frontPyramidToTop");
           }else if (voltage >= AUTO_SETTING_5){
               currentAutonomous = fiveDiskAutoToMiddle;
               print("fiveDiskAutoToMiddle");
           }else if (voltage >= AUTO_SETTING_6){
               currentAutonomous = fiveDiskAutoToTop;
               print("fiveDiskAutoToTop");
           }else if (voltage >= AUTO_SETTING_7){
               currentAutonomous = noAuto;
               print("noAuto");
           }else{
               currentAutonomous = noAuto;
           }
//        }
    }
    
    public void run(){
          init();
//        currentAutonomous = fiveDiskAutoToMiddle; //////ERASE FOR REAL THING
        int start = 0, current = start, finished = currentAutonomous.length;
//        print("Length of Array: " + finished);
        while (true){
            while(getLocalMode() == SubSystem.AUTO &&  isEnabled()){
                    current++;
                for (int i = start; i <= finished; i++){
                    if (isEnabled() && runAutonomous){
                    switch (currentAutonomous[i][0]){
                        case DRIVES_GO_FORWARD:
                            print("Driving forward");
                            drivesGo(currentAutonomous[i][1], DRIVING_FORWARDS, 0);
                            break;
                        case DRIVES_GO_REVERSE:
                            print("Driving reverse");
                            drivesGo(-currentAutonomous[i][1], DRIVING_REVERSE, 0);
                            break;
                        case DRIVES_TURN_LEFT:
                            print("Turning Left");
                            drivesTurn(currentAutonomous[i][1], TURNING_LEFT);
                            break;
                        case DRIVES_TURN_RIGHT:
                            print("Turning Right");
                            drivesTurn(currentAutonomous[i][1], TURNING_RIGHT);
                        case DRIVES_STOP:
                            print("Stopping");
                            drivesStop();
                            break;
                        case DRIVES_DONE:
                            print("DriveDone() was called");
                            drivesDone();
                            break;
                        case SHOOTER_ENABLE:
                            shooterEnable();
                            break;
                        case SHOOTER_DISABLE:
                            shooterDisable();
                            break;
                        case SHOOTER_FIRE:
                            shooterFire();
                            break;
                        case SHOOTER_PRESETS:
                            setShooterPresets(currentAutonomous[i][1], currentAutonomous[i][2], currentAutonomous[i][3]);
                            break;
                        case SHOOTER_IN_POSITION:
                            towerInPosition();
                            break;
                        case SHOOTER_DONE:
                            shooterDone();
                            break;
                        case ACQUISITIONS_ON:
                            acquisitionsOn();
                            break;
                        case ACQUISITIONS_OFF:
                            acquisitionsOff();
                            break;
                        case ACQUISITIONS_IN_POSITION:
                            floorPickupInPosition();
                            break;
                        case ASCENDERS_MOVE_HORNS:
                            ascendersMoveHorns(currentAutonomous[i][1]);
                            break;
                        case WAIT:
                            wait(currentAutonomous[i][1]);
                            break;
                        case END:
                            runAutonomous = false;
                        default:
                            print("No case statement: " + currentAutonomous[i]);
                    }
                } 
                sleep(30);   
            }
        }            
        sleep(30);   
      }
    }
    
    /**
     * Sets Drives goals in autonomous
     * @param numberOfInches - desired number of inches to go forward
     * @param direction - False = backwards, True = forwards
     */
    private void drivesGo(int numberOfInches, boolean direction, double speed){
        drives.setDriving(numberOfInches, direction, speed);
    }
    
    private void wait(int timeInMillSeconds){
            sleep(timeInMillSeconds);
    }
    /**
     * @param
     * degrees - number of degrees
     * direction - Direction to turn. Use finals TURING_LEFT and TURNING_RIGHT
     */
    private void drivesTurn(int degrees, boolean direction){
        print("calling drivesTurn");
       drives.setAutoTurn(degrees, direction);
    }
    
    private void drivesStop(){
        master.setLeftDrives(0);
        master.setRightDrives(0);
    }
    
    private void drivesSlowToStop(){//QUESTION: WHY DO WE HAVE THIS?
//        master.calculateDrivesSpeed();
//        master.calculateRightSpeed();
//        while (Math.abs(master.getLeftSpeed()) > 0.25 || Math.abs(master.getRightSpeed()) > 0.25){
//            if (master.getLeftSpeed() > 0.25)
//                master.setLeftDrives(master.getLeftSpeed()*0.05);
//            if (master.getRightSpeed() > 0.25)
//                master.setRightDrives(master.getRightSpeed() * 0.05);
//        }
        drivesStop();
    }
    
    private void shooterEnable(){
        print("ShooterEnabled");
        diskControl2.autoSetPickup(Mode.SHOOTER);
    }
    
    private void shooterDisable(){
        print("ShooterDisabled");
        diskControl2.autoSetPickup(Mode.FLOOR);
    }
    
    private void shooterFire(){
        print("ShooterFire");
        diskControl2.autoShoot();
    }
    
    private void acquisitionsOn(){
        print("acquisitionsON");
        diskControl2.autoSetPickup(Mode.FLOOR);
        
    }
    
    private void acquisitionsOff(){
        print("acquisitionsOFF");
        diskControl2.autoSetPickup(Mode.OFF);
    }
    
    private void ascendersMoveHorns(int value){
        if(value == CLIMBERS_DOWN){
           ascender.autoSetHorns(true); 
        }else if(value == CLIMBERS_UP){
           ascender.autoSetHorns(false);
        }
        
    }
    
    private void drivesDone(){
        print("" + drives.autoComplete());
        while(!drives.autoComplete()){
            sleep(10);
        }
    }
    
    public void shooterDone(){
        while(!diskControl2.doneShooting()){//not done
            diskControl2.autoShoot();
            sleep(2000);
        }
    }
    
    public void towerInPosition(){
        while(!diskControl2.autoTowerSet())
            sleep(10);
    }
            
    private void floorPickupInPosition(){
        while(!diskControl2.autoFloorSet())
            sleep(10);
    }
    
    public void runAuto(boolean run){
        runAutonomous = run;
    }
    
    public void setShooterPresets(int goal, int frontOfPyramid, int offset){
        boolean frontOfPyramidBoolean;
        switch(frontOfPyramid){
            case FRONT_OF_PYRAMID:
                frontOfPyramidBoolean = true;
                break;
            default:
                frontOfPyramidBoolean = false;
                break;
        }
        switch(goal){
            case LOWER_GOAL:
                diskControl2.autoPresets(true, false, false, frontOfPyramidBoolean, offset);
                break;
            case MIDDLE_GOAL:
                diskControl2.autoPresets(false, true, false, frontOfPyramidBoolean, offset);
                break;
            case HIGH_GOAL:
                diskControl2.autoPresets(false, false, true, frontOfPyramidBoolean, offset);
                break;
            default:
                diskControl2.autoPresets(false, false, false, frontOfPyramidBoolean, offset);
                break;
        }
    }
}