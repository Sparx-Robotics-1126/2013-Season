package robot;

import edu.wpi.first.wpilibj.AnalogModule;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IO extends SubSystem {

    ;
    public static final boolean MIKES_BOARD_GYRO = true;
    public static final boolean MIKES_BOARD_LED = true;
    public static final boolean MIKES_BOARD_ENABLED = true;
    public static final boolean MIKES_BOARD_DISABLED = false;
    public static boolean InDiagnostics;
    public static final double DIST_PER_TICK = 0.0245;
    private static IO master;
    //VARIABLES
    public static final boolean MIKES_BOARD = true;//TODO: test
    public static final double RPM_PER_TICK = 0.703125;//RPM
    public static final boolean CLIMBING_MODE = true;
    public static final boolean DRIVING_MODE = false;
    private static boolean climbing = false;//start driving not climbing
    private boolean addedItemsToLiveWindow = false;
    //COMPRESSOR
    private Compressor compressor;
    //SOLENOIDS  
    private Solenoid shift;
    private Solenoid climbClaws;
    private Solenoid towerTilt2;
    private Solenoid kicker;
    private Solenoid towerTilt1;
    private Solenoid floorPickup;
    private Solenoid punchy;
    private Solenoid punchy2;
    //MOTORS
    private Victor leftDrives1;
    private Victor leftDrives2;
    private Victor rightDrives1;
    private Victor rightDrives2;
    private Talon shooter1;
    private Talon shooter2;
    private Talon acquire;
    private Victor diskElevator;
    private Servo cameraServo;
    //ENCODERS
    private Encoder rightDrivesEncoder;
    private Encoder leftDrivesEncoder;
    private EncoderData rightDrivesEncoderData;
    private EncoderData leftDrivesEncoderData;
    private Counter shooterWheelEncoder;
    private EncoderData shooterWheelEncoderData;
    private DigitalInput elevationsLowerLimit;
    private DigitalInput platePosition;
    //
    public Gyro gyro;
    public PID pid;
    private MikesBoard board;
    private DigitalOutput mikesBoardSetting;
    private AnalogModule autoSwitch;
    private AnalogModule leftBendSensor;
    private AnalogModule rightBendSensor;

    // ********* Contstructor *************************
    private IO() {
        super("IO");
        //COMPRESSOR
        compressor = new Compressor(DEFAULT_SLOT, pressureSwitchChannel,
                DEFAULT_SLOT, compresssorRelayChannel);
        compressor.start();
        //SOLENOIDS
        shift = new Solenoid(DEFAULT_SLOT, pneuShiftChan);//ALTERNATE
//        climbClaws = new Solenoid(DEFAULT_SLOT, pneuClimbClaws);
        kicker = new Solenoid(DEFAULT_SLOT, pneuKicker);
        floorPickup = new Solenoid(DEFAULT_SLOT, pneuFloorPickup);
        towerTilt1 = new Solenoid(DEFAULT_SLOT, pneuTilt1);
        towerTilt2 = new Solenoid(DEFAULT_SLOT, pneuTilt2);
        climbClaws = new Solenoid(DEFAULT_SLOT, pneuClimbClaws);
        punchy = new Solenoid(DEFAULT_SLOT, pneuPunchy);
        punchy2 = new Solenoid(DEFAULT_SLOT, pneuPunckyTwo);

        //MOTORS
        leftDrives1 = new Victor(DEFAULT_SLOT, PWMLeftDrives1);
        leftDrives2 = new Victor(DEFAULT_SLOT, PWMLeftDrives2);
        rightDrives1 = new Victor(DEFAULT_SLOT, PWMRightDrives1);
        rightDrives2 = new Victor(DEFAULT_SLOT, PWMRightDrives2);
        shooter1 = new Talon(DEFAULT_SLOT, PWMShooterWheel1);
        shooter2 = new Talon(DEFAULT_SLOT, PWMShooterWheel2);
        diskElevator = new Victor(DEFAULT_SLOT, PWMDiskElevator1);
        acquire = new Talon(DEFAULT_SLOT, PWMAcquire);
        cameraServo = new Servo(DEFAULT_SLOT, PWMCameraServo);


        //ENCODERS
        leftDrivesEncoder = new Encoder(DEFAULT_SLOT, leftEncoderChan1, DEFAULT_SLOT, leftEncoderChan2, false, EncodingType.k4X);
        leftDrivesEncoder.setDistancePerPulse(DIST_PER_TICK);
        leftDrivesEncoder.start();
        leftDrivesEncoderData = new EncoderData(leftDrivesEncoder, DIST_PER_TICK);

        rightDrivesEncoder = new Encoder(DEFAULT_SLOT, rightEncoderChan1, DEFAULT_SLOT, rightEncoderChan2, true, EncodingType.k4X);
        rightDrivesEncoder.setDistancePerPulse(DIST_PER_TICK);
        rightDrivesEncoder.start();
        rightDrivesEncoderData = new EncoderData(rightDrivesEncoder, DIST_PER_TICK);

        shooterWheelEncoder = new Counter(DEFAULT_SLOT, shooterWheelChan);
        shooterWheelEncoder.start();
        shooterWheelEncoderData = new EncoderData(shooterWheelEncoder, RPM_PER_TICK);

//        counterShooterAngle = new Counter(DEFAULT_SLOT, POTShooterAngle);
//        counterShooterAngle.start();
//        counterShooterAngleEncoderData = new EncoderData(counterShooterAngle, DIST_PER_TICK);
//        
        //SENSORS
//        gyro = new Gyro(DEFAULT_SLOT, gyroChan);
//        elevationsUpperLimit = new DigitalInput(DEFAULT_SLOT, upperLimit);
//        elevationsLowerLimit = new DigitalInput(DEFAULT_SLOT, lowerLimit);
        platePosition = new DigitalInput(DEFAULT_SLOT, upperLimit);
        elevationsLowerLimit = new DigitalInput(DEFAULT_SLOT, lowerLimit);
        pid = new PID(0.0003875, 0.00138, 1, 0, 1, 0, true, false);
        board = MikesBoard.getInstance();
        mikesBoardSetting = new DigitalOutput(DEFAULT_SLOT, mikesBoardsettingChannel);
        autoSwitch = AnalogModule.getInstance(DEFAULT_SLOT);
        leftBendSensor = AnalogModule.getInstance(DEFAULT_SLOT);
        rightBendSensor = AnalogModule.getInstance(DEFAULT_SLOT);
    }

    public void run() {
        while (true) {
            sleep(10);
        }
    }

    // ********* LiveWindow *************************
    // Use of LiveWindow Test Mode
    public void startTestMode() {
//        if (!addedItemsToLiveWindow) {
//            // added the sensor display and actuator controls to the livewindow UI.
//            LiveWindow.addSensor("Robot", "Compressor", compressor);
//            LiveWindow.addSensor("Drives", "shifter", shift);
//            LiveWindow.addSensor("Drives", "Right Encoder", rightDrivesEncoder);
//            LiveWindow.addSensor("Drives", "Left Encoder", leftDrivesEncoder);
//            LiveWindow.addSensor("Drives", "Gyro", gyro);
//            LiveWindow.addSensor("Shooter", "Shooter Wheel Encoder", shooterWheelEncoder);
//            LiveWindow.addActuator("Drives", "Left Talon 1",leftDrives1);
//            LiveWindow.addActuator("Drives", "Left Talon 2",leftDrives2); 
//            LiveWindow.addActuator("Drives", "Right Talon 1",rightDrives1);
//            LiveWindow.addActuator("Drives", "Right Talon 2",rightDrives2);
//            LiveWindow.addActuator("Climbing", "Climbing Claws", climbClaws);
//            LiveWindow.addActuator("Shooter", "Kicker", kicker);
//            LiveWindow.addActuator("Shooter", "Shooter Wheel", shooter1);
//            LiveWindow.addActuator("Shooter", "Shooter Angle", shooter2);
//            LiveWindow.addActuator("Acquisitions", "Floor Pickup", floorPickup);         
//            LiveWindow.addActuator("Elevations", "Disk Elevator 1", diskElevator);
//            addedItemsToLiveWindow = true;
//        }
//        
//        // allow the livewindow to directly control the devices
//        compressor.startLiveWindowMode();
//        shift.startLiveWindowMode();
//        rightDrivesEncoder.startLiveWindowMode();
//        leftDrivesEncoder.startLiveWindowMode();
//        leftDrives1.startLiveWindowMode();
//        leftDrives2.startLiveWindowMode();       
//        rightDrives1.startLiveWindowMode();       
//        rightDrives2.startLiveWindowMode();
////        gyro.startLiveWindowMode();
//        shooterWheelEncoder.startLiveWindowMode();
//        climbClaws.startLiveWindowMode();
//        kicker.startLiveWindowMode();
//        shooter1.startLiveWindowMode();
//        shooter2.startLiveWindowMode();
//        floorPickup.startLiveWindowMode();
//        diskElevator.startLiveWindowMode();
    }
//    

    public void stopTestMode() {
//        // stop allowing the livewindow controls to control the devices
//        compressor.stopLiveWindowMode();
//        shift.stopLiveWindowMode();
//        rightDrivesEncoder.stopLiveWindowMode();
//        leftDrivesEncoder.stopLiveWindowMode();
//        leftDrives1.stopLiveWindowMode();
//        leftDrives2.stopLiveWindowMode();       
//        rightDrives1.stopLiveWindowMode();       
//        rightDrives2.stopLiveWindowMode(); 
    }

    // ********* DISCONTROL methods *************************
    public void setShooter(double speed) {
        shooter1.set(speed * -1);
        shooter2.set(speed * -1);
    }

    public void setKicker(boolean position) {
        kicker.set(position);
    }

    public void setPickUpMode(boolean position) {
        floorPickup.set(position);
        if (position) {
            setAcquire(1);
        } else if (!position) {
            setAcquire(0);
        }
    }

    public void setPunchyTop(boolean position) {
        punchy.set(position);
    }

    public void setPunchyBottom(boolean position) {
        punchy2.set(position);
    }

    public void setPickUpMode(boolean position, boolean justRamp) {
        floorPickup.set(position);
    }

    public void setTowerTilt1(boolean on) {
        towerTilt1.set(on);
    }

    public void setTowerTilt2(boolean on) {
        towerTilt2.set(on);
    }

    public void setAcquire(double speed) {
        acquire.set(speed);
    }

    public void setCameraServo(double position) {
        cameraServo.set(position);
    }

    public boolean getPlateLocation() {
        return !platePosition.get();
    }

    public boolean getBottomSensor() {
        return !elevationsLowerLimit.get();
    }

    public void calculateShootingSpeed() {
        shooterWheelEncoderData.calculateSpeed();
    }

    public double getShooterSpeed() {
        return shooterWheelEncoderData.getSpeed();
    }

    public void resetShooterEncoder() {
        shooterWheelEncoderData.reset();
    }

    public void setElevations(double speed) {
        diskElevator.set(-speed);
    }

    public void setPIDShooting(double goal) {
        pid.setGoal(goal);
    }

    public double getPIDShooting() {
        return pid.update(getShooterSpeed());
    }

    public void setShooterPIDValue(double p, double i, double d) {
        pid.setPIDValue(p, i, d);
    }

    public double getPIDPValue() {
        return pid.getPValue();
    }

    public double getPIDIValue() {
        return pid.getIValue();
    }

    public double getPIDDValue() {
        return pid.getDValue();
    }

    public void setSmartShooterSpeed(double value) {
        SmartDashboard.putNumber("SHOOTER SPEED", value);
    }

    public void setSmartPresetString(String text) {
        SmartDashboard.putString("SHOOTER Preset", text);
    }

    public void setSmartVoltageShooting(boolean value) {
        SmartDashboard.putBoolean("Voltage Shooting", value);
    }

    public void setSmartRPMShooting(boolean value) {
        SmartDashboard.putBoolean("RPM Shooting", value);
    }

    public void setSmartReadyToShoot(boolean value) {
        SmartDashboard.putBoolean("Ready to Shoot", value);
    }

    public double getLeftBendSensor() {
        return leftBendSensor.getAverageVoltage(leftBendSensorChannel);
    }

    public double getRightBendSensor() {
        return rightBendSensor.getAverageVoltage(rightBendSensorChannel);
    }

    // ********* ASCENDERS methods **********************
    public void setClimbClaws(boolean climbPosition) {
        climbClaws.set(climbPosition);
    }
    // ********* DRIVES methods ***************************
    //Needed for leftDrivesEncoderData.getSpeed()

    public void calculateDrivesSpeed() {
        leftDrivesEncoderData.calculateSpeed();
        rightDrivesEncoderData.calculateSpeed();
    }
    //Returns speed in Feet per Second

    public double getLeftSpeed() {
        if (MIKES_BOARD) {
            return board.getGyroAndWheels()[1];
        }
        return leftDrivesEncoderData.getSpeed();
    }
    //Returns speed in Feet per Second

    public double getRightSpeed() {
        if (MIKES_BOARD) {
            return board.getGyroAndWheels()[2];
        }
        return rightDrivesEncoderData.getSpeed();
    }
    //No idea what units this returns

    public double getLeftDist() {
        if (MIKES_BOARD) {
            return board.getDistances()[0];
        }
        return leftDrivesEncoderData.getDistance();
    }
    //See above comment

    public double getRightDist() {
        if (MIKES_BOARD) {
            return board.getDistances()[1];
        }
        return rightDrivesEncoderData.getDistance();
    }

    public double getTotalDist() {
        if (MIKES_BOARD) {
            return board.getDistances()[2];
        }
        return Math.sqrt(leftDrivesEncoderData.getDistance() * leftDrivesEncoderData.getDistance()
                + rightDrivesEncoderData.getDistance() * rightDrivesEncoderData.getDistance());
    }

    public void resetDrivesEncoders() {
        if (MIKES_BOARD) {
            board.resetEncoders();
        }
        rightDrivesEncoderData.reset();
        rightDrivesEncoder.reset();
        leftDrivesEncoderData.reset();
        leftDrivesEncoder.reset();
    }

    public void shift(boolean gear) {
        shift.set(gear);
    }

    public void setLeftDrives(double speed) {
        leftDrives1.set(speed * 1);
        leftDrives2.set(speed * -1);
    }

    public void setRightDrives(double speed) {
        rightDrives1.set(speed * -1);
        rightDrives2.set(speed * -1);//1
    }

//    public void resetGyro(){
//        gyro.reset();
//    }
//    public double getGyroAngle(){
//        return gyro.getAngle();
//    }
    public double getGyro() {
        if (MIKES_BOARD_GYRO) {
            return board.getGyroAndWheels()[0];
        }
        return 0;
    }

    public void resetGyro() {
        if (MIKES_BOARD_GYRO) {
            board.resetGyro();
        }
    }

    public void resetEncoders() {
        if (MIKES_BOARD) {
            board.resetEncoders();
        }
    }

    /**
     * Use Only with MikesBoard. 
     * @return The X Position of the robot if we are using MikesBoard. 0.0 If not 
     */
    public double getX() {
        if (MIKES_BOARD) {
            return board.getFieldLocation()[0];
        }
        return 0.0;
    }

    /**
     * Use Only with MikesBoard. 
     * @return The Y Position of the robot if we are using MikesBoard. 0.0 If not 
     */
    public double getY() {
        if (MIKES_BOARD) {
            return board.getFieldLocation()[1];
        }
        return 0.0;
    }
    // ********* Misc methods ***************************

    /**
     * This set the current mode to climbing or driving.
     * @param desiredMode - Climbing or Driving mode 
     */
    public void setClimbingMode(boolean desiredMode) {
        climbing = desiredMode;
    }

    /**
     * @returns if climbing mode is active or not 
     */
    public boolean getClimbingMode() {
        return climbing;
    }

    /**
     * Implemented Singleton getInstance().
     * @return the only instance of IO.
     */
    public static IO getInstance() {
        if (master == null) {
            master = new IO();
        }
        return master;
    }

    /**
     * Places I/O into a diagnostic state, where modules are ignored, and
     * inputs from those modules are taken directly from the controllers.
     */
    public void enableDiagnosticsMode() {
        InDiagnostics = true;
    }

    /**
     * Takes I/O out of Diagnostics mode. Input from all other modules will be
     * accepted.
     */
    public void disableDiagnosticsMode() {
        InDiagnostics = false;
    }

    public boolean getDiagnosticsMode() {
        return InDiagnostics;
    }

    /**************************************************************************/
    /*******************LIGHTS AND MORE LIGHTS*********************************/
    /**************************************************************************/
    public void rainbowStrip() {
        if (MIKES_BOARD_LED) {
            board.rainbowStrip();
            return;
        }
    }

    public void setGyroColor() {
        if (MIKES_BOARD_LED) {
            board.gyroLED();
            return;
        }
    }

    public void oneColorStrip(int blue, int red, int green) {
        if (MIKES_BOARD_LED) {
            board.oneColorStrip(blue, red, green);
            return;
        }
    }

    public void oneColorStripFlashing(int blue, int red, int green) {
        if (MIKES_BOARD_LED) {
            board.oneColorStripFlash(blue, red, green);
            return;
        }
    }

    public void setOneLED(int blue, int red, int green, int LEDnum) {
        if (MIKES_BOARD_LED) {
            board.setLED(blue, red, green, LEDnum);
            return;
        }
    }

    public void setOneLEDFlash(int blue, int red, int green, int LEDnum) {
        if (MIKES_BOARD_LED) {
            board.setLEDFlash(blue, red, green, LEDnum);
            return;
        }
    }

    public void setGroupOfLEDs(int blue, int red, int green, int numLEDs) {
        if (MIKES_BOARD_LED) {
            board.setGroup(blue, red, green, numLEDs);
            return;
        }
    }

    public void cylonLEDs() {
        if (MIKES_BOARD_LED) {
            board.cylon();
            return;
        }
    }

    /**Higher is slower*/
    public void setLEDSpeed(int speed) {
        if (MIKES_BOARD_LED) {
            board.setSpeed(speed);
            return;
        }
    }

    public void setLEDStrobeSpeed(int speed) {
        if (MIKES_BOARD_LED) {
            board.setStrobeSpeed(speed);
            return;
        }
    }

    //Manual Auto Switch
    public double getVoltage() {
        return autoSwitch.getVoltage(autoSwitchChannel);
    }

    public void setMikesBoard(boolean enabled) {
        mikesBoardSetting.set(enabled);
    }
    //********************************************************************
    //******************Input Output Hardware Constants******************* 
    //********************************************************************
    //********************************************************************
    //*****************Playstation 2 Controller Mapping*******************
    //********************************************************************
    public static final int LEFT_X_AXIS = 1;
    public static final int LEFT_Y_AXIS = 2;
    public static final int RIGHT_X_AXIS = 4;
    public static final int RIGHT_Y_AXIS = 3;
    public static final int DPAD_X_AXIS = 5;// right == 1, left == -1
    public static final int DPAD_Y_AXIS = 6;// down == 1, up == -1
    public static final int TRIANGLE = 1;
    public static final int CIRCLE = 2;
    public static final int CROSS = 3;
    public static final int SQUARE = 4;
    public static final int LTWO = 5;
    public static final int RTWO = 6;
    public static final int LONE = 7;
    public static final int RONE = 8;
    public static final int SELECT = 9;
    public static final int START = 10;
    public static final int L3 = 11;
    public static final int R3 = 12;
    //********************************************************************
    //********************AIRFLO Controller Mapping***********************
    //********************************************************************
    public static final int AIRFLO_CROSS = 1;
    public static final int AIRFLO_CIRCLE = 2;
    public static final int AIRFLO_SQUARE = 3;
    public static final int AIRFLO_TRIANGLE = 4;
    public static final int AIRFLO_LONE = 5;
    public static final int AIRFLO_LTWO = 6;
    public static final int AIRFLO_RONE = 7;
    public static final int AIRFLO_RTWO = 8;
    public static final int AIRFLO_SELECT = 9;
    public static final int AIRFLO_START = 10;
    public static final int AIRFLO_ANALOG = 11;
    public static final int AIRFLO_L3 = 12;
    public static final int AIRFLO_R3 = 13;
    public static final int AIRFLO_RIGHT_Y = 3;
    public static final int AIRFLO_RIGHT_X = 4;
    public static final int AIRFLO_LEFT_Y = 2;
    public static final int AIRFLO_LEFT_X = 1;
    public static final int AIRFLO_D_PAD_Y = 6;//UP - NEGATIVE
    public static final int AIRFLO_D_PAD_X = 5;//LEFT - NEGATIVE       
    //********************************************************************
    //*******************Attack 3 Controller Mapping**********************
    //********************************************************************
    public static final int ATTACK3_Y_AXIS = 2;
    public static final int ATTACK3_X_AXIS = 2;
    public static final int ATTACK3_Z_AXIS = 3;
    public static final int ATTACK3_TRIGGER = 1;
    public static final int ATTACK3_TOP_BUTTON = 2;
    //********************************************************************
    //**********************Driver Joystick Mapping***********************
    //********************************************************************
    public static final int driverJoystickRightPort = 1;
    public static final int driverJoystickLeftPort = 2;
    public static final int leftDrivesAxis = ATTACK3_Y_AXIS;
    public static final int rightDrivesAxis = ATTACK3_Y_AXIS;
    public static final int climbingClaws = ATTACK3_TRIGGER;//RIGHT
    public static final int stayInLowGear = ATTACK3_TRIGGER;//LEFT
    public static final int switchGears = ATTACK3_TOP_BUTTON;//LEFT
    public static final int driveStraight = ATTACK3_TOP_BUTTON;//RIGHT
    //********************************************************************
    //**********************Operator Joystick Mapping*********************
    //********************************************************************
    public static final int OperatorJoystickPort = 3;
    public static final int acquireButton = CIRCLE;//AIRFLO_CIRCLE;
    public static final int shootButton = START;//AIRFLO_START;
    public static final int fireButton = RTWO;//AIRFLO_RTWO;
    public static final int inbounderButton = TRIANGLE;//AIRFLO_TRIANGLE;
    public static final int rollerReverse = LONE;//AIRFLO_LTWO;
    public static final int offButton = CROSS;//AIRFLO_CROSS;
    public static final int RPMShootingModeButton = SELECT;//AIRFLO_SELECT;
    public static final int inDiagnosticsButton = 0;//AIRFLO_ANALOG;
    public static final int autoStart = 0;//AIRFLO_LONE;
    public static final int punchyAuto = LTWO;
    public static final int frontTopPreset = R3;
    public static final int backMiddlePreset = L3;
    //********************************************************************
    //******************************DigitalIO*****************************
    //********************************************************************
    //UPDATED: 1/30/13 4:00 p.m.
    public static final int DEFAULT_SLOT = 1;
    public static final int ALTERNATE_SLOT = 2;
    //****************/
    //** COMPRESSOR **/
    //****************/
    public static final int pressureSwitchChannel = 14;
    //****************/
    //***** PWMs *****/
    //****************/  
    public static final int PWMLeftDrives1 = 1;
    public static final int PWMLeftDrives2 = 2;
    public static final int PWMRightDrives1 = 3;
    public static final int PWMRightDrives2 = 4;
    public static final int PWMShooterWheel1 = 5;
    public static final int PWMShooterWheel2 = 6;
    public static final int PWMDiskElevator1 = 7;
    public static final int PWMAcquire = 8;
    public static final int PWMCameraServo = 9;
    //****************/
    //*** LED Strip **/
    //****************/
    public static final int clkPin = 11;
    public static final int dPin = 12;
    final static public short numLeds = 32;
    public static final int LEDStripClockChannel = 9;
    public static final int LEDStripDataChannel = 10;
    //****************/
    //****Pneumatic***/
    //****************/
    public static final int pneuShiftChan = 1;
    public static final int pneuTilt1 = 4;
    public static final int pneuTilt2 = 5;
    public static final int pneuKicker = 2;//2
    public static final int pneuFloorPickup = 3;
    public static final int pneuClimbClaws = 6;
    public static final int pneuPunchy = 7;//7
    public static final int pneuPunckyTwo = 8;
    //****************/
    //******Relay*****/
    //****************/
    public static final int compresssorRelayChannel = 1;//8
    //****************/
    //****Encoders****/
    //****************/
    public static final int leftEncoderChan1 = 1;
    public static final int leftEncoderChan2 = 2;
    public static final int rightEncoderChan1 = 3;
    public static final int rightEncoderChan2 = 4;
    public static final int shooterWheelChan = 5;
    //****************/
    //****Sensors*****/
    //****************/
    public static final int leftBendSensorChannel = 1;
    public static final int rightBendSensorChannel = 2;
    public static final int mikesBoardsettingChannel = 7;
    public static final int autoSwitchChannel = 4;
    public static final int lowerLimit = 9;
    public static final int upperLimit = 10;
}
