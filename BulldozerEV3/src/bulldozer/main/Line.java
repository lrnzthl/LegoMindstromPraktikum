package bulldozer.main;

import bulldozer.main.Hardware.ButtonType;

public class Line extends Brains {
    private int currentAngle;
	private int turnOffset;
    private Direction lastDirection;
    private State currentState;

    //rotaion for motors to go forward
    private final int step = 90;
    private final float Kp = 1.f;

    private final int delay = 30; //ms
    private float turningAngle = 10.f;
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

    private enum State{
        CALIBRATE, FOLLOWLINE, CORNER, GAP
    }
    
    protected int doLogic_new(){
    	int status = 0;
    	running = true;
        System.out.println("Going on the line");
        
        while(running){
        	switch (currentState) {
        		case CALIBRATE:
        			hardware.colorSensorCalibrate();
        	        if(hardware.getColorBlack() ==0 || hardware.getColorWhite() ==0){
        	            System.out.println("Colors not calibrated");
        	            status = -1;
        	            running = false;
        	        } else {
        	        	currentState = State.FOLLOWLINE;
        	        }
        		case FOLLOWLINE:
                    hardware.ledWhite();

                    System.out.println("Going forward for " + step);
                    hardware.motorForwardBlock(step);

                    float correction = ( Kp * ( hardware.getMidPoint() - hardware.readColor() ) );
                    int toTurn = (int) (correction * turningAngle);

                    if( alreadyTurned + toTurn > 90){
                        System.out.println("Nope >90, probably end of the line!?!?");
                        //go back alreadyTurned degrees to the right
                        
                        currentState = State.CORNER;
                    }
                    
                    alreadyTurned += toTurn;
                    
                    if(hardware.isOnWhite()){
                        //we must turn right
                        hardware.robotTurn(toTurn * right);
                    }else{
                        hardware.robotTurn(-toTurn * right );
                    }
        		case CORNER:
        			hardware.robotTurn(alreadyTurned * right);
        			alreadyTurned = 0;
        			currentState = State.GAP;
        		case GAP:
        		default:
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


        if(hardware.getColorBlack() ==0 || hardware.getColorWhite() ==0){
            System.out.println("Colors not calibrated");
            return -1;
        }

        while (! hardware.isOnMidpoint()){
            System.out.println("Put me on between white and black!");
            hardware.beep();
            mySleep(delay);
        }

        //we are on the middle
        while(running){
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

        float correction = (int) ( Kp * ( hardware.getMidPoint() - hardware.readColor() ) );
        int toTurn = Math.round(correction * turningAngle);

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
            hardware.robotTurn(toTurn * right);
        }else{
            hardware.robotTurn(-toTurn * right );
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
