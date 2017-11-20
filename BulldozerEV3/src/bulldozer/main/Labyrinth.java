package bulldozer.main;

public class Labyrinth extends Brains {

    private final int delay = 30; //ms

    public Labyrinth(Hardware hardware){
        super(hardware);
    }

    @Override
    public void run () {

        hardware.servoGoDown();
        hardware.servoGoUp();

        while(running){


            while(hardware.isTouchPressed()){
                hardware.beep();
                System.out.println("Touch is pressed, cannot go forward");
                mySleep(delay);
            }



            float[] rgb = hardware.readRGBColor();
            if(rgb == null){
                System.out.println("rgb is null");
            }

            //System.out.println("color:  "+ rgb[0] + "..." + rgb[1] + "..." + rgb[2]);

            //System.out.println("Ultra:"+hardware.getDistance());
            System.out.println("Angle:"+hardware.getAngle());
        }


    }


    private void goingUntilWhiteLineIsLost(){
        //start going
        System.out.println("...going forward");
        hardware.motorForward();

        while(hardware.isOnWhite()){
            mySleep(100);
        }

        System.out.println("I don't see the white line");

        //robot is no more on the white line
        hardware.motorStop();
    }




}
