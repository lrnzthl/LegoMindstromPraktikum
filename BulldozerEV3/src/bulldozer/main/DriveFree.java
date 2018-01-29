package bulldozer.main;

public class DriveFree extends Brains {
    private int motorMaxSpeedProcentage = 10;
    //default value is 6000
    private int motorAccelaration = 6000;
    private int turnSpeedProcentage = 10;

    private int distanceFromWall = 12;
    private int offset = 5;

    public DriveFree(Hardware hardware) {
        super(hardware);

        beaconColor.add(hardware.blue);

        this.setSearchForBeacon(false);
        
        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setMotorAccelaration(motorAccelaration);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

	@Override
	public void run() {

        System.out.println("Strarting drive free");
		hardware.led(9);
        hardware.servoGoUp();

        hardware.motorForwardBlock(360);

        int firstSample = hardware.getDistance();
        int secondSample = hardware.getDistance();
        
        hardware.motorStop();

        System.out.println("Search for wall...");
        while(firstSample > 30){
        	hardware.motorSetSpeedProcentage(20);
        	hardware.motorForwardBlock(25);
        	firstSample = hardware.getDistance();
        	System.out.println("Current distance: " + firstSample);
        }

        System.out.println("Found wall at distance of: " + firstSample);

        System.out.println("Try to adjust...");
        do {
        	firstSample = hardware.getDistance();
        	hardware.robotTurnBlock(5);
        	secondSample = hardware.getDistance();
        } while (firstSample - secondSample > 0 );

        System.out.println("Adjusted!");
        hardware.robotTurnBlock(-5);
        mySleep(50);


        System.out.println("Search the beacon...");
        this.setSearchForBeacon(true);


        while(running){
            getBackToDistance();

        	hardware.motorSetSpeedProcentage(40);
            hardware.motorForward(45);

        }
	}


    private void getBackToDistance(){
        while( Math.abs(hardware.getDistance() - distanceFromWall) > offset ){
            System.out.println("Correcting distance");
            int current = hardware.getDistance();
            System.out.println("... distance is " + current + " should be " + distanceFromWall);

            mySleep(50);
            if(current < distanceFromWall){
                System.out.println("going right");
                hardware.robotTurnBlock(10);
            }else{
                //current > distanceFromWall
                System.out.println("going left");
                hardware.robotTurnBlock(-10);
            }

            mySleep(50);
        }
    }


}


