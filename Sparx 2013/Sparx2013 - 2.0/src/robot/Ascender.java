package robot;

public class Ascender extends SubSystem{
    private static Ascender ascenders;
    private IO master;
    private DriverJoysticks driver;
    private boolean toggle = true;
    private boolean moveHornsUp = false;
    private boolean moveHorns = false;
    private boolean buttonDefault = false;
    private boolean climberToggle = false;
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
           climberToggle = driver.getClimbing();
           
            if(climberToggle && getLocalMode() == SubSystem.TELEOP){
                moveHorns = true;
                buttonDefault = false;
            }
            
            if(!climberToggle){
                buttonDefault = true;
            }
            
                if(moveHorns && toggle && buttonDefault){//THIS IS A TOGGLE
                    moveHornsUp = true;
                    toggle = false;
                    moveHorns = false;
                }else if(moveHorns && !toggle && buttonDefault){
                    moveHornsUp = false;
                    toggle = true;
                    moveHorns = false;
                }
            
            master.setClimbClaws(moveHornsUp);
            
            if (master.getClimbingMode()){//Checks to make sure in climbing mode and not driving mode
            }
        }
    }
    
    public void autoSetHorns(boolean value){
        moveHornsUp = value;
    }
    
}
