package bulldozer.main;

public class Labyrinth extends Brains {

    private final int step = 45;
    private final int delay = 30; //ms
    private final float Kp = 2.5f;
    private float turningAngle = 10.f;
    private long lastReset;

    private int motorMaxSpeedProcentage = 60;
    //default value is 6000
    private int motorAccelaration = 6000;
    private double turnSpeedProcentage = 0.4;
    //0.5 is too much swings back and fort, 0.25 is okay, just stop, 0.4 is also all right

    public Labyrinth(Hardware hardware){
        super(hardware);
        beaconColor = new OurColor(0.54f, 0.16f, 0.10f); //should be blue
        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setMotorAccelaration(motorAccelaration);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

    @Override
    public void run () {
        hardware.servoGoUp();

    	//Find the labyrinth. 
    	//Drive the distance between the blue beacon line and the white labyrinth
    	while(!hardware.isOnWhite()){
    		hardware.motorSetSpeedProcentage(1.0);
            hardware.motorForwardBlock(step);
    	}
    	
    	//Turn to be in an initial position on the line
        while(! hardware.isOnMidpointBW()){
            hardware.led(8);
            rotateToMiddle();
            lastReset = System.currentTimeMillis();
        }
        
        //On the line, start to solve the lab
        while(running){        	
            long now = System.currentTimeMillis();
            long diff = now - lastReset;

            hardware.motorSetSpeedProcentage(getSpeed(diff));
            hardware.motorForwardBlock(step);
            
            //alreadyTurned = hardware.getAngle();
            while(! hardware.isOnMidpointBW()){
                hardware.led(8);
                rotateToMiddle();
                lastReset = System.currentTimeMillis();
            }
        }
    }

    private void rotateToMiddle() {
        float correction =  ( Kp * ( hardware.getMidPointBW() - hardware.readColor() ) );
        int toTurn = Math.round(correction * turningAngle) ;
        hardware.robotTurn( -toTurn );
    }
    
    private double getSpeed(long diff){
        double accel = 10;
        double minimumOffset = 3; //should be smaller than 8

        diff = Math.round(accel * diff);
        double value = 1.0/(1.0+Math.exp(-((((double) diff)/1000.0) - 8.0 + minimumOffset))) ;
        //return 40;
        return value;
    }

    private void goingUntilWhiteLineIsLost(){
        //start going
        System.out.println("...going forward");
        hardware.motorForward();

        while(hardware.isOnWhite()){
            mySleep(100);
        }

        System.out.println("I don't see the white line");

        //robot is no more on the white line
        hardware.motorStop();
    }
}
