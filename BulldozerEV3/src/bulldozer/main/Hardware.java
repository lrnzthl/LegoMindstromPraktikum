package bulldozer.main;

import java.util.Scanner;




import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.Button	;





public class Hardware {
    private boolean init;
    private boolean simulation;

    private Sensors sensors;

    //in ms, delay between reading the senors
    private final int sensorReadDelay = 100;

    private EV3LargeRegulatedMotor mRight, mLeft;





    public Hardware(boolean simulation){
        this.simulation = simulation;
        sensors = new Sensors(sensorReadDelay, null, null, null);
        initialize();
    }



    public Hardware(){
        simulation = false;



        EV3TouchSensor tLeft = new EV3TouchSensor(SensorPort.S1);
        EV3ColorSensor color = new EV3ColorSensor(SensorPort.S3);
        EV3UltrasonicSensor ultra = new EV3UltrasonicSensor(SensorPort.S4);

        SingleValueSensorWrapper touch = new SingleValueSensorWrapper(tLeft, "Touch");
        SingleValueSensorWrapper col = new SingleValueSensorWrapper(color, "Red");
        SingleValueSensorWrapper dist = new SingleValueSensorWrapper(ultra, "Distance");




        sensors = new Sensors(sensorReadDelay, touch, col, dist);
        initialize();
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
     * initalizes the different hardware components
     */
    private boolean initialize(){
        System.out.println("Hardware is being initialized");
        //
        //
        //



        sensors.initialize();


        init = true;
        return init;
    }



}
