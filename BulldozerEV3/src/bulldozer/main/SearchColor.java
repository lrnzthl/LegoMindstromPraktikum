package bulldozer.main;


public class SearchColor extends Brains{

	
    
    private final int step = 45;


    private boolean foundRed = false;
    private boolean foundWhite = false;
    
    // 1 for the last rotation to right, -1 for last rotation to left
    private int lastRotation = 1;
    
    private int speedProcentage = 50;

    private int expectedDistance;
    private int distanceTolerance = 3;
    
	public SearchColor(Hardware hardware){
        super(hardware); 
        this.setSearchForBeacon(false);
    }

	@Override
	public void run() {

	    /*
        int number = 100000000;
		hardware.servoGoDown();
        while(number>0){
            System.out.println("Angle is " + hardware.getAngle());
            System.out.println("color is " + hardware.readRGBColor());
            number--;
            mySleep(20);
        }*/

		hardware.motorForwardBlock(360);


		hardware.robotTurnBlock(-17);
		hardware.motorForwardBlock(500);
		hardware.robotTurnBlock(17);
		

		//The distance expected between the robot and the wall
		hardware.servoGoUp();
		expectedDistance = hardware.getDistance();
		System.out.println("The distance is : " + expectedDistance);
		
		while( running || (!foundRed && !foundWhite ) )  {
			
			while(!hardware.isOnRed() && !hardware.isOnWhite()) {
				while(hardware.getDistance() > expectedDistance + distanceTolerance || hardware.getDistance() < expectedDistance - distanceTolerance){
					System.out.println("Error in the distance, correcting");
	                rotateToDistance();
	            }
				while(hardware.isTouchPressed()){
	                System.out.println("Touch is pressed, cannot go forward");
	                rotateInTheWall();
	                expectedDistance = hardware.getDistance();
	            }

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
		}
		
	}
	
	//Rotates if it´s getting further away or closer of the wall.
	public void rotateToDistance() {
		
		while(hardware.getDistance() > (expectedDistance + distanceTolerance) || hardware.getDistance() < (expectedDistance - distanceTolerance)){
			hardware.motorSetSpeedProcentage(5); 
			if (hardware.getDistance() > expectedDistance ) {
				//hardware.rotateRightMotorBlock(5);
			    hardware.rotateLeftMotorBlock(-10);
			}
			else {
				//hardware.rotateLeftMotorBlock(5);
                hardware.rotateRightMotorBlock(-10);
			}
			
		}
	}
	
	
	//Rotates at the end of the wall, to the right or left depending on the lastRotation
	public void rotateInTheWall() {
		hardware.motorForwardBlock(-180);
		
	    System.out.println("Turning...");
	    mySleep(50);

	    if(lastRotation < 0){
            hardware.robotTurnBlock(lastRotation * -87);
            mySleep(50);
            hardware.motorForwardBlock(180);
            mySleep(50);
            hardware.robotTurnBlock(lastRotation * -87);

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
