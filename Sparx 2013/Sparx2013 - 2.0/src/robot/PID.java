package robot;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Logic of a Proportional Integral Derivative loop. Must be constructed first,
 * then it must receive continual updates in order to receive accurate output
 * values.
 * @author Solis Knight
 */
public class PID {
    private double error, integral, derivative, lastInput; //P, I, and D
    private double pGain, iGain, dGain; //Scaling factors
    private double iMax; // integral limit
    private double lowerLimit, upperLimit; //output limits
    private boolean ignoreLimits_FLAG, updated_FLAG, ignoreD_FLAG, reverse_FLAG, brake_FLAG; //Flags
    private double goal; //goal
    private double output; //output value
    private long lasttime; //last time in ms when pid loop ran

    /**
     * Limitless Constructor - initializes PID
     * @param pScale - the modifier for the Proportional calculation
     * @param iScale - the modifier for the integral calculation
     * @param dScale - the modifier for the derivative calculation, give the 
     * dScale as 0 if you wish to ignore the derivative
     * @param iMax - the maximum absolute value the integral should rise to.
     * @param fastBrake - whether or not to set output to 0 when goal is 0.
     * @param reversed - whether or not the output should be inverted: 
     * true if invert, false if not
     * **NOTE** the modifiers are all multiplicative. Use fractions for divisors.
     */
    public PID(double pScale, double iScale, double iMax, double dScale, 
                                        boolean fastBrake, boolean reversed){
        error = 0;
        integral = 0;
        derivative = 0;
        pGain = pScale;
        iGain = iScale;
        this.iMax = iMax;
        if (dScale == 0)
            ignoreD_FLAG = true;
        else {
            ignoreD_FLAG = false;
            dGain = dScale;
            lastInput = 0;
        }
        ignoreLimits_FLAG = true;
        brake_FLAG = fastBrake;
        reverse_FLAG = reversed;
        lasttime = System.currentTimeMillis();
    }
    
    /**
     * Limited Constructor
     * @param pScale - the modifier for the Proportional calculation
     * @param iScale - the modifier for the integral calculation
     * @param iMax - the maximum absolute value the integral should rise to.
     * @param dScale - the modifier for the derivative calculation, give the 
     * dScale as 0 if you wish to ignore the derivative
     * @param upperLimit - the upper limit of the output.
     * @param lowerLimit - the lower limit of the output.
     * @param fastBrake - whether or not to set output to 0 when goal is 0.
     * @param reversed - whether or not the output should be inverted: 
     * true if invert, false if not
     * **NOTE** the modifiers are all multiplicative. Use fractions for divisors.
     */
    public PID(double pScale, double iScale, double iMax, double dScale, 
                                   double upperLimit, double lowerLimit, 
                                        boolean fastBrake, boolean reversed){
        error = 0;
        integral = 0;
        derivative = 0;
        pGain = pScale;
        iGain = iScale;
        this.iMax = iMax;
        if (dScale == 0)
            ignoreD_FLAG = true;
        else {
            ignoreD_FLAG = false;
            dGain = dScale;
            lastInput = 0;
        }
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
        ignoreLimits_FLAG = false;
        brake_FLAG = fastBrake;
        reverse_FLAG = reversed;
        lasttime = System.currentTimeMillis();
    }
    
    /**
     * Runs through update calculations. Drives the PID.
     * @param input - the current input value for the loop
     * @return the output generated by this update.
     */
    public double update(double input){
        double elapsedtime;
        long currenttime;

        currenttime = System.currentTimeMillis();
        elapsedtime = ((double) (currenttime - lasttime)) / 1000.0;
        lasttime = currenttime;

        if (elapsedtime > .5)
            elapsedtime = 0.04;

        error = (goal - input);
        
        if (DriverStation.getInstance().isEnabled())
            integral += (error * iGain * elapsedtime);
        else
            integral = 0;
        error *= pGain;
        if (Math.abs(integral) > iMax){
            int multiplier = 1;
            if (integral < 0)
                integral = -1;
            integral = iMax * multiplier;
        }
        if (!ignoreD_FLAG) {
            if (lastInput == 0)
                lastInput = input;
            derivative = (lastInput - input) * dGain / elapsedtime;
            lastInput = input;
        }
        output = error + integral + derivative;
        if (reverse_FLAG)
            output *= -1;
        updated_FLAG = true;
        return filterOutput(output);
    }
    
    /**
     * Sets the goal to work towards. Drives the entire PID loop.
     * @param goal - the requested goal to move toward.
     */
    public void setGoal(double goal){
        this.goal = goal;
    }
    
    /**
     * Getter for the output of the loop.
     * @return the currently calculated output of the PID loop.
     */
    public double getOutput(){
        if (!updated_FLAG)
            System.out.println("Old PID output returned! Has not been updated!");
        updated_FLAG = false;
        return output;
    }
    
    /**
     * Limits the output from the update calculation.
     * @param output - the raw output from the update calculation
     * @return the censored version of the output. if the ignoreLimits flag is 
     * set, it will return the output parameter, unchanged.
     */
    private double filterOutput(double output){
        if (brake_FLAG && goal == 0) {
            error = 0;
            integral = 0;
            derivative = 0;
            return 0;
        }
        if (ignoreLimits_FLAG)
            return output;
        if (output < lowerLimit)
            return lowerLimit;
        if (output > upperLimit)
            return upperLimit;
        return output;
    }
    
    public void setPIDValue(double p, double i, double d){
        pGain = p;
        iGain = i;
        dGain = d;
    }
    
    public double getPValue(){
        return pGain;
    }
    
    public double getIValue(){
        return iGain;
    }
    
    public double getDValue(){
        return dGain;
    }
}
