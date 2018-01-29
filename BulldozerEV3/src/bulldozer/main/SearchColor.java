package bulldozer.main;


public class SearchColor extends Brains{

	
    
    private final int step = 45;


    private boolean foundRed = false;
    private boolean foundWhite = false;
    
    // 1 for the last rotation to right, -1 for last rotation to left
    private int lastRotation = 1;
    
    private int speedProcentage = 60;
    private int turningSpeed = 10;

    private int correctionAngle = 15;

    private int expectedDistance;
    private int distanceTolerance = 2;
    
	public SearchColor(Hardware hardware){
        super(hardware); 
        this.setSearchForBeacon(false);
    }

	@Override
	public void run() {

        System.out.println("Trying to adjust");

        /*
        int distance = hardware.getDistance();
        while(distance > 5){
            hardware.rotateRightMotorBlock(20);
            System.out.println("Distance is " + distance);
            distance = hardware.getDistance();
        }*/

        hardware.motorForwardBlock(360);
        mySleep(50);
        
		hardware.robotTurnBlock(-40);
		mySleep(50);

		hardware.motorForwardBlock(360);
		mySleep(50);

		hardware.robotTurnBlock(40);

		hardware.servoGoUp();

		expectedDistance = hardware.getDistance();
		System.out.println("The distance is : " + expectedDistance);
		
		while( running )  {
			
			while(!hardware.isOnRed() && !hardware.isOnWhite()) {
				while(hardware.getDistance() > expectedDistance + distanceTolerance || hardware.getDistance() < expectedDistance - distanceTolerance){
					System.out.println("Error in the distance, correcting");
	                rotateToDistance();
	                
	                if(!running) break;
	            }
				while(hardware.isTouchPressed()){
	                System.out.println("Touch is pressed, cannot go forward");
	                rotateInTheWall();
	                expectedDistance = hardware.getDistance();
	                if(!running) break;
	            }
				if(!running) break;
	            mySleep(50);
				hardware.motorSetSpeedProcentage(speedProcentage);
				mySleep(50);
	            hardware.motorForward(step);
			}

            System.out.println("I see a color!");
			
			if (hardware.isOnRed() && !foundRed) {
				System.out.println("FOUND THE RED");
				foundRed = true;
				hardware.beep();
				mySleep(1000);
				hardware.motorForwardBlock(360);
			}

			if (hardware.isOnWhite() && !foundWhite) {
				System.out.println("FOUND THE WHITE");
				foundWhite = true;
				hardware.beep();
				mySleep(1000);
                hardware.motorForwardBlock(360);
			}

			if(foundRed && foundWhite){
			    running = false;
            }
		}
		
	}
	
	//Rotates if it´s getting further away or closer of the wall.
	public void rotateToDistance() {
		
		while(hardware.getDistance() > (expectedDistance + distanceTolerance) || hardware.getDistance() < (expectedDistance - distanceTolerance)){
			hardware.motorSetSpeedProcentage(turningSpeed);
			
			if(!running ){
				break;
			}
			
			if (hardware.getDistance() > expectedDistance ) {
				//hardware.rotateRightMotorBlock(5);
			    hardware.rotateLeftMotorBlock(-correctionAngle);
			}
			else {
				//hardware.rotateLeftMotorBlock(5);
                hardware.rotateRightMotorBlock(-correctionAngle);
			}
			
		}
	}
	
	
	//Rotates at the end of the wall, to the right or left depending on the lastRotation
	public void rotateInTheWall() {
		hardware.motorForwardBlock(-180);
		
	    System.out.println("Turning...");
	    mySleep(50);

	    if(lastRotation < 0){
            hardware.robotTurnBlock(lastRotation * -88);
            mySleep(50);
            hardware.motorForwardBlock(150);
            mySleep(50);
            hardware.robotTurnBlock(lastRotation * -88);



        }else{
            hardware.robotTurnBlock(lastRotation * -90);
            mySleep(50);
            hardware.motorForwardBlock(180);
            mySleep(50);
            hardware.robotTurnBlock(lastRotation * -90);
        }

        lastRotation *= -1;
        mySleep(50);
    }
	
	
}
