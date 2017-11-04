package bulldozer.main;

import java.util.Scanner;



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

    private Sensors sensors;

    //in ms, delay between reading the senors
    // preferred value is 20 ms
    private final int sensorReadDelay = 20;

    private int motorSpeedProcentage = 50 ;
    //default value is 6000
    private int motorAccelaration = 6000;


    private EV3LargeRegulatedMotor motLeft, motRight;





    public Hardware(boolean simulation){
        System.out.println("WARNING: Running in simulation mode!");
        this.simulation = simulation;
        sensors = new Sensors(sensorReadDelay, null, null, null);

        initialize();

        if(!init){
            System.out.println("WARNING: Hardware not initialized properly");
        }
    }



    public Hardware(){
        simulation = false;


        motRight = new EV3LargeRegulatedMotor(MotorPort.A);
        motLeft = new EV3LargeRegulatedMotor(MotorPort.B);


        EV3TouchSensor tLeft = new EV3TouchSensor(SensorPort.S1);
        EV3ColorSensor color = new EV3ColorSensor(SensorPort.S3);
        EV3UltrasonicSensor ultra = new EV3UltrasonicSensor(SensorPort.S4);

        SingleValueSensorWrapper touch = new SingleValueSensorWrapper(tLeft, "Touch");
        SingleValueSensorWrapper col = new SingleValueSensorWrapper(color, "Red");
        SingleValueSensorWrapper dist = new SingleValueSensorWrapper(ultra, "Distance");




        sensors = new Sensors(sensorReadDelay, touch, col, dist);


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


    public enum ButtonType {
        UP, DOWN, LEFT, RIGHT, ENTER, ESCAPE, NONE
    }


    /**
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

        switch (Button.getButtons()){
            case 0:
                buttonType = ButtonType.NONE;
                break;
            case Button.ID_UP:
                buttonType = ButtonType.UP;
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

        return (sensors.touch() == 1);

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
        motLeft.rotate(angle);

        deSynchMotors();
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
        motRight.startSynchronization();;
    }

    private void deSynchMotors(){
        motRight.endSynchronization();
    }


}
