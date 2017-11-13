package bulldozer.main;

public class Sensors extends Thread{
    private boolean isInited;

    private int sensorDelay ;


    private SingleValueSensorWrapper Stouch;
    private SingleValueSensorWrapper Scolor;
   // private SingleValueSensorWrapper Sdistance;


    private float touch, color;//, distance;


    public Sensors(int sensorReadyDelay, SingleValueSensorWrapper Stouch, SingleValueSensorWrapper Scolor){
        isInited = false;

        this.sensorDelay = sensorReadyDelay;


        if( Stouch == null || Scolor == null){
            System.out.println("WARNING: ONE of the sensors are null!");
        }


        this.Stouch = Stouch;
        this.Scolor = Scolor;
        //this.Sdistance = Sdist;

    }

    public boolean isInit() {
        return isInited;
    }


    /**
     * calibrates and initalizes senosrs
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
                this.touch = this.Stouch.getSample();
                this.color = this.Scolor.getSample();
               // this.distance = this.Sdistance.getSample();

                Thread.sleep(sensorDelay);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public float touch() {
        return touch;
    }

    public float color() {
        return color;
    }

    //public float distance() {
    //    return distance;
    //}
}
