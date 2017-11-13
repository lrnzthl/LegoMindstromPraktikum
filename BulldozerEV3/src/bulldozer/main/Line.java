package bulldozer.main;

import bulldozer.main.Hardware.ButtonType;

public class Line extends Brains {
    private int currentAngle;
	private int turnOffset;
    private Direction lastDirection;

    //rotaion for motors to go forward
    private final int step = 45;
    private final float Kp = (float) 1.5;

    private final int delay = 30; //ms
    private int turningAngle = 10;
    private int alreadyTurned = 0;

    //if we are going on the right of the line = 1
    //if we are going on the left side = -1
    private int right = 1;

    public Line(Hardware hardware) {
        super(hardware);

        currentAngle = 0;
        turnOffset = 30;
        lastDirection = Direction.LEFT;
    }

    private enum Direction{
        RIGHT, LEFT
    }


    protected int doLogic_old(){
    	running = true;
        System.out.println("Going on the line");
        
        //we should be on the white line
        while( ! hardware.isOnWhite()){
            System.out.println("We are not on the white line!");
            //hardware.beep();
            mySleep(100);
        }
        int status = 0;
        while(running){	
            if(!hardware.isOnWhite()) {
            	turnAndFindTheWhiteLine();
            }
            status = checkStillRunning();
        }
        return status;
    }
    
    private int checkStillRunning(){
    	int status = 0;
    	if(hardware.foundBeacon()){
    		running = false;
    		status =  1;
    	}
    	if(hardware.getButtonType() == ButtonType.ENTER){
    		running = false;
    		status = -1;
    	}
		return status;
    }

    private void changeDirection(){
        System.out.println("Changing direction");

        if(lastDirection == Direction.LEFT){
            lastDirection = Direction.RIGHT;
            hardware.robotTurn(520);

        }else{
            lastDirection = Direction.LEFT;
            hardware.robotTurn(-520);
        }
        currentAngle = 0;
    }

    private void turnAndFindTheWhiteLine(){
        boolean alreadyTurned = false;
        do {
            currentAngle = currentAngle + turnOffset ;
            if (currentAngle > 90) {
                //change direction

                if(alreadyTurned == false){
                    changeDirection();
                    alreadyTurned = true;
                }else {
                    System.out.println("Already turned two times and cannot find white line :(");
                }
            }
            if (lastDirection == Direction.LEFT) {
                hardware.robotTurn(turnOffset * -1);
            } else {
                hardware.robotTurn(turnOffset );
            }
        }while (!hardware.isOnWhite());
    }


    @Override
    public int doLogic(){
        System.out.println("Doing logic");

        if(hardware.getColorBlack() ==0 || hardware.getColorWhite() ==0){
            System.out.println("Colors not calibrated");
            return -1;
        }

        while (! hardware.isOnMidpoint()){
            System.out.println("Put me on between white and black!");
            hardware.beep();
            mySleep(delay);
        }

        System.out.println("Before while");
        running = true;

        //we are on the middle
        while(running){
            System.out.println("running..");

            hardware.ledWhite();

            System.out.println("Going forward for " + step);
            hardware.motorForwardBlock(step);

            alreadyTurned = 0; //resetting the variable with how much we've turned
            while(! hardware.isOnMidpoint()){
                rotateToMiddle();
            }

        }



        return 0;
    }

    private void rotateToMiddle() {
        System.out.println("Not in middle, trying to rotate");

        System.out.println("hard.getmidPoint " + hardware.getMidPoint());
        System.out.println("hard.getColor " + hardware.readColor());

        float correction =  ( Kp * ( hardware.getMidPoint() - hardware.readColor() ) );

        int toTurn = Math.round(-1.f * correction * turningAngle);

        System.out.println("toTurn="+toTurn);

        if( alreadyTurned + toTurn > 90){
            System.out.println("Nope >90, probably end of the line!?!?");
            //go back alreadyTurned degrees to the right
            hardware.robotTurn(alreadyTurned * right);

            //call function to handle that
            return;
        }


        alreadyTurned += toTurn;

        if(hardware.isOnWhite()){
            //we must turn right
            System.out.println("Going right!");
            hardware.robotTurn(toTurn );
        }else{
            System.out.println("Going left!");
            hardware.robotTurn(toTurn );
        }


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
