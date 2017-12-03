package bulldozer.main;

public class Bridge extends Brains {

    private int motorMaxSpeedProcentage = 10;
    //default value is 6000
    private int motorAccelaration = 6000;
    private int turnSpeedProcentage = 40;
    //0.5 is too much swings back and fort, 0.25 is okay, just stop, 0.4 is also all right


    public Bridge(Hardware hardware) {
        super(hardware);
        beaconColor = new CColor(0.306f,0.071f,0.215f); //red
        hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
        hardware.setMotorAccelaration(motorAccelaration);
        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
    }


    @Override
    public void run() {
        hardware.led(9);
        hardware.servoGoUp();


        while(running) {

            System.out.println(hardware.getAngle());
            mySleep(20);
        }

    }

}
