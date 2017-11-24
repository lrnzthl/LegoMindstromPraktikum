package bulldozer.main;

public class Sensors extends Thread{
    private boolean isInited;
    private int sensorDelay;
    
    private SingleValueSensorWrapper Stouch;
    private SingleValueSensorWrapper Scolor;
    private SingleValueSensorWrapper Sdistance;
    private SingleValueSensorWrapper Sgyro;


    private float touch;//, distance;
    private OurColor color;




    public Sensors(int sensorReadyDelay, SingleValueSensorWrapper Stouch, 
    		SingleValueSensorWrapper Scolor, SingleValueSensorWrapper Sdistance, SingleValueSensorWrapper Sgyrosensor){
        isInited = false;
        this.sensorDelay = sensorReadyDelay;


        if( Stouch == null || Scolor == null || Sdistance ==null || Sgyrosensor == null){
            System.out.println("WARNING: ONE of the sensors are null!");
        }


        this.Stouch = Stouch;
        this.Scolor = Scolor;
        this.Sdistance = Sdistance;
        this.Sgyro = Sgyrosensor;
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
                this.color = new OurColor (this.Scolor.getSample(true));  //return rgb mode sample

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
    	
        //System.out.println("intenisty: "+intensity);
        return color.getIntensity();
    }
    /**
     * 
     * @return Returns Array with Index 0 = Red; 1 = Green; 2 = Blue.
     * Values are in the range [0,1].
     */
    public OurColor colorRGB() {    		
        return color;
    }

    public float getDistance(){return Sdistance.getSample();}

    public float getAngle(){return Sgyro.getSample();}
    
    //public float distance() {
    //    return distance;
    //}
}
