package bulldozer.main;

public class DriveFree extends Brains {
    private int motorMaxSpeedProcentage = 10;
    //default value is 6000
    private int motorAccelaration = 6000;
    private int turnSpeedProcentage = 10;
	
    public DriveFree(Hardware hardware) {
        super(hardware);

        beaconColor.add(hardware.blue);
        
        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setMotorAccelaration(motorAccelaration);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

	@Override
	public void run() {
		hardware.led(9);
        hardware.servoGoUp();
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
        
        System.out.println("Search the beacon...");
        while(true){
        	hardware.motorSetSpeedProcentage(40);
            hardware.motorForward(45);
        }
	}
}
