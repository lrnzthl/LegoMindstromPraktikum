package bulldozer.main;

import lejos.hardware.Button;
import lejos.hardware.DeviceException;
import lejos.hardware.Sound;

public class Main {
    public static void main (String [] args) {
        try {


            Hardware hardware = new Hardware();
            hardware.startSensors();
            Menu menu = new Menu(hardware);

            menu.start();

            //lejos.hardware.DeviceException
        } catch (IllegalArgumentException e ){
            Sound.beepSequence();
            Button.LEDPattern(0);
            e.printStackTrace();
            System.exit(-1);
        }catch (DeviceException e2 ){
            Sound.beepSequence();
            Button.LEDPattern(0);
            e2.printStackTrace();
            System.exit(-1);
        }
    }
}
