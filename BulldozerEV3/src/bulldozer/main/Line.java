package bulldozer.main;

import bulldozer.main.Hardware.ButtonType;

public class Line extends Brains {

    //rotaion for motors to go forward
    private final int step = 45;
    private final float Kp = 1.5f;

    private final int delay = 30; //ms
    private float turningAngle = 10.f;
    private int alreadyTurned = 0;

    private long lastReset = 0;

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

            long now = System.currentTimeMillis();
            long diff = now - lastReset;

            hardware.setSpeed(getSpeed(diff));

            hardware.motorForwardBlock(step);


            alreadyTurned = 0; //resetting the variable with how much we've turned
            while(! hardware.isOnMidpoint()){
                rotateToMiddle();
                lastReset = System.currentTimeMillis();
            }

        }

    }

    private void rotateToMiddle() {
        System.out.println("Not in middle, trying to rotate");

        float correction =  ( Kp * ( hardware.getMidPoint() - hardware.readColor() ) );
        int toTurn = Math.round(correction * turningAngle) ;

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


    private int getSpeed(long diff){
        double value = (1.0/(1.0+Math.exp(-((((double) diff)/1000.0) - 8.0)))) ;
        double newSpeed = value * 60.0;
        int endValue = (int)value;
        return endValue;
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
