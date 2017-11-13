package bulldozer.main;

public class Labyrinth extends Brains {


    public Labyrinth(Hardware hardware){
        super(hardware);
    }

    @Override
    public void run () {
        System.out.println("I .. Line Brains");
        int round  = 30000;


        //while(! hardware.getButtonType().equals(Button.DOWN)){
            hardware.robotTurn(485);
        //}



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
