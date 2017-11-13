package bulldozer.main;

import bulldozer.main.Hardware.ButtonType;

public class Line extends Brains {

    //rotaion for motors to go forward
    private final int step = 90;
    private final float Kp = 1.f;

    private final int delay = 30; //ms
    private float turningAngle = 10.f;
    private int alreadyTurned = 0;



    public Line(Hardware hardware) {
        super(hardware);
    }




    @Override
    public void run(){


        if(hardware.getColorBlack() ==0 || hardware.getColorWhite() == 0){
            System.out.println("Colors not calibrated");
            returnValue = -1;
            return;
        }

        while (! hardware.isOnMidpoint()){
            System.out.println("Put me on between white and black!");
            hardware.beep();
            mySleep(delay);
        }

        //we are on the middle
        while(running){
            hardware.ledWhite();

            hardware.motorForwardBlock(step);


            alreadyTurned = 0; //resetting the variable with how much we've turned
            while(! hardware.isOnMidpoint()){
                rotateToMiddle();
            }

        }

    }

    private void rotateToMiddle() {
        System.out.println("Not in middle, trying to rotate");

        float correction =  ( Kp * ( hardware.getMidPoint() - hardware.readColor() ) );
        int toTurn = (int) (correction * turningAngle) ;

        System.out.println("toTurn="+toTurn);

        //%TODO:
        if( alreadyTurned + toTurn > 80){
            System.out.println("Nope >80, probably end of the line!?!?");

            //go back alreadyTurned degrees to the right
            hardware.robotTurn(alreadyTurned);

            //%TODO: call function to handle that
            return;
        }


        alreadyTurned += toTurn;

        //%TODO:
        hardware.robotTurn( -toTurn );


    }


    /**
     * performs forward zig-zag movements, trying to find right side of the white line
     * @return true if the midpoint is found
     */
    private boolean zigZagMovements(){

        if(hardware.isOnMidpoint()){
            return true;
        }


        return false;

    }
}
