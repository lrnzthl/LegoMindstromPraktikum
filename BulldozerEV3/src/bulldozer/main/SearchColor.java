package bulldozer.main;


public class SearchColor extends Brains{

    private final int step = 45;


    private boolean foundRed = false;
    private boolean foundWhite = false;

    private boolean stop = false;
    private long lastReset = 0;

    // 1 for the last rotation to right, -1 for last rotation to left
    private int lastRotation = 1;
    
    private int speedProcentage = 60;
    private int turningSpeed = 10;

    private int correctionAngle = 20;

    private int expectedDistance;
    private int distanceTolerance = 2;
    
	public SearchColor(Hardware hardware){
        super(hardware); 
        this.setSearchForBeacon(false);

        beaconColor.add(hardware.red);
        beaconColor.add(hardware.white);

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

		hardware.robotTurnBlock(35);

		hardware.servoGoUp();

		expectedDistance = hardware.getDistance();
		System.out.println("The distance is : " + expectedDistance);

		this.setSearchForBeacon(true);
		while( running )  {
		        rotateToDistance();
	                

				while(hardware.isTouchPressed()){
	                System.out.println("Touch is pressed, cannot go forward");
	                hardware.motorForwardBlock(80); //run into the wall
	                rotateInTheWall();
	                expectedDistance = hardware.getDistance();
	                if(!running) break;
	            }

				if(!running) break;

				//going forward
	            //mySleep(50);
				hardware.motorSetSpeedProcentage(speedProcentage);
				mySleep(50);

				if(!stop){
                    hardware.motorForward(step);
				}


                //tryFindBeacon();
		}
		
	}
	
	//Rotates if it´s getting further away or closer of the wall.
	public void rotateToDistance() {

		
		while(hardware.getDistance() > (expectedDistance + distanceTolerance) || hardware.getDistance() < (expectedDistance - distanceTolerance)){

            System.out.println("Error in the distance, correcting");
            System.out.println("current:" + hardware.getDistance() + ", expected:" + expectedDistance);

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
	private void rotateInTheWall() {
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

    @Override
    protected boolean checkForBeacon(){
        long current = System.currentTimeMillis();

        if((current - lastReset) > 1000){
            stop = false;
        }

        if(!running){
            System.out.println("not running in check for beacon");
            return true;
        }

        if(hardware.isOnWhite() && !foundWhite){
            System.out.println("found white!");
            foundWhite = true;

            stop = true;
            lastReset = System.currentTimeMillis();

            hardware.beep();
            hardware.beep();
        }

        if(hardware.isOnRed() && !foundRed){
            System.out.println("found red!");
            foundRed = true;

            stop = true;
            lastReset = System.currentTimeMillis();

            hardware.beep();
            hardware.beep();
        }

        if(foundWhite && foundRed){
            System.out.println("Both found");
            return true;
        }


        return false;
    }


    private void tryFindBeacon(){


        if(hardware.isOnRed() && !hardware.isOnWhite()) {

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

            if ((hardware.isOnWhite() && foundWhite) || (hardware.isOnRed() && foundRed)) {
                hardware.motorForwardBlock(360);
            }

            if (foundRed && foundWhite) {
                running = false;
            }
        }
    }
	
	
}
