package bulldozer.main;

public class Main {
    public static void main (String [] args){
        Hardware hardware = new Hardware();
        Menu menu = new Menu(hardware);
        
        menu.start();
    }
}
