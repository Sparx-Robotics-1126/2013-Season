/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.I2C;

/**
 * @author Alex
 */
public class MikesBoard extends SubSystem{
    private IO master;
    
    public static final boolean DISABLE_LED_STRIP = true;

    
    private static MikesBoard board;
    private static boolean alreadyRan = false;
    private static byte[] dataToSendWheels = {0x01};
    private static byte[] dataReceivedWheels = {0,0,0,0,0,0,0};
    private static byte[] dataToSendFieldPos = {0x02};
    private static byte[] dataReceivedFieldPos = {0,0,0,0,0,0,0};
    private static byte[] dataToSendDistances = {0x03};
    private static byte[] dataReceivedDistances = {0,0,0,0,0,0,0};
    private static byte[] tmpDataReceived = { 0,0,0,0,0,0,0 };
    private static byte[] trashBytes;
    private static byte[] dataLastSent = {0, 0, 0, 0, 0};
    private static long gyroCallTime = 0;
    private static long distCallTime = 0;
    private static long fieldCallTime = 0;
    private static long i2cErrors = 0;
    private static DigitalModule digitalSidecar = DigitalModule.getInstance(1); 
    private static I2C i2c = digitalSidecar.getI2C(4);
    
    private MikesBoard(){
        super("MikesBoard");
    }
    
    public void run(){
        
        i2c.setCompatabilityMode(true);
        while(true){
//            if(!alreadyRan && isEnabled()){
//                master.setMikesBoard(IO.MIKES_BOARD_ENABLED);
//                alreadyRan = true;
//            }else{
//                master.setMikesBoard(IO.MIKES_BOARD_DISABLED);
//                alreadyRan = false;
//            }       
            if (heartBeat())
            {
//              print("I am here failed-----------------------------");
              sleep(250);
            }
            else
            {
              System.out.println("i2c Errors " + i2cErrors);
              sleep(1000);
            }
        }
    }
    
    public static MikesBoard getInstance(){
        if (board == null)
            board = new MikesBoard();
        return board;
    }    
    
    public void recenterGyro(){
        if(i2c.write(0x11, 0))
            i2cErrors++;
    }
    public void resetGyro(byte lowOrderHeading, byte highOrderHeading){
        byte[] toSend = {0x10, lowOrderHeading, highOrderHeading};
        if(i2c.transaction(toSend, 3, trashBytes, 0))
            i2cErrors++;
    }
    public void resetGyro(){
        if(i2c.write(0x10, 0))
            i2cErrors++;
    }
    public void setFieldPos(byte lowOrderXPos, byte midOrderXPos, byte highOrderXPos, byte lowOrderYPos, byte midOrderYPos, byte highOrderYPos){
        byte[] toSend = {0x12, lowOrderXPos, midOrderXPos, highOrderXPos, lowOrderYPos, midOrderYPos, highOrderYPos};
        if(i2c.transaction(toSend, 7, trashBytes, 0))
            i2cErrors++;
    }
    public void setFieldPos(){
        if(i2c.write(0x12, 0))
            i2cErrors++;
    }
    public void resetEncoders(){
        if(i2c.write(0x13, 0))
            i2cErrors++;
    }
    public long getNumberOfErrors(){
        return i2cErrors;
    }
    public void resetNumberOfErrors(){
        i2cErrors = 0;
    }
    private void getHeadingAndWheels(){
        long curtime = System.currentTimeMillis();
        
        if (curtime - gyroCallTime > 15)
        {
          if(i2c.write(0x01, 0))
              i2cErrors++;

          if (i2c.transaction(dataToSendWheels, 0, tmpDataReceived, 7) == false)
          {
              if (tmpDataReceived[0] == 1)
              {
                dataReceivedWheels[0] = tmpDataReceived[0];
                dataReceivedWheels[1] = tmpDataReceived[1];
                dataReceivedWheels[2] = tmpDataReceived[2];
                dataReceivedWheels[3] = tmpDataReceived[3];
                dataReceivedWheels[4] = tmpDataReceived[4];
                dataReceivedWheels[5] = tmpDataReceived[5];
                dataReceivedWheels[6] = tmpDataReceived[6];
                gyroCallTime = curtime;
              }
          }
          else
              i2cErrors++;
          
//          i2c.transaction(dataToSendWheels, 0, dataReceivedWheels, 7);
       }
    }
    private void getFieldPosition() {
        long curtime = System.currentTimeMillis();
        
        if (curtime - fieldCallTime > 10)
        {
          if(i2c.write(0x02, 0))
              i2cErrors++;
          else
          {
            if (i2c.transaction(dataToSendFieldPos, 0, tmpDataReceived, 7) == false)
            {
                if (tmpDataReceived[0] == 2)
                {
                  dataReceivedFieldPos[0] = tmpDataReceived[0];
                  dataReceivedFieldPos[1] = tmpDataReceived[1];
                  dataReceivedFieldPos[2] = tmpDataReceived[2];
                  dataReceivedFieldPos[3] = tmpDataReceived[3];
                  dataReceivedFieldPos[4] = tmpDataReceived[4];
                  dataReceivedFieldPos[5] = tmpDataReceived[5];
                  dataReceivedFieldPos[6] = tmpDataReceived[6];
                  fieldCallTime = curtime;
                }
            }
            else
                i2cErrors++;
//            i2c.transaction(dataToSendFieldPos, 0, dataReceivedFieldPos, 7);
//            fieldCallTime = curtime;
          }
        }
    }
    private void getDistancesFromBoard(){
        long curtime = System.currentTimeMillis();
        
        if (curtime - distCallTime > 10)
        {
          if(i2c.write(0x03, 0))
              i2cErrors++;
          else
          {
            if (i2c.transaction(dataToSendDistances, 0, tmpDataReceived, 7) == false)
            {
                if (tmpDataReceived[0] == 3)
                {
                  dataReceivedDistances[0] = tmpDataReceived[0];
                  dataReceivedDistances[1] = tmpDataReceived[1];
                  dataReceivedDistances[2] = tmpDataReceived[2];
                  dataReceivedDistances[3] = tmpDataReceived[3];
                  dataReceivedDistances[4] = tmpDataReceived[4];
                  dataReceivedDistances[5] = tmpDataReceived[5];
                  dataReceivedDistances[6] = tmpDataReceived[6];
                  distCallTime = curtime;
                }
            }
            else
                i2cErrors++;
//            i2c.transaction(dataToSendDistances, 0, dataReceivedDistances, 7);
//            distCallTime = curtime;
          }
        }
    }
    /****************Old unneeded code***********************/
    /*
    public int getLeftEncoderSpeed(){
        getHeadingAndWheels();
        return (short)dataReceivedWheels[4]<<8 + dataReceivedWheels[3];
    }
    public int getRightEncoderSpeed(){
        getHeadingAndWheels();
        return ((int)dataReceivedWheels[6]*256) | dataReceivedWheels[5];
    }
    public int getGyro(){
        getHeadingAndWheels();
        int heading;
        heading = ((char) dataReceivedWheels[2] & 0x00ff);
        heading *= 256;
        int heading1;
        heading1 = ((char) dataReceivedWheels[1] &0x00ff);
        return heading + heading1;
    }
    public int getX(){
        getFieldPosition();
        int x = dataReceivedFieldPos[3]<<16 + dataReceivedFieldPos[2]<<8 + dataReceivedFieldPos[1];
        if ((0x00800000 & x) != 0){
            x |= 0xFF000000;
        }
        return x;
    }
    public int getY(){
        getFieldPosition();
        int y = dataReceivedFieldPos[6]<<16 + dataReceivedFieldPos[5]<<8 + dataReceivedFieldPos[1];
        if ((0x00800000 & y) != 0){
            y |= 0xFF000000;
        }
        return y;
    }
    */
    /**
     * @return [0] - Gyro in degrees
     * @return [1] - left encoder speed
     * @return [2] - right encoder speed
     */
    public double[] getGyroAndWheels(){
        getHeadingAndWheels();
        int heading;
        heading = ((char) dataReceivedWheels[2] & 0x00ff);
        heading *= 256;
        int heading1;
        heading1 = ((char) dataReceivedWheels[1] &0x00ff);
        double headingToReturn =  ((heading + heading1) / 65536.0) * 360;
//        double rightEncoderSpeed = ((int)(dataReceivedWheels[6]*256) | (dataReceivedWheels[5] & 255)) * 0.004197;
//        double leftEncoderSpeed = ((int)(dataReceivedWheels[3]*256) | (dataReceivedWheels[2] & 255)) * 0.004197;
        double rightEncoderSpeed = ((int)(dataReceivedWheels[6]*256) | (dataReceivedWheels[5] & 255)) * 0.05364;
        double leftEncoderSpeed = ((int)(dataReceivedWheels[4]*256) | (dataReceivedWheels[3] & 255)) * 0.05364;
        double[] speeds = {headingToReturn, leftEncoderSpeed, rightEncoderSpeed};
        return speeds;
    }
    /**
     * @return [0] - X Position
     * @return [1] - Y Position
     */
    public double[] getFieldLocation(){
        getFieldPosition();
        double xPos = ((int)(dataReceivedFieldPos[3]*65536) | (dataReceivedFieldPos[2] & 255) * 256 | (dataReceivedFieldPos[1]) & 255);
        double yPos = ((int)(dataReceivedFieldPos[6]*65536) | (dataReceivedFieldPos[5] & 255) * 256 | (dataReceivedFieldPos[4]) & 255);
        xPos *= IO.DIST_PER_TICK;
        yPos *= IO.DIST_PER_TICK;
        double totalDist = Math.sqrt(xPos * xPos + yPos * yPos);
        double[] pos = {xPos, yPos, totalDist};
        return pos;
    }
    public double[] getDistances(){
        getDistancesFromBoard();
        double leftDist = ((int)(dataReceivedDistances[3]*65536) | (dataReceivedDistances[2] & 255) * 256 | (dataReceivedDistances[1]) & 255);
        double rightDist = ((int)(dataReceivedDistances[6]*65536) | (dataReceivedDistances[5] & 255) * 256 | (dataReceivedDistances[4]) & 255);
        leftDist *= IO.DIST_PER_TICK;
        rightDist *= IO.DIST_PER_TICK;
        double[] dist = {leftDist, rightDist, (leftDist + rightDist)/2};
        return dist;   
    }
    public void rainbowStrip(){
        if (DISABLE_LED_STRIP)
            return;
        if (dataLastSent[0] == 0x60)
            return;

        if(i2c.write(0x60,0))
            i2cErrors++;
        else
            dataLastSent[0] = (byte) 0x60; 
    }
    
    public void oneColorStrip(int blue, int red, int green){
        if (DISABLE_LED_STRIP)
            return;
        if ((dataLastSent[0] == 0x61) && (dataLastSent[1] == (byte)blue) &&
                (dataLastSent[2] == (byte) red) && (dataLastSent[3] == (byte) green))
        { return;}
        dataLastSent[0] = (byte) 0x61;
        dataLastSent[1] = (byte) blue;
        dataLastSent[2] = (byte) red;
        dataLastSent[3] = (byte) green;
        if(i2c.transaction(dataLastSent, 4, trashBytes, 0))
        {
            i2cErrors++;
            dataLastSent[0] = (byte) 0xFF;
        }
    }
    
    public void oneColorStripFlash(int blue, int red, int green){
        if (DISABLE_LED_STRIP)
            return;
        if ((dataLastSent[0] == 0x62) && (dataLastSent[1] == (byte)blue) &&
                (dataLastSent[2] == (byte) red) && (dataLastSent[3] == (byte) green))
        { return;}
        dataLastSent[0] = (byte) 0x62;
        dataLastSent[1] = (byte) blue;
        dataLastSent[2] = (byte) red;
        dataLastSent[3] = (byte) green;
        if(i2c.transaction(dataLastSent, 4, trashBytes, 0))
        {
            i2cErrors++;
            dataLastSent[0] = (byte) 0xFF;
        }
    }
    
    public void setLED(int blue, int red, int green, int LEDnum){
        if (DISABLE_LED_STRIP)
            return;
        if ((dataLastSent[0] == 0x63) && (dataLastSent[1] == (byte)blue) &&
                (dataLastSent[2] == (byte) red) && (dataLastSent[3] == (byte) green) &&
                (dataLastSent[4] == (byte) LEDnum))
        { return;}
        dataLastSent[0] = (byte) 0x63;
        dataLastSent[1] = (byte) blue;
        dataLastSent[2] = (byte) red;
        dataLastSent[3] = (byte) green;
        dataLastSent[4] = (byte) LEDnum;
        if(i2c.transaction(dataLastSent, 5, trashBytes, 0))
        {
            i2cErrors++;
            dataLastSent[0] = (byte) 0xFF;
        }
    }
    
    public void setLEDFlash(int blue, int red, int green, int LEDnum){
        if (DISABLE_LED_STRIP)
            return;
       if ((dataLastSent[0] == 0x64) && (dataLastSent[1] == (byte)blue) &&
                (dataLastSent[2] == (byte) red) && (dataLastSent[3] == (byte) green) &&
                (dataLastSent[4] == (byte) LEDnum))
        { return;}
        dataLastSent[0] = (byte) 0x64;
        dataLastSent[1] = (byte) blue;
        dataLastSent[2] = (byte) red;
        dataLastSent[3] = (byte) green;
        dataLastSent[4] = (byte) LEDnum;
        if(i2c.transaction(dataLastSent, 5, trashBytes, 0))
        {
            i2cErrors++;
            dataLastSent[0] = (byte) 0xFF;
        }
    }
    public void cylon(){
        if (DISABLE_LED_STRIP)
            return;
        if (dataLastSent[0] == 0x65)
            return;
        dataLastSent[0] = (byte) 0x65;
        if(i2c.write(0x65, 0))
        {
            i2cErrors++;
            dataLastSent[0] = (byte) 0xFF;
        }
    }
    public void gyroLED(){
        if (DISABLE_LED_STRIP)
            return;
        if (dataLastSent[0] == 0x67)
            return;
        dataLastSent[0] = (byte) 0x67;
        if(i2c.write(0x67, 0))
        {
            i2cErrors++;
            dataLastSent[0] = (byte) 0xFF;
        }
    }

    public void setGroup(int blue, int red, int green, int numOfLEDs){
        if (DISABLE_LED_STRIP)
            return;
        if ((dataLastSent[0] == 0x66) && (dataLastSent[1] == (byte) blue) &&
                (dataLastSent[2] == (byte) red) && (dataLastSent[3] == (byte) green) &&
                (dataLastSent[4] == (byte) numOfLEDs))
        { return;}
        dataLastSent[0] = (byte) 0x66;
        dataLastSent[1] = (byte) blue;
        dataLastSent[2] = (byte) red;
        dataLastSent[3] = (byte) green;
        dataLastSent[4] = (byte) numOfLEDs;
        if(i2c.transaction(dataLastSent, 5, trashBytes, 0))
        {
            i2cErrors++;
            dataLastSent[0] = (byte) 0xFF;
        }
    }
    /**
     * @param Speed - speed of the update. The higher the number the slower
     */
    public void setSpeed(int speed){
        if (DISABLE_LED_STRIP)
            return;
        if(i2c.write(0x6F, (byte) speed))
            i2cErrors++;
    }
    
    public void setStrobeSpeed(int speed){
        if (DISABLE_LED_STRIP)
            return;
        if(i2c.write(0x6E, speed))
            i2cErrors++;
    }
    
    public boolean heartBeat(){
        dataLastSent[0] = (byte) 0x20;
        
        if(i2c.write(0x20, 0))
        {
            i2cErrors++;
            return false;
        }
        return true;
    }
}
