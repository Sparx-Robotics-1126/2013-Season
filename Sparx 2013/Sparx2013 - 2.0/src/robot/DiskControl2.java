package robot;

/**
 *
 * @author Connor
 * @author Justin
 */
public class DiskControl2 extends SubSystem{
    
    private static final boolean RAMP_UP = true;
    private static final boolean RAMP_DOWN = false;
    private static final boolean SHOOTING_POSITION = true;
    private static final boolean PICKUP_POSITION = false;
    private static final boolean PUNCHY_UP = true;
    private static final boolean PUNCHY_DOWN = false;
    private static final int STOP_SHOOTER = 0;
    private static final int LOW_GOAL     = 1;
    private static final int MIDDLE_GOAL  = 2;
    private static final int HIGH_GOAL    = 3;
    private static final int PYRAMID_GOAL = 4;
    private static final int LOW_GOAL_SHOOTER_SPEED = 2000;
    private static final int MIDDLE_GOAL_FRONT_SHOOTER_SPEED = 5350;
    private static final int MIDDLE_GOAL_BACK_SHOOTER_SPEED  = 6150;//6650
    private static final int HIGH_GOAL_FRONT_SHOOTER_SPEED   = 6000;
    private static final int HIGH_GOAL_BACK_SHOOTER_SPEED    = 7700;//7000;
    private static final int PYRAMID_GOAL_SHOOTING_SPEED     = 0;
    
    private  IO master;
    private  OperatorJoystick2 joystick;
    private  DriverJoysticks testJoy;
    
    private boolean autoShooterInPosition = false;
    private boolean autoFloorPickupInPosition = false;
    private boolean shootDisk = false;
    private int shootingMode = ShooterMode.HIGH;
    private boolean pyramidShooting = false;
    private boolean frontOfPyramid = false;
    private boolean rollerReverseMode = false;
    private boolean RPMShooting = true;
    private boolean notPunched = true;
    private boolean readyToFire = false;
    private boolean setForAuto = false;
    private boolean manualPunchy = false;
    private boolean firstTilt = false;
    
    private int operatingMode;
    private int speedError = 0;
    private double shootingSpeed = 0.0;
    
    private DiskControl2(){
         super("disccontrol");
         joystick = OperatorJoystick2.getInstance();
         testJoy = DriverJoysticks.getInstance();
         master = IO.getInstance();
    }
    
    /**
     * This is the private instance of the singleton
     */
    private static DiskControl2 disccontrol;
    
    /**
     * Use this method to get the instance of {@link robot.DiskControl2}.  If it
     * is not created already then it creates an instance and returns it.
     * 
     * @return the instance.
     */
    public static DiskControl2 getInstance(){
         if (disccontrol == null){
             disccontrol = new DiskControl2();
         }
         return disccontrol;
     }
    
    public void run(){//NEED TO ADD TIME STOP FOR ELEVATIONS TO GO UP
        //GET VALUES
        while(true){
            //diagnostics mode
            if(joystick.getInDiagnosticsMode()){
                if(master.getDiagnosticsMode()){
                    master.disableDiagnosticsMode();
                    print("Disabled Diagnostics");
                }else{
                    master.enableDiagnosticsMode();
                    print("Enabled Diagnostics");
                }
                sleep(500);
            }

            if(!master.getDiagnosticsMode()){
                if(getLocalMode() == SubSystem.TELEOP){
                    if(joystick.getRampButton()){
                        operatingMode = OperatingMode.FLOOR;
                        speedError = 0;
                    }else if(joystick.getShootingButton()){
                        operatingMode = OperatingMode.SHOOTER;
                        RPMShooting = true;
                        speedError = 0;
                    }else if(joystick.getInbounderButton()){
                        operatingMode = OperatingMode.INBOUNDER;
                        speedError = 0;
                    }else if(joystick.getOffButton()){
                        operatingMode = OperatingMode.OFF;
                        speedError = 0;
                    }
        
                    if(joystick.getVoltageMode()){
                        RPMShooting = false;
                    }
                    setShootingMethod(RPMShooting);//SmartDashboard
        
                    if(joystick.highFrontGoalPreset()){
                        shootingMode = ShooterMode.HIGH;
                        frontOfPyramid = true;
                        speedError = 0;
                    }else if(joystick.highBackGoalPreset()){
                        shootingMode = ShooterMode.HIGH;
                        frontOfPyramid = false;
                        speedError = 0;
                    }else if(joystick.middleFrontGoalPreset()){
                        shootingMode = ShooterMode.MIDDLE;
                        frontOfPyramid = true;
                        speedError = 0;
                    }else if(joystick.middleBackGoalPreset()){
                        shootingMode = ShooterMode.MIDDLE;
                        frontOfPyramid = false;
                        speedError = 0;
                    }else if(joystick.lowGoalPreset()){
                        shootingMode = ShooterMode.LOW;
                        speedError = 0;
                    }
        
                    if(joystick.fireDisk()){
                        shootDisk = true;
                    }else if(getLocalMode() == SubSystem.TELEOP){
                    shootDisk = false;
                    }else{
                        shootDisk = false;
                    }
        
                    if(joystick.getRollerReverse()){
                        rollerReverseMode = true;
                    }else{
                        rollerReverseMode = false;
                    }
                    
                    if(joystick.getPunchyManual()){
                        manualPunchy = true;
                    }else{
                        manualPunchy = false;
                    }
        
                    if(joystick.increaseSpeedTrim()){
                        speedError = speedError + 5;
                    }else if(joystick.decreaseSpeedTrim()){
                        speedError = speedError - 5;
                    }
                    
                    if(joystick.getAutoStart()){
                        setForAuto = true;
                    }else{
                        setForAuto = false;
                    }
                }
                
                //Floor Pickup
                if(rollerReverseMode){
                    setIntakeMotor(-1);
                    setRamp(RAMP_UP);
                }else if(manualPunchy){
                    diskAdjust();
                }else if(operatingMode == OperatingMode.FLOOR){
                    setShootingSpeed(STOP_SHOOTER, false);
                    setTowerAngle(LOW_GOAL);//HOME POSITION
                    setTowerPostion(PICKUP_POSITION, false);
                    setRamp(RAMP_DOWN);
                    setIntakeMotor(1);
                    master.rainbowStrip();
                }else if(operatingMode == OperatingMode.SHOOTER){
                    master.oneColorStripFlashing(0, 127, 0);
                    master.setLEDStrobeSpeed(200);
                    setShootingPresets();
                    setTowerPostion(SHOOTING_POSITION, true);
                    setRamp(RAMP_UP);
                    setIntakeMotor(0);
                }else if(operatingMode == OperatingMode.INBOUNDER){
                    setShootingSpeed(STOP_SHOOTER, false);
                    setTowerAngle(MIDDLE_GOAL);
                    setTowerPostion(SHOOTING_POSITION, false);
                    setRamp(RAMP_UP);
                    setIntakeMotor(0);
                    master.rainbowStrip();
                }else if(operatingMode == OperatingMode.OFF){
                    setShootingSpeed(STOP_SHOOTER, false);
                    setIntakeMotor(0);
                    setTowerAngle(LOW_GOAL);
                    master.setElevations(0);
//                    setTowerPostion(PICKUP_POSITION);
                    setRamp(RAMP_UP);
                    master.setGyroColor();
                }else{
                    setShootingSpeed(STOP_SHOOTER, false);
                    setRamp(RAMP_UP);
                    setIntakeMotor(0);
                    master.rainbowStrip();
                }
                
                //Punchy
                if(operatingMode != OperatingMode.SHOOTER){
                    master.setPunchyTop(PUNCHY_DOWN);
                }

        
                if(shootDisk && operatingMode == OperatingMode.SHOOTER){//NEED TO ADD UP TO SPEED METHOD
                    diskAdjust();
                    master.setKicker(true);
                    pidSleep(100);
                    //AUTO 
                if(getLocalMode() == SubSystem.AUTO){
                        pidSleep(50);
                        shootDisk = false;
                }
                }else{
                    master.setKicker(false);
  
                }
            }
        }
    }
    
    private void setRamp(boolean rampPosition){
        master.setPickUpMode(rampPosition, true);
    }

    private void setTowerAngle(int points){
        switch(points){
            case LOW_GOAL://HOME POSTIONS
                master.setTowerTilt1(false);
                master.setTowerTilt2(false);
                break;
            case MIDDLE_GOAL://MIDDLE GOAL
                master.setTowerTilt1(true);
                master.setTowerTilt2(false);
                break;
            case HIGH_GOAL://MIDDLE GOAL
                master.setTowerTilt1(false);
                master.setTowerTilt2(true);
                break;
            case PYRAMID_GOAL://TOP OF PYRAMID
                master.setTowerTilt1(true);
                master.setTowerTilt2(true);
                break;
            default:
                master.setTowerTilt1(false);
                master.setTowerTilt2(false);
                break;
        }    
    }
    
    private void setTowerPostion(boolean position, boolean shooting){
        print("LOWER: " + master.getBottomSensor() + " FRISBEE: " + master.getPlateLocation());
        if(position == SHOOTING_POSITION && firstTilt){
            sleep(500);
            firstTilt = false;
        }
        if(position == SHOOTING_POSITION && !master.getPlateLocation()){
            master.setElevations(0.50);
            autoShooterInPosition = false;
            notPunched = true;
        }else if(position == PICKUP_POSITION && !master.getBottomSensor()){
            master.setElevations(-0.65);
            notPunched = true;
            autoFloorPickupInPosition = false;
            firstTilt = true;
        }else if(master.getPlateLocation() && notPunched){
            notPunched = false;
            //diskAdjust();
        }else if(position == SHOOTING_POSITION && master.getPlateLocation() && shooting){
            master.setElevations(0.2);
        }else{
            master.setElevations(0);
        }
        
        if(master.getPlateLocation()){//for AUTO
            autoShooterInPosition = true;
        }else if(master.getBottomSensor()){
            autoFloorPickupInPosition = true;
        }
    }
    
    private void setShootingSpeed(int points, boolean frontOfPyramid){
        double wantedSpeed;
        if(RPMShooting){
            switch(points){
                case STOP_SHOOTER:
                    wantedSpeed = 0;
                    break;
                case LOW_GOAL:
                    wantedSpeed = LOW_GOAL_SHOOTER_SPEED;
                    break;
                case MIDDLE_GOAL:
                    if(frontOfPyramid){
                        wantedSpeed = MIDDLE_GOAL_FRONT_SHOOTER_SPEED;
                    }else{
                        wantedSpeed = MIDDLE_GOAL_BACK_SHOOTER_SPEED;
                    }
                    break;
                case HIGH_GOAL:
                    if(frontOfPyramid){
                        wantedSpeed = HIGH_GOAL_FRONT_SHOOTER_SPEED;
                    }else{
                        wantedSpeed = HIGH_GOAL_BACK_SHOOTER_SPEED;
                    }
                    break;
                case PYRAMID_GOAL:
                    wantedSpeed = PYRAMID_GOAL_SHOOTING_SPEED;
                    break;
                default:
                    wantedSpeed = 0;
                    break;
            }
            
        master.calculateShootingSpeed();
        // print(""+speedError);
        print("Wanted Speed " + (wantedSpeed + speedError) + " Real Speed " + master.getShooterSpeed());
        master.setPIDShooting((wantedSpeed + speedError));
        shootingSpeed = master.getPIDShooting();
        master.setShooter(shootingSpeed);
        
        // For the smartDashboard
        if((wantedSpeed+speedError) > master.getShooterSpeed() - 50 && (wantedSpeed+speedError) < master.getShooterSpeed() + 50){
            readyToFire = true;
        }else{
            readyToFire = false;
        }
        setSmartReadytoShoot(readyToFire);
        
        }else{//STRAIGHT VOLTAGE OUTPUT
            switch(points){
                case STOP_SHOOTER:
                    wantedSpeed = 0;
                    break;
                case LOW_GOAL:
                    wantedSpeed = 0.93;
                    break;
                case MIDDLE_GOAL:
                    if(frontOfPyramid){
                        wantedSpeed = 0.93;
                    }else{
                        wantedSpeed = 0.93;//0.85;
                    }
                    break;
                case HIGH_GOAL:
                    if(frontOfPyramid){
                        wantedSpeed = 0.93;
                    }else{
                        wantedSpeed = 0.93;
                    }
                    break;
                case PYRAMID_GOAL:
                    wantedSpeed = 0;
                    break;
                default:
                    wantedSpeed = 0;
                    break;
            }
            shootingSpeed = wantedSpeed + (speedError * 0.0005);
        }
        master.setShooter(shootingSpeed);
        setSmartSpeed(master.getShooterSpeed());
    }
    
    private void setIntakeMotor(double motorValue){
        if(master.getBottomSensor()){
            master.setAcquire(motorValue * -1);
        }
    }
    
    public void autoSetPickup(int newMode){
        operatingMode = newMode;
    }
    
    private void setColorStrip(int red, int green, int blue, int LEDnum){
        master.oneColorStripFlashing(blue, red, green);
    }
    
    public void autoShoot(){
        shootDisk = true;
    }
    
    public boolean autoFloorSet(){
        return autoFloorPickupInPosition;
    }
    
    public boolean autoTowerSet(){
        return autoShooterInPosition;
    }
    
    public void autoPresets(int shootingMode, boolean frontOfPyramid, int offset){
        this.shootingMode = shootingMode;
        this.frontOfPyramid = frontOfPyramid;
        speedError = offset;
    }
    
    public boolean doneShooting(){
        return true;
    }
    
    private void setShootingPresets(){
        switch(shootingMode){
            case ShooterMode.LOW:
                setTowerAngle(LOW_GOAL);
                setShootingSpeed(LOW_GOAL, frontOfPyramid);
                setSmartString("Low Gaol");
                break;
                
            case ShooterMode.MIDDLE:
                if (frontOfPyramid) {
                    setTowerAngle(PYRAMID_GOAL);
                    setShootingSpeed(MIDDLE_GOAL, true);
                    setSmartString("Middle Goal, Front");
                } else {
                    setTowerAngle(MIDDLE_GOAL);
                    setShootingSpeed(MIDDLE_GOAL, false);
                    setSmartString("Middle Goal, Back");
                }
                break;
                
            case ShooterMode.HIGH:
                if (frontOfPyramid) {
                    setTowerAngle(PYRAMID_GOAL);
                    setShootingSpeed(HIGH_GOAL, true);
                    setSmartString("High Goal, Front");
                } else {
                    setTowerAngle(MIDDLE_GOAL);
                    setShootingSpeed(HIGH_GOAL, false);
                    setSmartString("High Goal, Back");
                }
                break;
                
            case ShooterMode.DEFAULT:
            default:
                setTowerAngle(MIDDLE_GOAL);
                setShootingSpeed(LOW_GOAL, true);
                setSmartString("No preset selected");
                break;
        }
    }
    
    private void diskAdjust(){
        master.setPunchyTop(PUNCHY_UP);
        pidSleep(75);
        master.setPunchyTop(PUNCHY_DOWN);
        pidSleep(75);
        master.setPunchyTop(PUNCHY_UP);
        pidSleep(100);
    }
    
    //SMARTDASHBOARD
    
    /**
     * Prints number to Smart Dashboard
     * @param number - shooter speed
     */
    public void setSmartSpeed(double number){
        master.setSmartShooterSpeed(number);
    }
    
    public void setSmartString(String text){
        master.setSmartPresetString(text);
    }
    
    public void setShootingMethod(boolean type){
        if(RPMShooting){
            master.setSmartRPMShooting(true);
            master.setSmartVoltageShooting(false);
        }else{
            master.setSmartRPMShooting(false);
            master.setSmartVoltageShooting(true);
        }
    }
    
    public void setSmartReadytoShoot(boolean value){
        master.setSmartReadyToShoot(value);
    }
    
    public void pidSleep(int seconds){
        while (seconds > 0)
        {
            if (seconds < 21)
            {
                sleep(seconds);
                return;
            }
            sleep(20);
            seconds -= 20;
            setShootingPresets();
        }
    }
}

