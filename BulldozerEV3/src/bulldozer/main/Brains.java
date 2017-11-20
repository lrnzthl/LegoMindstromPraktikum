package bulldozer.main;

public abstract class Brains extends Thread{

    protected boolean running;
    protected Hardware hardware;

    private final int delay = 20; //ms

    protected int returnValue = -1;

    public Brains(Hardware hardware){
        running = false;

        //checking if the hardware is initialized properly
        if(hardware != null || hardware.isInit() ){
            this.hardware = hardware;
        }



    }


    public int mainLoop() throws InterruptedException{


        running = true;




        this.start();




        while(true){

            if(! hardware.isEscapeUp() || ! hardware.isLeftUp()){
                System.out.println("Program is from button terminated");
                returnValue = -1;
                running = false;
                break;
            }

            if(hardware.foundBeacon(new float[]{-1.f, -1.f, -1.f}, -1)){
                System.out.println("Beacon is found");
                returnValue = 1;
                running = false;
                break;
            }


            mySleep(delay);
        }



        return returnValue;
    }


    public abstract void run();

    protected void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

