package bulldozer.main;

public class Sensors extends Thread{
    private boolean isInited;

    private int sensorDelay ;


    private SingleValueSensorWrapper touch;
    private SingleValueSensorWrapper Scolor;
    private SingleValueSensorWrapper Sdistance;





    public Sensors(int sensorReadyDelay, SingleValueSensorWrapper touch, SingleValueSensorWrapper col, SingleValueSensorWrapper dist){
        isInited = false;

        this.sensorDelay = sensorReadyDelay;


        if( touch == null || col == null || dist == null){
            System.out.println("WARNING: Sensors are null");
        }
        this.touch = touch;
        this.Scolor = col;
        this.Sdistance = dist;

    }

    public boolean isInit() {
        return isInited;
    }


    /**
     * calibrates senosrs
     * @return
     */
    public boolean initialize(){
        //init and calib sensors

        //get some values from the sensors, they should be checked, that are valid


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



    public SingleValueSensorWrapper touch() {
        return touch;
    }

    public SingleValueSensorWrapper color() {
        return Scolor;
    }

    public SingleValueSensorWrapper distance() {
        return Sdistance;
    }
}
