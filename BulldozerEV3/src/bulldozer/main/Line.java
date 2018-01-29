package bulldozer.main;

public class Line extends Brains {

    //rotaion for motors to go forward
    private final int step = 45;
    private final float Kp = 1.5f;

    private final int delay = 30; //different delays in ms
    private int turningAngle = 10;
    private int initialRotationAngle = 0; //already turned angle

    private int zigZagAngle = 15;

    private long lastReset = 0; //last time we have resetted the time counter

    private int motorMaxSpeedProcentage = 60;
    //private double turnSpeedProcentage = 0.35;
    private int turnSpeedProcentage = 25;
    //50% swings too much back and fort, 25 is okay, just stop, 40 is also all right

    private int offsetXobstacle = 670; //length of obstacle
    private int offsetYobstacle = 1600;


    public Line(Hardware hardware) {
        super(hardware);


        beaconColor.add(hardware.red);
        beaconColor.add(hardware.dRed);

        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

    @Override
    public void run(){
        hardware.led(9);
        hardware.servoGoUp();


        while (! hardware.isOnMidpointBW()){
            System.out.println("Put me on between white and black!");
            hardware.beep();
            mySleep(delay);
        }
        resetTimer();

        //we are on the middle
        while(running){
            long now = System.currentTimeMillis();
            long diff = now - lastReset;

            hardware.led(7);

            while(hardware.isTouchPressed()){
                System.out.println("Touch is pressed, cannot go forward");
                goAroundObstacle();
                resetTimer();
            }

            hardware.motorSetSpeedProcentage(getSpeed(diff));
            hardware.motorForward(step);

            initialRotationAngle = hardware.getAngle();  //resetting the variable with how much we've turned
            while(! hardware.isOnMidpointBW() && running){
                hardware.led(8);
                System.out.println("are initial rot. angle:  " + initialRotationAngle);
                System.out.println("Not in middle, trying to rotate, colorIntensity:" + hardware.readColorIntensity());
                rotateToMiddle();
                resetTimer();
            }

            System.out.println("Resetting init rot: " + initialRotationAngle);
        }
    }

    private void rotateToMiddle() {

        float correction =  ( Kp * ( hardware.getMidPointBW() - hardware.readColorIntensity() ) );
        //always round to the bigger number, lower possibility of getting 0
        int toTurn = (int) Math.ceil(correction * turningAngle) + ( correction < 0 ? -1 : 1) ;

        int currentGyroAngle = hardware.getAngle();
        int angleDiff = initialRotationAngle - currentGyroAngle;

        System.out.println("current gyro angle:" + currentGyroAngle);
        System.out.print(" correction:" + correction + ", with angle toTurn:"+ toTurn + ".....");
        System.out.print("current angle:"+ angleDiff +"...");

        if( Math.abs(angleDiff) > 80 ){
            System.out.println("Nope >80, probably end of the line!?!?");
            int estimateOrientation = hardware.estimateOrientation();
            int toGoBack = angleDiff;//estimateOrientation - currentGyroAngle;

            System.out.println("angleDiff is " + angleDiff + ", estimatedOrientantaion: " + estimateOrientation);
            System.out.println("Then, we have to turn " + toGoBack);

            hardware.robotTurn(toGoBack);

            zigZagMovements();
            resetTimer();
            return;
        }
        hardware.robotTurn( -toTurn );
    }

    /**
     * calculates the speed by
     * @param diff, delta of the time
     * @return the speed, in procent
     */
    private int getSpeed(long diff){
        //was 7!
        double accel = 7;
        double minimumOffset = 3; //should be smaller than 8

        diff = Math.round(accel * diff);
        double value = 1.0/  (1.0  +  Math.exp(-((((double) diff)/1000.0) - 8.0 + minimumOffset))) ;

        return (int) Math.round( value*100 );
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

            mySleep(50);
            if(hardware.isOnMidpointBW()){
                break;
            }

            mySleep(delay);
        }

        //following turns
        outer:
        while(! hardware.isOnMidpointBW()){
            System.out.println("Searching white line...");

            hardware.robotTurnNonBlockOneWheel(angle);
            while(hardware.motorsAreMoving()) {

                mySleep(50);
                if(hardware.isOnMidpointBW()){
                    break outer;
                }

                mySleep(delay);
            }

            angle = angle*(-1);
        }


        //we are on midpoint
        hardware.motorsWaitStopMoving();
        System.out.println("Found mid point!");
        //rotate right, black is reached
        hardware.robotTurn(90);
        while( ! hardware.isOnBlack() ){
            mySleep(delay);
        }

    }



    private void goAroundObstacle() {
        int motorSpeed = 70;

        hardware.motorSetSpeedProcentage(motorSpeed);

        System.out.println("going a little bit back");
        hardware.motorForwardBlock(-180);

        mySleep(50);

        //rotate right
        System.out.println("turning right...");
        hardware.robotTurnBlock(90);

        mySleep(50);


        hardware.motorSetSpeedProcentage(motorSpeed);
        mySleep(50);
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

        hardware.motorsWaitStopMoving();
        hardware.motorStop();
        hardware.motorSetSpeedProcentage(15);

        System.out.println("Searching the line ...");
        while(!hardware.isOnWhite() && !hardware.isOnMidpointBW()){
            hardware.motorForward(step);
        }


        hardware.motorStop();
        initialRotationAngle = hardware.getAngle();

        resetTimer();
    }


    private void resetTimer(){
        lastReset = System.currentTimeMillis();
    }


}
