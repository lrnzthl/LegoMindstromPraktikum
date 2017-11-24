package bulldozer.main;

import bulldozer.main.Hardware.ButtonType;
import com.sun.org.apache.xpath.internal.SourceTree;

public class Line extends Brains {

    //rotaion for motors to go forward
    private final int step = 45;
    private final float Kp = 2.5f;

    private final int delay = 30; //different delays in ms
    private float turningAngle = 10.f;
    private float alreadyTurned = 0.f; //already turned angle

    private int zigZagAngle = 10;

    private long lastReset = 0; //last time we have resetted the time

    private int offsetYobstacle= 500; //lenght of obstacle
    private int offsetXobstacle = 500; //lenght of obstacle



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


    private void rotateToMiddle() {

        float correction =  ( Kp * ( hardware.getMidPointBW() - hardware.readColor() ) );
        int toTurn = Math.round(correction * turningAngle) ;

        System.out.print(" correction:" + correction+", with angle toTurn:"+toTurn + ".....");
        System.out.print("current angle:"+(alreadyTurned - hardware.getAngle())+"...");


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



    private void goAroundObstacle() {

        assert(hardware.isTouchPressed());


        hardware.motorForwardBlock(-180);

        //rotate right
        System.out.println("turning right...");
        hardware.robotTurn(90);

        hardware.motorForwardBlock(offsetXobstacle);

        //oops
        while(!hardware.isTouchPressed()){
            //go back and try again
            System.out.println("trying to correct...");
            hardware.motorForwardBlock(-180);
            hardware.robotTurn((int) turningAngle);
            hardware.motorForwardBlock(180);
        }

        System.out.println("Obstacle should be behind us: 1");

        //obstacle is behind us
        //rotate left
        hardware.robotTurn(-90);

        hardware.motorForwardBlock(offsetYobstacle);

        //oops
        while(!hardware.isTouchPressed()){
            //go back and try again
            System.out.println("trying to correct...");
            hardware.motorForwardBlock(-180);
            hardware.robotTurn((int) turningAngle);
            hardware.motorForwardBlock(180);
        }

        System.out.println("Obstacle should be behind us: 2");

        //turn left, we should be close to the white line
        hardware.robotTurn(-90);

        while( !hardware.isOnMidpointBW()){
            hardware.motorForwardBlock(step);

            if(hardware.isTouchPressed()){
                hardware.motorForwardBlock(-180);
                hardware.robotTurn((int) -turningAngle);
            }
        }


    }




}
