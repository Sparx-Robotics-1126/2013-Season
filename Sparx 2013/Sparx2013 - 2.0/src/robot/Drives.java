package robot;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Pixel Fyre
 */
public class Drives extends SubSystem {

    private static boolean stayInLow = false;
    private static boolean manualShift = false;
    private static final boolean SECOND_GEAR = true;
    private static final boolean FIRST_GEAR = false;
    private static final int UP_SHIFT_THRESHOLD = 55; //50
    private static final int DOWN_SHIFT_THRESHOLD = 25; //25
    private static DriverJoysticks driverjoysticks;
    private double shiftingSpeed = 0.15;
    private static Drives drives;
    private static IO master;
    private boolean notInHighGear = true;
    private boolean gearToggle = false;//HAS NOT BEEN PRESSED
    private boolean currentGear = FIRST_GEAR;
    private double leftJoySpeed;
    private double rightJoySpeed;
    private double dist;
    //Auto
    private boolean autoTurn = false;
    private boolean autoDrive = false;
    private double autoDegreesWanted = 0;
    private double autoDistanceWanted = 0;//0
    private boolean autoFoward = false;
    private double setHeading = 0.0;
    private boolean autoTurningRight = false;
    private boolean autoDone = true;
    private static final double INCHESPERDEGREE = .083;//.588 ;
    //.367043;
    private static final double AUTOTURNSPEED = .5;
    private int i = 0;
    private static final int TURNING_ERROR = 180;
    private boolean autoTurningLeft;
    private boolean headingToggle = false;
    public int counter = 0;
    public boolean calledLights = false;
    private static boolean driveStraight = false; 
    private static double rightOffset = 0;
    private static double leftOffset  = 0;
    private static boolean firstGyroReset = false;

    private Drives() {
        super("drives");
        master = IO.getInstance();
        driverjoysticks = DriverJoysticks.getInstance();
        master.resetDrivesEncoders();
    }

    /**
     * Lowers our speed to allow the gears to mesh properly
     */
    public void shift(boolean gear) {
        if (!stayInLow) {
            if (master.getLeftSpeed() <= 0 && master.getRightSpeed() <= 0) {
                setMotors(-shiftingSpeed, -shiftingSpeed);
            } else if (master.getLeftSpeed() >= 0 && master.getRightSpeed() >= 0) {
                setMotors(shiftingSpeed, shiftingSpeed);
            }
        }

        //Sleep the thread to allow time for our speed to reduce to what is set above
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }
        currentGear = gear;
        master.shift(gear);

//        if (gear == SECOND_GEAR)
//           notInHighGear = false;
//        else
//           notInHighGear = true;
        
        //Wait for the physical shifting to occur
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }

    public void run() {
        //Shifting will occur regardless of teleop/auto, but driver control happens only within teleop.+

        while(true) {
            sleep(15);
//            print("Gyro: " + master.getGyro());
//            
//            print("RIGHT: " + master.getRightSpeed() + " LEFT: " + master.getLeftSpeed() + " Adverage " + ((master.getRightSpeed() + master.getLeftSpeed())/2));
            leftJoySpeed = driverjoysticks.getLeftJoyY() * -1;
            rightJoySpeed = driverjoysticks.getRightJoyY() * -1;
            //Driving mode is false, climbing mode is true.
//            if(master.getClimbingMode() == master.DRIVING_MODE){
            master.calculateDrivesSpeed();

            if (driverjoysticks.getStayInLowGear()) {//MOMENTARY
                shift(FIRST_GEAR);
                stayInLow = true;

            } else if (driverjoysticks.getSwitchGears() && !gearToggle) {
                shift(!currentGear);
                gearToggle = true;
                manualShift = true;
            } else if (!manualShift && !stayInLow) {
                getShift();
            }

            //SWITCHGEAR - ONE WAY SWITCH
            if (!driverjoysticks.getSwitchGears() && gearToggle) {
                gearToggle = false;
            }

            //STAYINLOWGEAR
            if (!driverjoysticks.getStayInLowGear()) {//MOMENTARY
                stayInLow = false;
            }
            
            if(driverjoysticks.getDriveStraight()){
                driveStraight = true;
            }else{
                driveStraight = false;
                leftOffset = 0;
                rightOffset = 0;
                firstGyroReset = false;
            }

            if (getLocalMode() == SubSystem.TELEOP) {
                if(!driveStraight){
                    setMotors(leftJoySpeed, rightJoySpeed); 
                }else{
                    getGyroOffset();
                    setMotors(rightJoySpeed + leftOffset, rightJoySpeed + rightOffset);   
                }

            }else if (getLocalMode() == SubSystem.AUTO) {
                if (autoTurn) {
                    autoTurn();
                }

                if (autoDrive) {
                    autoDrive();
                }
            }
            // printData();

        }
        //}
    }

    //Prints the left and right speed when called. It prints to the first line of the drivers station. 
    private void printData() {
        drives.printToLCD("L=" + (double) ((long) (master.getLeftSpeed() * 10)) / 10 + " R=" + (double) ((long) (master.getRightSpeed() * 10)) / 10);

    }

    public static Drives getInstance() {
        if (drives == null) {
            drives = new Drives();
        }
        return drives;
    }

    /**
     * For the shifting code, we take the average absolute speed of the motors
     * in feet per second, and compare that to the upshift/downshift threshold.
     * The upshift threshold must be larger than the downshift threshold, or it
     * will toggle back and forth between first and second gear. I've updated
     * this, and currently don't know whether it will work. I've changed the
     * gear value of true from first to second, and switched other values in
     * order to make it make sense.
     */
    public void getShift() {
        if ((Math.abs(master.getLeftSpeed()) >= UP_SHIFT_THRESHOLD) && (Math.abs(master.getRightSpeed()) >= UP_SHIFT_THRESHOLD) && notInHighGear) {
            notInHighGear = false;
            shift(SECOND_GEAR);
        } else if ((Math.abs(master.getLeftSpeed()) <= DOWN_SHIFT_THRESHOLD) && (Math.abs(master.getRightSpeed()) <= DOWN_SHIFT_THRESHOLD) && !notInHighGear) {
            notInHighGear = true;
            shift(FIRST_GEAR);
        }
    }

    public void setMotors(double leftDrives, double rightDrives) {
        master.setLeftDrives(leftDrives);
        master.setRightDrives(rightDrives);
    }

    //AUTONOMOUS
    public void autoDimeTurn() {
        double degrees = 0;
        if (IO.MIKES_BOARD == true) {
            degrees = master.getGyro();
            double degreesToGoTo = autoDegreesWanted + degrees;

            if (autoTurningLeft == true) {
                setMotors(-0.4, 0.4);
            }

            if (autoTurningRight == true) {
                setMotors(0.4, -0.4);
            }

            if (degreesToGoTo >= master.getGyro()) {
                
                setMotors(0, 0);
                autoTurn = false;
                autoTurningRight = false;
                autoTurningLeft = false;
            }


        } else {

            master.calculateDrivesSpeed();

            if (autoTurningLeft == true) {
                setMotors(-0.4, 0.4);
                degrees = master.getLeftDist() * INCHESPERDEGREE;
            }

            if (autoTurningRight == true) {
                setMotors(0.4, -0.4);
                degrees = master.getRightDist() * INCHESPERDEGREE;
            }

            if (degrees >= autoDegreesWanted) {
                setMotors(0, 0);
                autoTurn = false;
                autoTurningRight = false;
                autoTurningLeft = false;
            }

        }

    }

    public void autoTurn() {
        double degrees = 0;
        counter++;
        if (IO.MIKES_BOARD_GYRO == true) {
            double degreesToGoTo = 180 - autoDegreesWanted;
            if (autoTurningRight==true)degreesToGoTo+=TURNING_ERROR;
            print("Auto: " + (degreesToGoTo) + " GYRO: " + (master.getGyro() + TURNING_ERROR));
            if (autoTurningLeft == true) {
                setMotors(-1, 1);
            }else if (autoTurningRight == true) {
                setMotors(1, -1);
            }

            if (degreesToGoTo - 10 <= (master.getGyro() + TURNING_ERROR) % 360 && autoTurningRight) {
                print("Done Turning right");
                setMotors(0, 0);
                autoTurn = false;
                autoTurningRight = false;
                autoTurningLeft = false;
            }else if(degreesToGoTo + 10 >= (master.getGyro() + TURNING_ERROR) % 360 && autoTurningLeft) {
                print("Done Turning left");
                setMotors(0, 0);
                autoTurn = false;
                autoTurningRight = false;
                autoTurningLeft = false;
            }

        } else {

            master.calculateDrivesSpeed();

            if (autoTurningLeft == true) {
                setMotors(-1, 1);
                degrees = master.getLeftDist() * INCHESPERDEGREE;
            }

            if (autoTurningRight == true) {
                setMotors(1, -1);
                degrees = master.getRightDist() * INCHESPERDEGREE;
            }

            if (degrees >= autoDegreesWanted) {
                setMotors(0, 0);
                autoTurn = false;
                autoTurningRight = false;
                autoTurningLeft = false;
            }

        }
    }

    public void autoDrive() {
        master.calculateDrivesSpeed();
        if (headingToggle == false) {
            setHeading = master.getGyro();
            headingToggle = true;
        }

        if (autoDistanceWanted > 0) {
//            if (Math.abs(setHeading - master.getGyro()) > master.getGyro()) {
//                setMotors(1, .8);
//                dist = Math.abs(master.getRightDist() + master.getLeftDist()) / 2;
//            } else if (setHeading - master.getGyro() <= master.getGyro()) {
//                setMotors(.8, 1);
//                dist = Math.abs(master.getRightDist() + master.getLeftDist()) / 2;
//            } else {
                setMotors(0.75, 0.75);             
//            }
        } else if (autoDistanceWanted < 0) {
//            if (Math.abs(setHeading - master.getGyro()) > master.getGyro()) {
//                setMotors(-1, -.8);
//                dist = Math.abs(master.getRightDist() + master.getLeftDist()) / 2;
//            } else if (setHeading - master.getGyro() <= master.getGyro()) {
//                setMotors(-.8, -1);
//                dist = Math.abs(master.getRightDist() + master.getLeftDist()) / 2;
//            } else {
                setMotors(-0.75, -0.75);
            //}
        }
        
        dist = Math.abs((master.getRightDist() + master.getLeftDist()) / 2);

        
//        print("AUTODISTANCE: " + autoDistanceWanted + " < " + " ENCODER: " + dist);
        if (Math.abs(dist) >= Math.abs(autoDistanceWanted)) {
            setMotors(0, 0);
            autoDrive = false;
            headingToggle = false;
        }
    }

    public void setAutoTurn(int degrees, boolean direction) {
        autoDegreesWanted = degrees;
        autoTurningRight = direction;
        autoTurningLeft = !direction;
        autoTurn = true;
        master.resetDrivesEncoders();
        master.resetGyro();
    }

    public void setDriving(int distance, boolean direction, double speed) {
        autoDistanceWanted = distance;
        autoFoward = direction;// false = backward
        autoDrive = true;
        master.resetDrivesEncoders();
    }

    public boolean autoComplete() {
        return ((!autoDrive) && (!autoTurn));
    }
    
    public void getGyroOffset(){
        if(firstGyroReset){
            master.resetGyro();
            firstGyroReset = false;
        }
        if(master.getGyro() > 2 && master.getGyro() < 90){
            rightOffset = 0.1;
            leftOffset = -0.1;
        }else if(master.getGyro() < 358 && master.getGyro() > 270){
            rightOffset = -0.1;
            leftOffset = 0.1;                 
        }else{
            rightOffset = 0;
            leftOffset = 0;
        }
    }
}
