package bulldozer.main;

public abstract class Brains {
    protected boolean running;
    protected Hardware hardware;


    public Brains(Hardware hardware){
        running = false;

        //checking if the hardware is initisalized properly
        if (hardware != null || hardware.isInit()) {
            this.hardware = hardware;
        }

    }



    /**
     *
     * @return
     */
    public int start(){
        System.out.println("Starting " + this.toString());

        running = true;

        while(running){
            doLogic();




            //checks if the escape button is pressed
            if (hardware.getButtonType().equals(Hardware.ButtonType.ESCAPE)){
                running = false;
                return -1;
            }

            //checks if we've finish the current challange
            if(hardware.foundBeacon()){
                return 1;
            }

            mySleep(20);
        }

        return 0;
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

