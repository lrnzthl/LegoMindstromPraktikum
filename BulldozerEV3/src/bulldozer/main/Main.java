package bulldozer.main;

public class Main {
    public static void main (String [] args){

        Hardware hardware = new Hardware(true);
        Menu menu = new Menu(hardware);


        menu.start();


    }
}
