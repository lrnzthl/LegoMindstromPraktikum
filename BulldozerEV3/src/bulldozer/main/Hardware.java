package bulldozer.main;



import lejos.hardware.Sound;
import lejos.hardware.port.MotorPort;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;

import java.util.LinkedList;

import lejos.hardware.Button	;
import lejos.robotics.RegulatedMotor;



public class Hardware {
    private boolean init;
    private boolean buttonHold;
    private EV3LargeRegulatedMotor motLeft, motRight, servo;
    private Sensors sensors;
    private LinkedList<Integer> orientationHistory;
    private int maxOrHistorySize = 10;

    //in ms, delay between reading the senors
    // preferred value is 20 ms
    private final int sensorReadDelay = 20;

    /** these are default values, they should be modified in Line, Labyrinth and ..**/
    private int motorMaxSpeedProcentage = 60;
    //default value is 6000
    private int motorAccelaration = 6000;

    private int turnSpeedProcentage = 40;
    //050 is too much swings back and fort, 25 is okay, just stop, 40 is also all right

    //eveyrhing over high is white
    private float midPointBWHigh = (float) 0.28;
    private float midPointBWLow = (float) 0.11;

    private float midPointRBHigh = (float) 0.1;
    private float midPointRBLow = (float) 0.75;

    private float midPointWRHigh = (float) 0.28;
    private float midPointWRLow = (float) 0.11;

    private CColor red = new CColor(0.339f, 0.087f, 0.032f);
    private CColor blue = new CColor(0.050f, 0.17f, 0.13f);
    private CColor white = new CColor(0.296f, 0.474f, 0.232f);
    private CColor black = new CColor(0.054f, 0.091f, 0.028f);



    //To let us know if we have to correct to red or white
    public enum actualColor{
    	BW, RB;
    }
    public actualColor acColor;
    
    public Hardware() throws IllegalArgumentException{
        //copy the values after first calibration

        led(9);


            System.out.println("Hardware is being initialized...");

            motRight = new EV3LargeRegulatedMotor(MotorPort.D);
            System.out.println("Motor right is ok");


            motLeft = new EV3LargeRegulatedMotor(MotorPort.A);
            System.out.println("Motor left is ok");

            servo = new EV3LargeRegulatedMotor(MotorPort.B);
            System.out.println("Servo is ok, touch:");


            EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S3);
            System.out.println("Touch ok, color:");
            EV3ColorSensor color = new EV3ColorSensor(SensorPort.S4);
            System.out.println("Color ok, ultra:");

            EV3UltrasonicSensor ultraSensor = new EV3UltrasonicSensor(SensorPort.S2);
            System.out.println("Ultra ok, gyro:");

            EV3GyroSensor gyroSensor = new EV3GyroSensor(SensorPort.S1);
            System.out.println("Gyro Ev3 ok");


       SingleValueSensorWrapper touch = new SingleValueSensorWrapper(touchSensor, "Touch");
       SingleValueSensorWrapper col = new SingleValueSensorWrapper(color, "RGB");
       SingleValueSensorWrapper dist = new SingleValueSensorWrapper(ultraSensor, "Distance");
       SingleValueSensorWrapper gyro = new SingleValueSensorWrapper(gyroSensor, "Angle");

       System.out.println("wrappers ready");
       sensors = new Sensors(sensorReadDelay, touch, col, dist, gyro);

        initialize();

	    if(!init){
	        System.out.println("WARNING: Hardware not initialized properly");
	    }

	    led(1);

        Sound.setVolume(20);
	    orientationHistory = new LinkedList<>();
    }

    /**
     *
     * @return true, if we found the beacon
     */
    public boolean foundBeacon(CColor color, float tolerance) {
        if(color.equals(new CColor(-1.f, -1.f, -1.f))) {
    		return false;
    	}

    	return color.equalsTolerance(this.readColorIntensity());

    }


    /**
     * initializes the sensors
     */
    public void startSensors() {
        System.out.println("Starting sensors..");

        if(! sensors.isInit()){
            System.out.println("Cannot start, sensors must be initialized!");
            return;
        }

        sensors.start();
        System.out.println("Sensors are started");
    }

    /**
     * make a sound
     */
    public void beep() {
        Sound.beep();
    }

    public float readColorIntensity() {
        return sensors.colorIntensity();
    }


    public CColor readRGBColor() {
    	return sensors.colorRGB();
    }

    /**
     * get distance from the ultrasonic sensor
     * @return the distance in CM
     */
    public int getDistance() {
        return Math.round(sensors.getDistance()*100);
    }

    public int getMotorAngle() {
        return motRight.getTachoCount();
    }

    public void setMotorMaxSpeedProcentage(int motorMaxSpeedProcentage) {
        this.motorMaxSpeedProcentage = motorMaxSpeedProcentage;
    }

    public void setMotorAccelaration(int motorAccelaration) {
        this.motorAccelaration = motorAccelaration;
    }

    public void setTurnSpeedProcentage(int turnSpeedProcentage) {
        this.turnSpeedProcentage = turnSpeedProcentage;
    }

    public void robotTurnBlock(int angle) {
            motorsWaitStopMoving();

            int absoluteAngle = angle * 6;

            motorSetSpeedProcentage(turnSpeedProcentage);

            synchMotors();

            if(angle < 0){
                motLeft.rotate(absoluteAngle, true);
                motRight.rotate(-absoluteAngle, true);
            }else{
                motRight.rotate(-absoluteAngle, true);
                motLeft.rotate(absoluteAngle, true);
            }

            deSynchMotors();

            motorsWaitStopMoving();
    }

    public boolean isRightUp() {
        return Button.LEFT.isUp();
    }


    public enum ButtonType {
        UP, DOWN, LEFT, RIGHT, ENTER, ESCAPE, NONE
    }

    /**
     * DEBUGGING
     *  which button is pressed
      * @return  UP, DOWN, LEFT, RIGHT, ENTER, ESCAPE or NONE
     */
    public ButtonType getButtonType(){
        ButtonType buttonType = ButtonType.NONE;
        int clickedButton = Button.getButtons();
        
        if(clickedButton != 0) {
        	if(!buttonHold) {
		        switch (clickedButton){
		            case Button.ID_UP:
		                buttonType = ButtonType.UP;
		                buttonType = ButtonType.ENTER;
		                break;
		            case Button.ID_DOWN:
		                buttonType = ButtonType.DOWN;
		                break;
		            case Button.ID_LEFT:
		                buttonType = ButtonType.LEFT;
		                break;
		            case Button.ID_RIGHT:
		                buttonType = ButtonType.RIGHT;
		                break;
		            case Button.ID_ENTER:
		                buttonType = ButtonType.ENTER;
		                break;
		            case Button.ID_ESCAPE:
		                buttonType = ButtonType.ESCAPE;
		        }
		        buttonHold = true;
        	}
        } else {
        	buttonHold = false;
        }
        return buttonType;
    }

    public boolean isInit() {
        return init;
    }

    /**
     * initalizes the motors and the sensors
     * @return true if everything is ok
     */
    private boolean initialize(){
        System.out.println("Hardware is being initialized");

        if(motLeft == null){
            System.out.println("WARNING: Left motor is null!");
            return false;
        }

        if(motRight == null){
            System.out.println("WARNING: Right motor is null!");
            return false;
        }

        int motorAbsoluteSpeed = (int) (motLeft.getMaxSpeed() * motorMaxSpeedProcentage / 100 );
        motRight.setSpeed(motorAbsoluteSpeed);
        motLeft.setSpeed(motorAbsoluteSpeed);

        motRight.setAcceleration(motorAccelaration);
        motLeft.setAcceleration(motorAccelaration);

        init = sensors.initialize() ? true : false;

        
        return init;
    }

    /**
     * /*
     0: turn off button lights
     1/2/3: static light green/red/yellow
     4/5/6: normal blinking light green/red/yellow
     7/8/9: fast blinking light green/red/yellow
     >9: same as 9.
     * @param color
     */
    public void led(int color){
        //Button.LEDPattern(colorIntensity);
    }

    /**
     *
     * @return true, if the touch sensor is pressed
     */
    public boolean isTouchPressed(){

        if (Float.compare(sensors.touch(), (float)0) > 0){
            System.out.println("Touch is pressed, yes");
            beep();
            return true;
        }

        return false;
    }

    /**
     * both motors move an angle forward;
     * function blocks until the movement is done!
     *
     */
    public void motorForward(int angle){
        synchMotors();

        motRight.rotate(angle, true);
        motLeft.rotate(angle); //in case this works automatic with the first motor

        deSynchMotors();
    }

    public void motorForwardBlock(int angle){
        motorsWaitStopMoving();
        synchMotors();

        motRight.rotate(angle, true);
        motLeft.rotate(angle); //in case this works automatic with the first motor

        deSynchMotors();

        motorsWaitStopMoving();
    }



    /**
     * stopping both motors
     */
    public void motorStop(){
        motLeft.stop(true);
        motRight.stop(true);
    }

    /**
     * sets the speed ot the motor to a procentage of the maximum speed
     * @param procentage; must be between 0 and 1
     */
    public void motorSetSpeedProcentage(int procentage){

        System.out.println("Setting speed to " + procentage + " procent");
        int motorAbsoluteSpeed = Math.round (motLeft.getMaxSpeed() * procentage / 100 );
        motRight.setSpeed(motorAbsoluteSpeed);
        motLeft.setSpeed(motorAbsoluteSpeed);
    }

    /**
     * syncing motors, so that they move the same amount of degrees
     */
    private void synchMotors(){
        //adding left motor to a an array and synchronizing with the right
        motRight.synchronizeWith(new RegulatedMotor[] {motLeft});
        motRight.startSynchronization();
    }

    private void deSynchMotors(){
        motRight.endSynchronization();
    }

    /**
     * motor turns
     * @param angle (can also be negative);
     * negative means go left, positive means go right
     */
    public void robotTurn(int angle){
        //robotTurnGyro(angle);
        //return;

        //360 * (2 * pi) / ( (1/4) *2*pi*r1)
        int absoluteAngle = angle * 6;

        //motorsWaitStopMoving();
        motorSetSpeedProcentage(turnSpeedProcentage);

        synchMotors();

        motRight.rotate(-absoluteAngle, true);
        motLeft.rotate(absoluteAngle, true);


        deSynchMotors();

    }




    public void robotTurnGyro(int angle){
        motorSetSpeedProcentage(turnSpeedProcentage);

        int currentAngle = getAngle();
        int finalAngleToReach = getAngle() + angle;
        System.out.println("VOR:current: " + currentAngle + " final:"+ finalAngleToReach  );

        //motorsWaitStopMoving();


        robotTurn((int) Math.round(angle * 1.3) );

        while( Math.abs(currentAngle - finalAngleToReach) > 1 ){

            currentAngle = getAngle();
            mySleep(1);
        }

        //when final angle is reached, we should stop the motors
        motorStop();

        System.out.println("NACH:current: " + currentAngle + " final:"+ finalAngleToReach  );
        System.out.println("ready with the turn");

    }


    public void robotTurnNonBlockOneWheel(int angle){

        int absoluteAngle = angle * 12;

        motorsWaitStopMoving();

        synchMotors();

        if(angle < 0){
            motRight.rotate((int) Math.abs(absoluteAngle*1.2), true);
        }else{
            motLeft.rotate((int) Math.round(absoluteAngle), true);
        }

        deSynchMotors();
    }

    /**
     * functions blocks until the motors have stopped turning
     */
    public void motorsWaitStopMoving(){

        while(motLeft.isMoving() && motRight.isMoving()){
           mySleep(5);
        }

    }

    public boolean motorsAreMoving(){
        if(! motRight.isMoving() && ! motLeft.isMoving() ){
            return false;
        }

        return true;
    }

    /**
     *
     * @return true, if the colorIntensity sensor is on white; works with check with the midpoint
     */
    public boolean isOnWhite(){

        if(sensors.colorIntensity() > midPointBWHigh ){
            //System.out.println("sensor on white");
        	    acColor = actualColor.BW;
            return true;
        }else if( sensors.colorIntensity() < midPointBWLow){
            return false;
        }else{
            //System.out.println("Hitting midPoint");
            return false;
        }
    }


    public boolean isOnBlack() {
        return readRGBColor().equalsTolerance(black);
    }
    
    /**
    *
    * @return true, if the colorIntensity sensor is on red
    * ; works with check with the midpoint
    */
   public boolean isOnRed(){

       if(sensors.colorIntensity() > midPointRBHigh ){
           //System.out.println("sensor on white");
    	   	   acColor = actualColor.RB;
           return true;
       }else if( sensors.colorIntensity() <  midPointRBLow){
           return false;
       }else{
           //System.out.println("Hitting midPoint");
           return false;
       }
   }
    

    /**
     *
     * @return the midPoint;
     * DO NOT USE to check if sensor is on white -> isOnWhite() function
     */
    public float getMidPointBW(){
        return (white.getIntensity() + black.getIntensity())/2 + black.getIntensity();
    }

    /**
    *
    * @return the midPoint;
    * DO NOT USE to check if sensor is on white -> isOnWhite() function
    */
   public float getMidPointRB(){
       return (red.getIntensity() + black.getIntensity())/2 + black.getIntensity();
   }
    
    /**
     *
     * @return true if the sensor is on the midpoint between black and white
     */
    public boolean isOnMidpointBW(){
        if(sensors.colorIntensity() < midPointBWHigh && sensors.colorIntensity() > midPointBWLow){
        	updateOrientation();
            System.out.println("I am on the middle BW");
            acColor = actualColor.BW;
            return true;
        }
        
        return false;
    }
    
    public boolean isOnMidpointRB(){
        if(sensors.colorIntensity() < midPointRBHigh && sensors.colorIntensity() > midPointRBLow){
        	updateOrientation();
            System.out.println("I am on the middle RB");
            acColor = actualColor.RB;
            beep();
            return true;
        }

        return false;
    }

    
    
    

    
    /**
     *
     * @return current angle, read from the gyro sensor
     */
    public int getAngle(){
        return Math.round(-sensors.getAngle());
    }

    public void servoGoUp(){
        servo.rotate(-90);
        //servo.flt();
    }

    public void servoGoDown(){

        servo.rotate(90);
        //servo.flt();
    }

    public boolean isEscapeUp(){
        return Button.ESCAPE.isUp();
    }

    public boolean isUpUp(){
        return Button.UP.isUp();
    }

    public boolean isLeftUp() { return Button.LEFT.isUp(); }





    private void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void updateOrientation(){
    	
    	int resetTolerance = 40;
    	if(orientationHistory.isEmpty()){
            System.out.println("it is empty, so we add, no conditions");
            orientationHistory.add(getAngle());
    	} else {
    		int average = 0;
    		int angle = getAngle();
    		for(float value : orientationHistory){
    			average += value;
    		}
    		average /= orientationHistory.size();
            //%TODO: Turn avarge-angle to int or compare with Float.compare
    		if(  (Math.abs(average-angle) ) > resetTolerance){
                System.out.println("differnce is above tolerance, clear the list");
                orientationHistory.clear();
    		}
    		orientationHistory.add(angle);
    		if(orientationHistory.size() > maxOrHistorySize){
    			orientationHistory.removeLast();
    		}
    	}

       // System.out.println("after update orientation " + orientationHistory);
    }
    
    /**
     * 
     * @return -1 if too less measurepoints are available. Otherwise eastimate an angle.
     */
    public int estimateOrientation(){
        //System.out.println("Last values: " + orientationHistory);

    	if(orientationHistory.size() < 2){
    		return -1;
    	}
    	float average = 0.f;
		for(float value : orientationHistory){
			average += value;
		}
		average /= orientationHistory.size();
		return Math.round(average);
    }
    
    
}
