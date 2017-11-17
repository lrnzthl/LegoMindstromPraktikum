package bulldozer.main;

import bulldozer.main.Hardware.ButtonType;
import com.sun.org.apache.xpath.internal.SourceTree;

public class Line extends Brains {

    //rotaion for motors to go forward
    private final int step = 45;
    private final float Kp = 1.5f;
    private float[] beaconColor = {0.f,0.f,0.f};

    private final int delay = 30; //ms
    private float turningAngle = 10.f;
    private int alreadyTurned = 0;

    private int zigZagAngle = 50;

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

            hardware.motorSetSpeedProcentage(getSpeed(diff));

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

        System.out.print(" with angle toTurn= "+toTurn + "\n");

        //%TODO:
        if( alreadyTurned + toTurn > 80){
            System.out.println("Nope >80, probably end of the line!?!?");

            //go back alreadyTurned degrees to the right
            hardware.robotTurn(alreadyTurned);

            zigZagMovements();
            return;
        }


        alreadyTurned += toTurn;

        hardware.robotTurn( -toTurn );


    }


    private double getSpeed(long diff){
        double minimumOffset = 0; //should be smaller than 8
        double value = 1.0/(1.0+Math.exp(-((((double) diff)/1000.0) - 8.0 + minimumOffset))) ;
        return value;
    }


    /**
     * performs forward zig-zag movements, trying to find right side of the white line
     * @return true if the midpoint is found
     */
    private void zigZagMovements(){
        //assert we are exactly in the middle!
        int initialAngle = zigZagAngle;

        int angle = 2*zigZagAngle;

        //inital turn
        hardware.robotTurnNonBlock(initialAngle);
        while(hardware.motorsAreMoving()) {

            if(hardware.isOnMidpoint()){
                System.out.println("Found mid point!");
                hardware.motorStop();
            }

            mySleep(delay);
        }


        //following turns
        while(! hardware.isOnMidpoint()){
            System.out.println("Searching white line...");

            hardware.robotTurnNonBlock(angle);
            while(hardware.motorsAreMoving()) {

                if(hardware.isOnMidpoint()){
                    System.out.println("Found mid point!");
                    hardware.motorStop();
                }

                mySleep(delay);
            }

            angle = angle*(-1);
        }

    }


    /**
     * rotates for
     * @param angle and when it rotates, we are checking if we are on the middle
     * @return true if we are on the middle
     */
    private boolean carefulTurn(int angle){
        System.out.println("Careful turning");
        int times = 4; //how many times should it stop

        for(int i=0; i<times; i++){
            if(hardware.isOnMidpoint()){
                return true;
            }

            hardware.robotTurn(angle/times);
        }


        hardware.robotTurnNonBlock(zigZagAngle);

        while (! hardware.isOnMidpoint()){
            mySleep(20);
        }

        if(hardware.isOnMidpoint()){
            hardware.motorStop();
        }

        return false;
    }
}
