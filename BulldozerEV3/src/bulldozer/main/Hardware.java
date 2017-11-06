package bulldozer.main;

import java.util.Scanner;




import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.Button	;





public class Hardware {
    private boolean init;
    private boolean simulation;


    /**
     *
     * @return true, if we found the beacon
     */
    public boolean foundBeacon() {
        return false;
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
    
    public Button getButtonReleased() {
    	
    }


    public Hardware(boolean simulation){
        this.simulation = simulation;
    }



    public Hardware(){
        simulation = false;
        initialize();
    }


    public boolean isInit() {
        return simulation ?  true : init;
    }


    /**
     * initalizes the different hardware components
     */
    private boolean initialize(){
        //
        //
        //
        init = true;
        return init;
    }



}
