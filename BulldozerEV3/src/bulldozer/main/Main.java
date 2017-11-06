package bulldozer.main;

public class Main {
    public static void main (String [] args){
        Hardware hardware = new Hardware();
        hardware.startSensors();
        Menu menu = new Menu(hardware);
        
        menu.start();
    }
}
