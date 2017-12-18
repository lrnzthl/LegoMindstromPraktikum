package bulldozer.main;

import lejos.hardware.Sound;

import java.util.LinkedList;

public abstract class Brains extends Thread{
    private final int delay = 20; //ms
    private boolean searchBeaconState = true;
	
    protected boolean running;
    protected Hardware hardware;
    protected LinkedList<CColor> beaconColor;
    protected int returnValue = -1;

    public Brains(Hardware hardware){
        running = false;

        beaconColor = new LinkedList<>();

        beaconColor.clear();

        //checking if the hardware is initialized properly
        if(hardware != null || hardware.isInit() ){
            this.hardware = hardware;
        }
    }

    public int mainLoop() throws InterruptedException{
        running = true;
        //starting thread
        this.start();
        
        while(true){
            if(! hardware.isEscapeUp() || ! hardware.isLeftUp()){
                System.out.println("Program is from button terminated");
                returnValue = -1;
                hardware.led(0);
                running = false;
                break;
            }

            if(hardware.foundBeacon(beaconColor) && searchBeaconState){
                System.out.println("Beacon is found");
                Sound.beepSequenceUp();
                returnValue = 1;
                running = false;
                break;
            }
            mySleep(delay);
        }
        return returnValue;
    }

    public abstract void run();

    protected void setSearchForBeacon(boolean state){
    	searchBeaconState = state;
    }
    
    protected void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

