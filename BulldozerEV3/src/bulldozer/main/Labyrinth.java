package bulldozer.main;

import bulldozer.main.Hardware.actualColor;

public class Labyrinth extends Brains {

    private final int step = 45;
    private final int delay = 30; //ms
    private final float Kp = 2.5f;
    private int turningAngle = 10;
    private long lastReset;

    private int motorMaxSpeedProcentage = 40;
    private int turnSpeedProcentage = 20;

    
    public Labyrinth(Hardware hardware){
        super(hardware);

        this.setSearchForBeacon(false);

        beaconColor.add(hardware.blue);
       // beaconColor.add(hardware.blueblack);
       // beaconColor.add(hardware.bluewhite);


        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

    @Override
    public void run () {
    	mySleep(50);
        hardware.servoGoDown();
        mySleep(50);
        
    	//Find the labyrinth. 
    	//Drive the distance between the blue beacon line and the white labyrinth
        hardware.motorStop();
        mySleep(50);
        System.out.println("Searching the line ...");
        
    	while(!hardware.isOnWhite() && !hardware.isOnRed()){
    		hardware.motorSetSpeedProcentage(20);
            hardware.motorForward(step);

    	}
    	this.setSearchForBeacon(true);
    	
    	//Turn to be in an initial position on the line
        while( !hardware.isOnMidpointRed()  && running){
            System.out.println("Lost the midpoint");
            hardware.led(8);
            rotateToMiddle();
            lastReset = System.currentTimeMillis();
        }

        System.out.println("Began running");

        //On the line, start to solve the labyrinth
        resetTimer();
        while(running){
        	long now = System.currentTimeMillis();
        	long diff = now - lastReset;

            //hardware.motorSetSpeedProcentage(getSpeed(diff));
            hardware.motorForward(step);
            
            //alreadyTurned = hardware.getAngle();
            while( ! hardware.isOnMidpointRed() ){
                System.out.println("Lost the midpoint inside running");
                hardware.led(8);
                rotateToMiddle();
                lastReset = System.currentTimeMillis();
            }


        }

        hardware.led(0);
    }

    private void rotateToMiddle() {
        float correction = 0.f;

        CColor current = hardware.readRGBColor();

    	correction = ( Kp * ( hardware.getMidPointRed() - current.getRed() ) );
        int toTurn = (int) (Math.ceil(correction * turningAngle) + ( correction < 0 ? -1 : 1)) ;

        System.out.println("toTurn: " + toTurn);
        hardware.robotTurn( -toTurn );
    	
    } 
    

    /**
     * calculates the speed by
     * @param diff, delta of the time
     * @return the speed, in procent
     */
    private int getSpeed(long diff){

        double accel = 5;
        double minimumOffset = 3; //should be smaller than 8

        diff = Math.round(accel * diff);
        double value = 1.0/  (1.0  +  Math.exp(-((((double) diff)/1000.0) - 8.0 + minimumOffset))) ;

        return (int) Math.round( value*100 );
    }


    private void resetTimer(){
        lastReset = System.currentTimeMillis();
    }


}
