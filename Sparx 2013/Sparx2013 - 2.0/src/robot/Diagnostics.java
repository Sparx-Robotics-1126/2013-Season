/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Connor
 */
public class Diagnostics extends SubSystem{
    public static Diagnostics diagnostics;
    private static Joystick joystick;
    private static IO master;
    private static boolean shifting = false;
    private static boolean tilt1 = false;
    private static boolean tilt2 = false;
    private static boolean kicker = false;
    private static boolean floorPickup = false;
    private static boolean climbingClaws = false;
    private static boolean punchy = false;
    private static boolean voltageShootingEnabled = false;
    private static boolean PIDShooterEnabled = false;
    private static double pGain = 0.0;
    private static double iGain = 0.0;
    private static double dGain = 0.0;
    private static boolean inFirstMode = true;
    private static boolean printing = true;
    private static boolean shootingSequence = false;
    private static boolean justPressed = false;
    private static boolean setValues = true;
    
    public Diagnostics(){
        super("Diagnostics");
        joystick = new Joystick(4);
        master = IO.getInstance();
    }
    
    public static Diagnostics getInstance(){
         if (diagnostics == null){
             diagnostics = new Diagnostics();
         }
         return diagnostics;
    }
        
    public void run(){
        pGain = master.getPIDPValue();
        iGain = master.getPIDIValue();
        dGain = master.getPIDDValue();
        while(true){
           sleep(20);
           printToLCD(("Mode = " + master.getDiagnosticsMode()), true);
            if(master.getDiagnosticsMode()){  
                
                //simple toggle
                if((joystick.getRawButton(1) || joystick.getRawButton(2) || joystick.getRawButton(3) ||
                        joystick.getRawButton(4) || joystick.getRawButton(5) || joystick.getRawButton(6) ||
                        joystick.getRawButton(7) || joystick.getRawButton(8) || joystick.getRawButton(9) ||
                        joystick.getRawButton(10) || joystick.getRawButton(11)) && !justPressed){
                    justPressed = true;
                    setValues = true;
                }else if( !(joystick.getRawButton(1) || joystick.getRawButton(2) || joystick.getRawButton(3) ||
                            joystick.getRawButton(4) || joystick.getRawButton(5) || joystick.getRawButton(6) ||
                            joystick.getRawButton(7) || joystick.getRawButton(8) || joystick.getRawButton(9) ||
                            joystick.getRawButton(10) || joystick.getRawButton(11))){
                    justPressed = false;    
                }
                
                if(setValues){
                    printToLCD(("ShotMode: " + inFirstMode));
                    if(joystick.getRawButton(4)){
                        inFirstMode = !inFirstMode;
                        print("Shooting Mode: " + inFirstMode);
                    }
                    if(inFirstMode){//shooter mode
                        if(joystick.getRawButton(1)){
                            shootingSequence = !shootingSequence;
                        }else if (joystick.getRawButton(2)){
                            tilt1 = !tilt1;
                        }else if(joystick.getRawButton(3)){
                            tilt2 = !tilt2;
                        //4 is for switching between modes
                        }else if(joystick.getRawButton(5)){
                            floorPickup = !floorPickup;
                        }else if(joystick.getRawButton(6)){
                            pGain = pGain - 0.00001;
                        }else if(joystick.getRawButton(7)){
                            pGain = pGain + 0.00001;
                        }else if(joystick.getRawButton(8)){
                            PIDShooterEnabled = false;
                            voltageShootingEnabled = false;
                        }else if(joystick.getRawButton(9)){
                            PIDShooterEnabled = true;
                            voltageShootingEnabled = false;
                        }else if(joystick.getRawButton(10)){
                            iGain = iGain + 0.00001;
                        }else if(joystick.getRawButton(11)){
                            iGain = iGain - 0.00001; 
                        }
                    }else{//Values Mode
                        if(joystick.getRawButton(1)){
                            kicker = !kicker;
                        }else if (joystick.getRawButton(2)){
                            climbingClaws = !climbingClaws;
                        }else if(joystick.getRawButton(3)){
                            printing = !printing;
                        //4 is for switching between modes
                        }else if(joystick.getRawButton(5)){
                            punchy = !punchy;
                        }else if(joystick.getRawButton(6)){
                            master.calculateDrivesSpeed();
                            print("LeftDrive: " + master.getLeftSpeed() + " RightDrive: " + master.getRightSpeed()); 
                        }else if(joystick.getRawButton(7)){
                            master.calculateShootingSpeed();
                            print("Shooter Speed: " + master.getShooterSpeed());
                        }else if(joystick.getRawButton(8)){
                            PIDShooterEnabled = false;
                            voltageShootingEnabled = false;
                        }else if(joystick.getRawButton(9)){
                            voltageShootingEnabled = false;
                            PIDShooterEnabled = true;
                        }else if(joystick.getRawButton(10)){
                        
                        }else if(joystick.getRawButton(11)){
                        
                        }
                    }
                    setValues = false;//have been set once
                }
                

                
                //Set values
                master.setKicker(kicker);
                master.shift(shifting);
                master.setTowerTilt1(tilt1);
                master.setTowerTilt2(tilt2);
                master.setPunchyTop(punchy);
                master.setPunchyBottom(punchy);
                master.setPickUpMode(floorPickup, true);
                master.setClimbClaws(climbingClaws);
                setPrinting(printing);
                
                if(shootingSequence){   
                    master.setPunchyTop(false);
                    sleep(75);
                    master.setPunchyTop(true);
                    sleep(75);
                    master.setPunchyTop(false);
                    sleep(75);
                    master.setPunchyTop(true);
//                    sleep(75);
//                    master.setPunchyTop(false);
                    sleep(100);
                    master.setKicker(true);
                    sleep(500);
                    master.setKicker(false);
                    shootingSequence = false;
                }

                if(voltageShootingEnabled){
                    master.calculateShootingSpeed();
                    print("Encoder: " + master.getShooterSpeed());
                    master.setShooter(getZeroToOne(joystick.getZ()));
                }
                
                if(PIDShooterEnabled){    
                    master.setShooterPIDValue(pGain, iGain, dGain);
                    master.calculateShootingSpeed();
                    master.setPIDShooting(getZeroToOne(joystick.getZ()) * 12000);
                    double motorValue = master.getPIDShooting();
                    print("Encoder: " + master.getShooterSpeed() + "  JoystickValue: " + getZeroToOne(joystick.getZ()) * 12000 + "  Motor Value " + motorValue + "   p: " + pGain + " i: " + iGain + " d: " + dGain);
                    master.setShooter(motorValue);
                }
                
                if(!voltageShootingEnabled && !PIDShooterEnabled){
                    master.setShooter(0);
                }
            
                if(getAxis(joystick.getY()) != 0){
                    print("Lower Sensor: " + master.getBottomSensor() + "   Upper Sensor: " + master.getPlateLocation());
                }
                
                master.setElevations(getAxis(joystick.getY()));
                print(""+ getAxis(joystick.getY()));
                master.setAcquire(getAxis(joystick.getX()));
            } 
        }
    }
    
    public double getAxis(double axisValue){
        if(Math.abs(axisValue) < 0.1){
            return 0.0;
        }else{
            return axisValue;
        }
    }
    
    public double getZeroToOne(double axisValue){
        return ((axisValue + 1) / 2);
    }
}
