package bulldozer.main;

public abstract class Brains extends Thread{
    protected boolean running;
    protected Hardware hardware;

    private final int checkFinishedDelay = 20;



    public Brains(Hardware hardware){
        running = false;

        //checking if the hardware is initialized properly
        if(hardware != null || hardware.isInit() ){
            this.hardware = hardware;
        }

    }



    /**
     *
     * @return
     */
    public int mainLoop(){
        System.out.println("Starting Brains " + this.toString());

        //starting the sensors
        hardware.startSensors();
        this.start(); //starting the brains


        running = true;

        while(running){

            //checks if the escape button is pressed
            if (hardware.getButtonType().equals(Hardware.ButtonType.ESCAPE)){
                running = false;
                return -1;
            }

            //checks if we've finish the current challange
            if(hardware.foundBeacon()){
                return 1;
            }

            mySleep(checkFinishedDelay);
        }

        return 0;
    }


    public void run() {
        doLogic();
    }

    protected abstract void doLogic();





    private void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

