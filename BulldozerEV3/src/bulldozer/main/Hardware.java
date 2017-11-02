package bulldozer.main;

import java.util.Scanner;

public class Hardware {
    private boolean init;
    private boolean simulation;


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
                    }
                }
            }
            keyboard.close();
        }





        return ButtonType.NONE;
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
