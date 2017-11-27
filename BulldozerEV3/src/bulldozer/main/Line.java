package bulldozer.main;

public class Line extends Brains {

    //rotaion for motors to go forward
    private final int step = 45;
    private final float Kp = 2.5f;

    private final int delay = 30; //different delays in ms
    private float turningAngle = 10.f;
    private float initialRotationAngle = 0.f; //already turned angle

    private int zigZagAngle = 20;

    private long lastReset = 0; //last time we have resetted the time



    private int motorMaxSpeedProcentage = 60;
    //default value is 6000
    private int motorAccelaration = 6000;
    private double turnSpeedProcentage = 0.35;
    //0.5 is too much swings back and fort, 0.25 is okay, just stop, 0.4 is also all right


    private int offsetXobstacle = 670; //length of obstacle
    private int offsetYobstacle = 1600;


    public Line(Hardware hardware) {
        super(hardware);
        beaconColor = new CColor(0.306f,0.071f,0.215f); //red
        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setMotorAccelaration(motorAccelaration);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
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
            hardware.led(7);

            while(hardware.isTouchPressed()){
                System.out.println("Touch is pressed, cannot go forward");
                goAroundObstacle();
                lastReset = System.currentTimeMillis();
            }

            long now = System.currentTimeMillis();
            long diff = now - lastReset;

            hardware.motorSetSpeedProcentage(getSpeed(diff));

            hardware.motorForward(step);

            //initialRotationAngle = hardware.getAngle();
            while(! hardware.isOnMidpointBW()){
                hardware.led(8);
                System.out.println("are initial rot. angle:  " + initialRotationAngle);
                System.out.println("Not in middle, trying to rotate, color:" + hardware.readColor());
                rotateToMiddle();
                lastReset = System.currentTimeMillis();
            }
            initialRotationAngle = hardware.getAngle(); //resetting the variable with how much we've turned
            System.out.println("Resetting already turned and alreadyTruned: " + initialRotationAngle);
        }
    }

    private void rotateToMiddle() {

        float correction =  ( Kp * ( hardware.getMidPointBW() - hardware.readColor() ) );
        int toTurn = Math.round(correction * turningAngle) ;

        float currentGyroAngle = hardware.getAngle();
        float angleDiff = initialRotationAngle - currentGyroAngle;

        System.out.println("current gyro angle:" + currentGyroAngle);
        System.out.print(" correction:" + correction + ", with angle toTurn:"+ toTurn + ".....");
        System.out.print("current angle:"+ angleDiff +"...");



        if( Math.abs(angleDiff) > 80 ){
            System.out.println("Nope >80, probably end of the line!?!?");

            hardware.robotTurn(Math.round(hardware.estimateOrientation() + angleDiff ));

            zigZagMovements();
            lastReset = System.currentTimeMillis();
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
     */
    private void zigZagMovements(){
        System.out.println("starting zig zag");
        //assert we are exactly in the middle!
        int initialAngle = zigZagAngle;

        int angle = -2*zigZagAngle;

        //inital turn
        hardware.robotTurnNonBlockOneWheel(initialAngle);
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

            hardware.robotTurnNonBlockOneWheel(angle);
            while(hardware.motorsAreMoving()) {

                if(hardware.isOnMidpointBW()){
                    System.out.println("Found mid point!");
                    hardware.motorStop();
                }

                mySleep(delay);
            }

            angle = angle*(-1);
        }

    }

    private void goAroundObstacle() {
        float motorSpeed = 0.7f;

        hardware.motorSetSpeedProcentage(motorSpeed);

        System.out.println("going a little bit back");
        hardware.motorForwardBlock(-180);



        //rotate right
        System.out.println("turning right...");
        hardware.robotTurnBlock(90);

        hardware.motorSetSpeedProcentage(motorSpeed);
        hardware.motorForwardBlock(offsetXobstacle);

        System.out.println("Obstacle should be behind us: 1");

        //obstacle is behind us
        //rotate left
        hardware.robotTurnBlock(-90);

        hardware.motorSetSpeedProcentage(motorSpeed);
        hardware.motorForwardBlock(offsetYobstacle);

        System.out.println("Obstacle should be behind us: 2");

        //turn left, we should be close to the white line
        hardware.robotTurnBlock(-90);

        while( !hardware.isOnMidpointBW()){
            hardware.motorForward(step);
        }

        hardware.motorStop();
        initialRotationAngle = hardware.getAngle();


        /*
        //oops
        while(hardware.isTouchPressed()){
            //go back and try again
            System.out.println("trying to correct...");
            hardware.motorForwardBlock(-180);
            hardware.robotTurnBlock((int) turningAngle);
            hardware.motorForwardBlock(180);
        }
        */
    }



    private void goAroundObstacle1() {

        assert(hardware.isTouchPressed());


        hardware.motorForward(-180);

        //rotate right
        System.out.println("turning right...");
        hardware.robotTurn(90);

        tryToMove(offsetXobstacle);

        System.out.println("Obstacle should be behind us: 1");

        //obstacle is behind us
        //rotate left
        hardware.robotTurn(-90);

        tryToMove(offsetYobstacle);

        System.out.println("Obstacle should be behind us: 2");

        //turn left, we should be close to the white line
        hardware.robotTurnBlock(-90);

        while( !hardware.isOnMidpointBW()){
            hardware.motorForward(step);

            if(hardware.isTouchPressed()){
                hardware.motorForward(-180);
                hardware.robotTurn((int) -turningAngle);
            }
        }


    }

    /**
     * robot tries to rotate both motors
     * @param angle; If the touch sensor is pressed, the robot goes back a bit and tries finish what he has left
     */
    private void tryToMove(int angle){
        System.out.println("try to move...");
        int currentMotorAngle = hardware.getMotorAngle();
        hardware.motorForwardNonBlock(angle);


        while(hardware.motorsAreMoving()){

            if(hardware.isTouchPressed()){
                hardware.motorStop();
                int toTurnLeft = offsetXobstacle - currentMotorAngle;

                //turn around and try to finish it
                hardware.motorForward(-180);
                hardware.robotTurn((int) -turningAngle);
                tryToMove(toTurnLeft);
            }

            mySleep(delay);
        }
    }






}
