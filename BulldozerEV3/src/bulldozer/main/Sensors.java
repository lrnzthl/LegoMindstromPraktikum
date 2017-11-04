package bulldozer.main;

public class Sensors extends Thread{
    private boolean isInited;

    private int sensorDelay ;


    public Sensors(int sensorReadyDelay){
        isInited = false;


        this.sensorDelay = sensorReadyDelay;
    }

    public boolean isInit() {
        return isInited;
    }


    public boolean initialize(){
        //init sensors

        isInited = true;
        System.out.println("Sensors are initialized: " + isInited);
        return isInited;
    }



    public void run() {
        try {
            while (true) {
                System.out.println("Reading sensors...");
                //
                //
                //

                Thread.sleep(sensorDelay);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
