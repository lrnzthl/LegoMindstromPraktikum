package bulldozer.main;

import java.util.Scanner;


import lejos.hardware.Sound;
import lejos.hardware.port.MotorPort;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.Button	;
import lejos.robotics.RegulatedMotor;


public class Hardware {
    private boolean init;
    private boolean simulation;
    private boolean buttonHold;

    private Sensors sensors;



    //in ms, delay between reading the senors
    // preferred value is 20 ms
    private final int sensorReadDelay = 20;

    private int motorSpeedProcentage = 20;
    //default value is 6000
    private int motorAccelaration = 6000;


    private EV3LargeRegulatedMotor motLeft, motRight;


    //everything over midPointHigh is white
    //everying lower than midPointLow is black
    private float midPointHigh, midPointLow;


    public Hardware(boolean simulation){
        System.out.println("WARNING: Running in simulation mode!");
        this.simulation = simulation;
        sensors = new Sensors(sensorReadDelay, null, null);

        initialize();

        if(!init){
            System.out.println("WARNING: Hardware not initialized properly");
        }
    }



    public Hardware(){
        simulation = false;

        System.out.println("Beginning of constructor");

        motRight = new EV3LargeRegulatedMotor(MotorPort.A);
        System.out.println("Motor right is ok");


        motLeft = new EV3LargeRegulatedMotor(MotorPort.B);
        System.out.println("Motor left is ok");

        System.out.println("Motors in constructor ready");


        EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S3);
        System.out.println("Touch EV3 ok");
        EV3ColorSensor color = new EV3ColorSensor(SensorPort.S4);
        System.out.println("Color EV3 ok");
       
       // EV3UltrasonicSensor ultra = new EV3UltrasonicSensor(SensorPort.S2);
     //   System.out.println("Ultra Ev3 on");


       SingleValueSensorWrapper touch = new SingleValueSensorWrapper(touchSensor, "Touch");
       SingleValueSensorWrapper col = new SingleValueSensorWrapper(color, "Red");
    //  SingleValueSensorWrapper dist = new SingleValueSensorWrapper(ultra, "Distance");
        System.out.println("wrappers ready");




        //sensors = new Sensors(sensorReadDelay, touch, col, dist);
        sensors = new Sensors(sensorReadDelay, touch, col);


       initialize();

        if(!init){
            System.out.println("WARNING: Hardware not initialized properly");
        }
    }



    /**
     *
     * @return true, if we found the beacon
     */
    public boolean foundBeacon() {
        return false;
    }

    public void startSensors() {
        if(! sensors.isInit()){
            System.out.println("Cannot start, sensors must be initialized!");
            return;
        }


        sensors.start();
    }


    /**
     * make a sound
     */
    public void beep() {
        Sound.beep();
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
        //TODO: get which button is up

        if(simulation){
            Scanner keyboard = new Scanner(System.in);
            boolean exit = false;
            while (!exit) {
                System.out.println("Enter command (quit to exit):");

                if(!keyboard.hasNextLine()){
                    return ButtonType.NONE;
                }

                String input = keyboard.nextLine();


                if(input != null) {
                    System.out.println("Your input is : " + input);
                    if ("quit".equals(input)) {
                        System.out.println("Exit programm");
                        exit = true;
                    } else if ("x".equals(input)) {
                        return ButtonType.ESCAPE;
                    }else if ("l".equals(input)) {
                        return ButtonType.LEFT;
                    }else if ("r".equals(input)) {
                        return ButtonType.RIGHT;
                    }else if ("e".equals(input)){
                        return ButtonType.ENTER;
                    }
                }
            }
            keyboard.close();
        }


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
        return simulation ?  true : init;
    }


    /**
     * initalizes the motors and the sensors
     * @return true if everything is ok
     */
    private boolean initialize(){
        boolean problem = false;
        System.out.println("Hardware is being initialized");
        //
        //
        //
        if(motLeft == null){
            System.out.println("WARNING: Left motor is null!");
            return false;
        }

        if(motRight == null){
            System.out.println("WARNING: Right motor is null!");
            return false;
        }

        int motorAbsoluteSpeed = (int) (motLeft.getMaxSpeed() * motorSpeedProcentage / 100 );
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
     *
     * @return true, if the touch sensor is pressed
     */
    public boolean isTouchPressed(){

        if (sensors.touch() == 1){
            System.out.println("Touch is pressed, yes");
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
        System.out.println("Synced blocked movement " + angle);

        synchMotors();

        motRight.rotate(angle);
        motLeft.rotate(angle); //in case this works automatic with the first motor

        deSynchMotors();
    }

    /**
     * Does not ork properly...
     * move forward for
     * @param ms
     */
    public void motorMoveForwardMs(int ms){
        synchMotors();

        motRight.forward();
        mySleep(ms);
        motRight.stop();

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
     * @param procentage
     */
    public void motorSetSpeedProcentage(int procentage){

        int motorAbsoluteSpeed = (int) (motLeft.getMaxSpeed() * procentage / 100 );
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
    public void motorTurn(int angle){

    	motLeft.stop();
    	motRight.stop();
        if(angle < 0){
            motLeft.rotate(angle, true);
            motRight.rotate(-angle);
        }else{
            motRight.rotate(-angle, true);
            motLeft.rotate(angle);
        }

        //wait until motors have stopped turning
        while(motLeft.isMoving()){
            mySleep(sensorReadDelay);
        }

        while (motRight.isMoving()){
            mySleep(sensorReadDelay);
        }

    }



    public void colorSensorCalibrate(){
        float offsetWhite = (float) 0.2;
        float offsetBlack = (float) 0.2;

        System.out.println("Starting to calibrate color sensor");

        System.out.println("Please go entirely on white and press Down button!");

        while(getButtonType().equals(ButtonType.DOWN)){
            mySleep(sensorReadDelay);
        }

        float white = sensors.color();
        System.out.println("Read value is " + white);
        midPointHigh = white - offsetWhite;

        System.out.println("Everything more than " + midPointHigh + " is white");



        System.out.println("Please go entirely on black and press Down button!");

        while(getButtonType().equals(ButtonType.DOWN)){
            mySleep(sensorReadDelay);
        }

        float black = sensors.color();
        System.out.println("Read value is " + black);
        midPointLow = black + offsetBlack;

        System.out.println("Everything more than " + midPointHigh + " is white");



    }


    /**
     *
     * @return true, if the color sensor is on white; works with check with the midpoint
     */
    public boolean isOnWhite(){

        if(sensors.color() > midPointHigh ){
            System.out.println("sensor on white");
            return true;
        }else if( sensors.color() < midPointLow){
            return false;
        }else{
            System.out.println("Hitting midPoint");
            return false;
        }

    }

    /**
     *
     * @return the midPoint;
     * DO NOT USE to check if sensor is on white -> isOnWhite() function
     */
    public float getMidPoint(){
        return (midPointHigh + midPointLow)/2;
    }

    /**
     *
     * @return true if the sensor is on the midpoint between black and white
     */
    public boolean isOnMidpoint(){
        if(sensors.color() < midPointHigh && sensors.color() > midPointLow){
            return true;
        }

        return false;
    }


    private void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
