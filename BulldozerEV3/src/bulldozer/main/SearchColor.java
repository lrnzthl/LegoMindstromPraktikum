package bulldozer.main;


public class SearchColor extends Brains{

	private int motorMaxSpeedProcentage = 30;
    private int turnSpeedProcentage = 20;
    
    private final int step = 45;
    
    private boolean foundRed = false;
    private boolean foundWhite = false;
    
    // 1 for the last rotation to right, -1 for last rotation to left
    private int lastRotation = 1;
    
	public SearchColor(Hardware hardware){
        super(hardware);
        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//The distance expected between the robot and the wall
		int expectedDistance = hardware.getDistance();
		
		while(!(foundRed && foundWhite)) {
			//TODO if necessary , rotate the gyro/distance 
			
			
			while(!hardware.isOnRed() && !hardware.isOnWhite()) {
				
				while(hardware.isTouchPressed()){
	                System.out.println("Touch is pressed, cannot go forward");
	                rotateInTheWall();
	            }
				
				while(!(hardware.getDistance() == expectedDistance)){
	                returnToDistance();
	            }
				
				hardware.motorSetSpeedProcentage(10);
	            hardware.motorForward(step);
	            System.out.println("Searching the colors ...");
	            
	            
	            
			}
			
			
			if (hardware.isOnRed()) {
				foundRed = true;
			}
			else if (hardware.isOnWhite()) {
				foundWhite = true;
			}
		}
		
	}
	
	//Rotates if it´s getting further away or closer of the wall.
	public void returnToDistance() {
		
	}
	
	//Rotates at the end of the wall, to the right or left depending on the lastRotation
	public void rotateInTheWall() {
		hardware.motorForwardBlock(-180);
		
		if (lastRotation > 0) {
			System.out.println("turning left...");
			
			hardware.robotTurnBlock(-90);
			hardware.motorForward(180);
	        hardware.robotTurnBlock(-90);
	        
			lastRotation = -1;
		}
		else {
			System.out.println("turning right...");
			
	        hardware.robotTurnBlock(90);
	        hardware.motorForward(180);
	        hardware.robotTurnBlock(90);
	        
	        lastRotation = 1;
		}
	}
	
	
}
