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
    private LinkedList<Float> orientationHistory;
    private int maxOrHistorySize = 10;

    //in ms, delay between reading the senors
    // preferred value is 20 ms
    private final int sensorReadDelay = 20;

    /** these are default values, they should be modified in Line, Labyrinth and ..**/
    private int motorMaxSpeedProcentage = 60;
    //default value is 6000
    private int motorAccelaration = 6000;
    private double turnSpeedProcentage = 0.4;
    //0.5 is too much swings back and fort, 0.25 is okay, just stop, 0.4 is also all right

    //eveyrhing over high is white
    private float midPointBWHigh = (float) 0.28;
    private float midPointBWLow = (float) 0.11;

    private float midPointRBHigh = (float) 0.28;
    private float midPointRBLow = (float) 0.11;

    private float midPointWRHigh = (float) 0.28;
    private float midPointWRLow = (float) 0.11;

    private float colorWhite = (float) 0.33;
    private float colorBlack = (float) 0.05;
    private float colorRed = (float) 0.15;

    private CColor red = new CColor(0.339f, 0.087f, 0.032f);
    private CColor blue = new CColor(0.050f, 0.17f, 0.13f);
    private CColor white = new CColor(0.296f, 0.474f, 0.232f);
    private CColor black = new CColor(0.054f, 0.091f, 0.028f);

    public Hardware(){
        //copy the values after first calibration


        System.out.println("Hardware is being initialized...");

        motRight = new EV3LargeRegulatedMotor(MotorPort.D);
        System.out.println("Motor right is ok");


        motLeft = new EV3LargeRegulatedMotor(MotorPort.A);
        System.out.println("Motor left is ok");

        servo = new EV3LargeRegulatedMotor(MotorPort.B);
        System.out.println("Servo is ok");


        EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S3);
        System.out.println("Touch ok");
        EV3ColorSensor color = new EV3ColorSensor(SensorPort.S4);
        System.out.println("Color ok");
       
       EV3UltrasonicSensor ultraSensor = new EV3UltrasonicSensor(SensorPort.S2);
       System.out.println("Ultra on");

       EV3GyroSensor gyroSensor = new EV3GyroSensor(SensorPort.S1);
       System.out.println("Gyro Ev3 on");

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
	    orientationHistory = new LinkedList<Float>();
    }

    /**
     *
     * @return true, if we found the beacon
     */
    public boolean foundBeacon(CColor color, float tolerance) {
        if(color.equals(new CColor(-1.f, -1.f, -1.f))) {
    		return false;
    	}

    	return color.equalsTolerance(this.readColor());

    	/*
        boolean returnValue = false;
    	int checkComponents = 0;
    	
    	float lowerMultiply = 1.f - tolerance;
    	float upperMultiply = 1.f + tolerance;
    	
    	CColor sensorColors = this.readRGBColor();
    	CColor lowerColor = new CColor(color.getRed()*lowerMultiply,
    									   color.getGreen()*lowerMultiply, 
    									   color.getBlue()*lowerMultiply);
    	CColor upperColor = new CColor(color.getRed()*upperMultiply,
    									   color.getGreen()*upperMultiply, 
    									   color.getBlue()*upperMultiply);
   
    	return sensorColors.isGreaterThan(lowerColor) && sensorColors.isLessThan(upperColor);
    	*/
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

    public float readColor() {
        return sensors.color();
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

    /**
     * both motors rotate
     * @param angle degrees forward without blcking
     */
    public void motorForwardNonBlock(int angle) {
        motLeft.stop();
        motRight.stop();

        synchMotors();
        motLeft.rotate(angle, true);
        motRight.rotate(angle, true);

        deSynchMotors();

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

    public void setTurnSpeedProcentage(double turnSpeedProcentage) {
        this.turnSpeedProcentage = turnSpeedProcentage;
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

        if(init == true){
            Button.LEDPattern(1);
        }else{
            Button.LEDPattern(5);
        }
        
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
        Button.LEDPattern(color);
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
    public void motorForwardBlock(int angle){
        synchMotors();

        motRight.rotate(angle);
        motLeft.rotate(angle); //in case this works automatic with the first motor

        deSynchMotors();
    }

    /**
     * both motors are started;
     * motorStop must be called for the robot to be stopped
     */
    public void motorForward(){
        motRight.forward();
        motLeft.forward();
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
    public void motorSetSpeedProcentage(double procentage){

        System.out.println("Setting speed to " + procentage + " procent");

        int motorAbsoluteSpeed = (int) Math.round( procentage * (motRight.getMaxSpeed() * motorMaxSpeedProcentage /100)  );
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
        //90 grad is 540
        //robotTurnGyro(angle);
        //return;

        //360 * (2 * pi) / ( (1/4) *2*pi*r1)
        int absoluteAngle = angle * 6;

        //motorsWaitStopMoving();
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

    }




    public void robotTurnGyro(int angle){

        motorSetSpeedProcentage(turnSpeedProcentage);

        System.out.println("current: "+ getAngle());
        float finalAngleToReach = getAngle() + angle;
        System.out.println("finalAngleToReach " + finalAngleToReach);

        //motorsWaitStopMoving();

        synchMotors();



        // getAngle < finalAngle
        System.out.println("current: " + getAngle() + " final:"+ finalAngleToReach  );
        System.out.println("before: " + (Float.compare(getAngle(),finalAngleToReach) < 0) );
        while( Float.compare(getAngle(),finalAngleToReach) < 0 ){
            if(angle < 0){
                motLeft.rotate(angle*20, true);
                motRight.rotate(-angle*20, true);
            }else{
                motRight.rotate(-angle*20, true);
                motLeft.rotate(angle*20, true);
            }

            System.out.println("current: " + getAngle() + " final:"+ finalAngleToReach  );
            System.out.println(" in while ");
            System.out.println("schleifeinvariant " + (Float.compare(getAngle(),finalAngleToReach) < 0) );
            mySleep(5);
        }
        System.out.println("ready with the turn");
        //when final angle is reached, we should stop the motors
        motorStop();

        deSynchMotors();
    }


    public void robotTurnNonBlock(int angle){
        //%TODO:
        int absoluteAngle = angle * 12;

        motorsWaitStopMoving();

        //%TODO: problem with synching motors?
        synchMotors();

        if(angle < 0){
            motRight.rotate(Math.abs(absoluteAngle),true);
        }else{
            motLeft.rotate(absoluteAngle, true);
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
     * @return true, if the color sensor is on white; works with check with the midpoint
     */
    public boolean isOnWhite(){

        if(sensors.color() > midPointBWHigh ){
            //System.out.println("sensor on white");
            return true;
        }else if( sensors.color() < midPointBWLow){
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
        return (colorWhite + colorBlack)/2 + colorBlack;
    }

    /**
     *
     * @return true if the sensor is on the midpoint between black and white
     */
    public boolean isOnMidpointBW(){
        if(sensors.color() < midPointBWHigh && sensors.color() > midPointBWLow){
        	updateOrientation();
            System.out.println("I am on the middle");
            return true;
        }

        return false;
    }

    /**
     *
     * @return current angle, read from the gyro sensor
     */
    public float getAngle(){
        return sensors.getAngle();
    }

    public void servoGoUp(){
        servo.rotate(-90);
    }

    public void servoGoDown(){

        servo.rotate(90);
    }

    public boolean isEscapeUp(){
        return Button.ESCAPE.isUp();
    }

    public boolean isUpUp(){
        return Button.UP.isUp();
    }

    public boolean isLeftUp() { return Button.LEFT.isUp(); }

    public float getColorWhite(){
        return colorWhite;
    }

    public float getColorBlack(){
        return colorBlack;
    }

    private void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void updateOrientation(){
    	float resetTolerance = 0.2f;
    	if(orientationHistory.isEmpty()){
    		orientationHistory.add(getAngle());
    	} else {
    		float average = 0.f;
    		float angle = getAngle();
    		for(float value : orientationHistory){
    			average += value;
    		}
    		average /= orientationHistory.size();
    		if((Math.abs(average-angle) / angle) > resetTolerance){
    			orientationHistory.clear();
    		}
    		orientationHistory.add(angle);
    		if(orientationHistory.size() > maxOrHistorySize){
    			orientationHistory.removeLast();
    		}
    	}
    }
    
    /**
     * 
     * @return -1 if too less measurepoints are available. Otherwise eastimate an angle.
     */
    public int estimateOrientation(){
    	if(orientationHistory.size() < 3){
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
