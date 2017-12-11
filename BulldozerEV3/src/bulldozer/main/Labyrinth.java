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

       // beaconColor.add(hardware.blue);

        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }

    @Override
    public void run () {
        hardware.servoGoUp();

    	//Find the labyrinth. 
    	//Drive the distance between the blue beacon line and the white labyrinth
        System.out.println("Searching the line ...");
    	while(!hardware.isOnWhite() || !hardware.isOnRed()){
    		hardware.motorSetSpeedProcentage(10);
            hardware.motorForward(step);

    	}

    	//Turn to be in an initial position on the line
        while(! hardware.isOnMidpointBW() && ! hardware.isOnMidpointRB() ){
            System.out.println("Lost the midpoint");
            System.out.println(hardware.acColor);
            hardware.led(8);
            rotateToMiddle();
            lastReset = System.currentTimeMillis();
        }

        System.out.println("Began running");
        //On the line, start to solve the labyrinth
        while(running){
        	long now = System.currentTimeMillis();
        	long diff = now - lastReset;

            //hardware.motorSetSpeedProcentage(getSpeed(diff));
            hardware.motorForward(step);
            
            //alreadyTurned = hardware.getAngle();
            while( ! hardware.isOnMidpointRB() ){
                //System.out.println("Lost the midpoint inside running");
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

    	if ( hardware.acColor.equals(actualColor.BW)) {
    		System.out.println("Rotating to BW");
    		correction =  ( Kp * ( hardware.getMidPointBW() - hardware.readColorIntensity() ) );
        }
    	else {
    		System.out.println("Rotating to RB");
    		correction =  ( Kp * ( hardware.getMidPointRB() - hardware.readColorIntensity() ) );
    		//correction =  ( Kp * ( 0.256f - hardware.readColorIntensity() ) );
    		correction*= -1;
    	}
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
