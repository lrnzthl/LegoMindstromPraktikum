package bulldozer.main;

import bulldozer.main.Hardware.actualColor;

public class Labyrinth extends Brains {

    private final int step = 45;
    private final int delay = 30; //ms
    private final float Kp = 2.5f;
    private float turningAngle = 10.f;
    private long lastReset;

    private int motorMaxSpeedProcentage = 60;
    private int turnSpeedProcentage = 40;

    

   
    
    public Labyrinth(Hardware hardware){
        super(hardware);
        beaconColor = new CColor(0.54f, 0.16f, 0.10f); //should be blue
        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

    @Override
    public void run () {
        hardware.servoGoUp();

    	//Find the labyrinth. 
    	//Drive the distance between the blue beacon line and the white labyrinth

    
    	while(!hardware.isOnWhite() || !hardware.isOnRed()){
    		hardware.motorSetSpeedProcentage(10);
            hardware.motorForward(step);
            System.out.println("Searching the line ...");
    	}
    	
   
    	
    	
    	//Turn to be in an initial position on the line
        while(! hardware.isOnMidpointBW() && ! hardware.isOnMidpointRB() ){
        		System.out.println("Lost the midpoint");
            hardware.led(8);
            rotateToMiddle();
            lastReset = System.currentTimeMillis();
        }
        
        //On the line, start to solve the labyrinth
        while(running){
        		System.out.println("Began running");
        		long now = System.currentTimeMillis();
        		long diff = now - lastReset;

            hardware.motorSetSpeedProcentage(getSpeed(diff));
            hardware.motorForward(step);
            
            //alreadyTurned = hardware.getAngle();
            while(! hardware.isOnMidpointBW() && ! hardware.isOnMidpointRB() ){
            		System.out.println("Lost the midpoint ");
                hardware.led(8);
                rotateToMiddle();
                lastReset = System.currentTimeMillis();
            }
        }
    }

    private void rotateToMiddle() {
    		
    	if (hardware.acColor.equals(actualColor.BW)) {
    		System.out.println("Rotating to BW");
    		float correction =  ( Kp * ( hardware.getMidPointBW() - hardware.readColor() ) );
    		int toTurn = Math.round(correction * turningAngle) ;
    		hardware.robotTurn( -toTurn );
    }
    	else {
    		System.out.println("Rotating to RB");
    		float correction =  ( Kp * ( hardware.getMidPointRB() - hardware.readColor() ) );
    		int toTurn = Math.round(correction * turningAngle) ;
    		hardware.robotTurn( -toTurn );
    	}
    	
    } 
    

    /**
     * calculates the speed by
     * @param diff, delta of the time
     * @return the speed, in procent
     */
    private int getSpeed(long diff){

        double accel = 10;
        double minimumOffset = 3; //should be smaller than 8

        diff = Math.round(accel * diff);
        double value = 1.0/  (1.0  +  Math.exp(-((((double) diff)/1000.0) - 8.0 + minimumOffset))) ;

        return (int) Math.round( value*100 );
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
