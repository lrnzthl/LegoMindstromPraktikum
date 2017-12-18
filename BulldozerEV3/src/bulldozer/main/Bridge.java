package bulldozer.main;


public class Bridge extends Brains {

    private int motorMaxSpeedProcentage = 10;
    //default value is 6000
    private int motorAccelaration = 6000;
    private int turnSpeedProcentage = 15;
    
    private int blacksFound = 0;
    //0.5 is too much swings back and fort, 0.25 is okay, just stop, 0.4 is also all right
    
    public Bridge(Hardware hardware) {
        super(hardware);

        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setMotorAccelaration(motorAccelaration);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

    @Override
    public void run() {
        hardware.led(9);
        hardware.servoGoDown();
        long last = System.currentTimeMillis();
        while(running) {
        	if(hardware.getDistance() > 10){
        		hardware.motorStop();
        		hardware.setTurnSpeedProcentage(turnSpeedProcentage);
        		hardware.rotateRightMotorBlocking(-20);
        		last = System.currentTimeMillis();
        	} else {
        		hardware.setTurnSpeedProcentage(turnSpeedProcentage);
            	hardware.motorForward(45);
        	}
        	
        	if(System.currentTimeMillis() - last > 2000){
        		hardware.motorStop();
        		hardware.setTurnSpeedProcentage(turnSpeedProcentage);
        		hardware.rotateLeftMotorBlocking(-20);
        		last = System.currentTimeMillis();
        	}
        	
        	if(hardware.readColorIntensity() < 0.01f){
        		if(blacksFound == 0) {
        			hardware.motorStop();
            		hardware.setTurnSpeedProcentage(turnSpeedProcentage);
            		hardware.motorForward(90);
            		blacksFound++;
        		} else {
        			hardware.motorStop();
            		hardware.setTurnSpeedProcentage(turnSpeedProcentage);
            		hardware.motorForward(-90);
            		hardware.robotTurnBlock(-90);
        		}
        	}
            mySleep(20);
        }
    }
}
