package bulldozer.main;

import bulldozer.main.Hardware.ButtonType;
import com.sun.org.apache.xpath.internal.SourceTree;

public class Line extends Brains {

    //rotaion for motors to go forward
    private final int step = 45;
    private final float Kp = 2.5f;
    private float[] beaconColor = {0.306f,0.071f,0.215f}; //red

    private final int delay = 30; //ms
    private float turningAngle = 10.f;
    private float alreadyTurned = 0.f;

    private int zigZagAngle = 10;

    private long lastReset = 0;

    private int offsetOfObstacle = 8; //in cm



    public Line(Hardware hardware) {
        super(hardware);
        beaconColor = new float[]{0.306f,0.071f,0.215f}; //red
    }




    @Override
    public void run(){
        hardware.led(9);
        hardware.servoGoUp();

        if(hardware.getColorBlack() ==0 || hardware.getColorWhite() == 0){
            System.out.println("Colors not calibrated");
            returnValue = -1;
            return;
        }

        while (! hardware.isOnMidpointBW()){
            System.out.println("Put me on between white and black!");
            hardware.beep();
            mySleep(delay);
        }




        //we are on the middle
        while(running){
            hardware.led(3);


            while(hardware.isTouchPressed()){
                System.out.println("Touch is pressed, cannot go forward");
                goAroundObstacle();
                mySleep(delay);
            }


            long now = System.currentTimeMillis();
            long diff = now - lastReset;

            hardware.motorSetSpeedProcentage(getSpeed(diff));

            hardware.motorForwardBlock(step);



            //alreadyTurned = hardware.getAngle();
            while(! hardware.isOnMidpointBW()){
                hardware.led(8);
                System.out.println("we have already turned " + alreadyTurned + " angles");
                System.out.println("Not in middle, trying to rotate, color:" + hardware.readColor());
                rotateToMiddle();
                lastReset = System.currentTimeMillis();
            }
            alreadyTurned = hardware.getAngle(); //resetting the variable with how much we've turned
            System.out.println("Resetting already turned and alreadyTruned: " + alreadyTurned);

        }

    }

    private void goAroundObstacle() {

        if(!hardware.isTouchPressed()){
            System.out.println("touch not pressed !?!?");
        }


        hardware.motorForwardBlock(-360);

        //rotate right
        hardware.robotTurn(90);

        while(hardware.getDistance() < offsetOfObstacle && !hardware.isTouchPressed()){

        }

    }

    private void rotateToMiddle() {

        float correction =  ( Kp * ( hardware.getMidPointBW() - hardware.readColor() ) );
        int toTurn = Math.round(correction * turningAngle) ;

        System.out.print(" correction:" + correction+", with angle toTurn:"+toTurn + ".....");
        System.out.print("current angle:"+(alreadyTurned - hardware.getAngle())+"...");

        //%TODO:
        if( Math.abs(alreadyTurned - hardware.getAngle()) > 80 ){
            System.out.println("Nope >80, probably end of the line!?!?");

            //go back alreadyTurned degrees to the right
            hardware.robotTurn(Math.round(alreadyTurned));

            zigZagMovements();
            return;
        }


        hardware.robotTurn( -toTurn );


    }


    private double getSpeed(long diff){
        double accel = 10;
        double minimumOffset = 3; //should be smaller than 8

        diff = Math.round(accel * diff);
        double value = 1.0/(1.0+Math.exp(-((((double) diff)/1000.0) - 8.0 + minimumOffset))) ;
        //return 40;
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

            if(hardware.isOnMidpointBW()){
                System.out.println("Found mid point!");
                hardware.motorStop();
            }

            mySleep(delay);
        }


        //following turns
        while(! hardware.isOnMidpointBW()){
            System.out.println("Searching white line...");

            hardware.robotTurnNonBlock(angle);
            while(hardware.motorsAreMoving()) {

                if(hardware.isOnMidpointBW()){
                    System.out.println("Found mid point!");
                    hardware.motorStop();
                }

                mySleep(delay);
            }

            //angle = angle*(-1);
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
            if(hardware.isOnMidpointBW()){
                return true;
            }

            hardware.robotTurn(angle/times);
        }


        hardware.robotTurnNonBlock(zigZagAngle);

        while (! hardware.isOnMidpointBW()){
            mySleep(20);
        }

        if(hardware.isOnMidpointBW()){
            hardware.motorStop();
        }

        return false;
    }
}
