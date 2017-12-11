package bulldozer.main;


public class SearchColor extends Brains{

	
    
    private final int step = 45;
    
    private boolean foundRed = false;
    private boolean foundWhite = false;
    
    // 1 for the last rotation to right, -1 for last rotation to left
    private int lastRotation = 1;
    
    private int expectedDistance;
    
	public SearchColor(Hardware hardware){
        super(hardware);
        beaconColor = new CColor(0.54f, 0.16f, 0.10f); //should be blue
        
        
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//The distance expected between the robot and the wall
		expectedDistance = hardware.getDistance();
		
		while(!(foundRed && foundWhite)) {
			
			hardware.servoGoUp();
			
			while(!hardware.isOnRed() && !hardware.isOnWhite()) {
				
				while(hardware.isTouchPressed()){
	                System.out.println("Touch is pressed, cannot go forward");
	                rotateInTheWall();
	            }
				
				while(!(hardware.getDistance() == expectedDistance)){
	                rotateToDistance();
	            }
				
				hardware.motorSetSpeedProcentage(20);
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
	public void rotateToDistance() {
		
		while(hardware.getDistance() != expectedDistance) {
			
			if (hardware.getDistance() < expectedDistance) {
				//Rotate to right
			}
			else {
				//Rotate to left 
			}
		}
	}
	
	
	//Rotates at the end of the wall, to the right or left depending on the lastRotation
	public void rotateInTheWall() {
		hardware.motorForwardBlock(-180);
		
		if (lastRotation > 0) {
			System.out.println("turning left...");
			
			hardware.robotTurnBlock(-90);
			hardware.motorForward(180);
	        hardware.robotTurnBlock(-90);
	        
	        expectedDistance = hardware.getDistance();
	        
			lastRotation = -1;
		}
		else {
			System.out.println("turning right...");
			
	        hardware.robotTurnBlock(90);
	        hardware.motorForward(180);
	        hardware.robotTurnBlock(90);
	        
	        expectedDistance = hardware.getDistance();
	        
	        lastRotation = 1;
		}
	}
	
	
}
