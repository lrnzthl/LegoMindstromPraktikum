package bulldozer.main;

import bulldozer.main.Hardware.ButtonType;

public class Line extends Brains {
    private int currentAngle;
	private int turnOffset;
    private Direction lastDirection;

    public Line(Hardware hardware) {
        super(hardware);

        currentAngle = 0;
        turnOffset = 30;
        lastDirection = Direction.LEFT;
    }

    private enum Direction{
        RIGHT, LEFT
    }

    @Override
    protected int doLogic(){
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
            hardware.motorTurn(520);

        }else{
            lastDirection = Direction.LEFT;
            hardware.motorTurn(-520);
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
                hardware.motorTurn(turnOffset * -1);
            } else {
                hardware.motorTurn(turnOffset );
            }
        }while (!hardware.isOnWhite());
    }
}
