package bulldozer.main;

public class Labyrinth extends Brains {


    public Labyrinth(Hardware hardware){
        super(hardware);
    }

    @Override
    public void run () {
        System.out.println("I .. Line Brains");
        int round  = 30000;

        while(running){
            float[] rgb = hardware.readRGBColor();
            if(rgb == null){
                System.out.println("rgb is null");
            }

            System.out.println("color"+ rgb[0] + " color2: " + rgb[1]);
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
