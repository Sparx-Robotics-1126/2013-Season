package robot;

public class Ascender extends SubSystem{
    private static Ascender ascenders;
    private IO master;
    private DriverJoysticks driver;
    private boolean toggle = true;
    private boolean currentPosition = false;
    private boolean moveHorns = false;
    //Declare here
    
    private Ascender(){
        super("ascender");
        master = IO.getInstance();
        driver = DriverJoysticks.getInstance();
    //Define here
    }
    
    public static Ascender getInstance(){
         if (ascenders == null){
             ascenders = new Ascender();
         }
         return ascenders;
     }
    
    public void run(){
        while (true){
           sleep(20);
            //Joysticks
           
           
            if(driver.getClimbing() && getLocalMode() == SubSystem.TELEOP){
                moveHorns = true;
            }
            
            if(moveHorns && toggle){//THIS IS A TOGGLE
                currentPosition = true;
                toggle = false;
                moveHorns = false;
                master.setClimbClaws(currentPosition);
                sleep(1000);
            }else if(moveHorns && !toggle){
                currentPosition = false;
                toggle = true;
                moveHorns = false;
                master.setClimbClaws(currentPosition);
                sleep(1000);
            }
            
            master.setClimbClaws(currentPosition);
            
            if (master.getClimbingMode()){//Checks to make sure in climbing mode and not driving mode
            }
        }
    }
    
    public void autoSetHorns(boolean value){
        currentPosition = value;
    }
   ; 
}
