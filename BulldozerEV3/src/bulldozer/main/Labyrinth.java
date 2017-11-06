package bulldozer.main;

public class Labyrinth extends Brains {


    public Labyrinth(Hardware hardware){
        super(hardware);
    }

    @Override
    protected int doLogic() {
        System.out.println("I .. Line Brains");
        int round  = 30000;


        while(round>0) {
            System.out.println("Here...");

            while (hardware.isOnWhite() || !hardware.isTouchPressed() ) {

                hardware.motorForwardBlock(90);
                // hardware.motorMoveForwardMs(50);
            }

            System.out.println("I am out!");
            round--;
        }

        return 0;


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
