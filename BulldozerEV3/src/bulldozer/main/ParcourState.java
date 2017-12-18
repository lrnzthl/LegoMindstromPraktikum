package bulldozer.main;

public enum ParcourState {
    MENU (0), LINE (1), DRIVEFREE(2), LABYRINTH (3), BRIDGE (4), SEARCHCOLOR (5), ;//, FINDCOLOUR (5);

    private final int id;

    ParcourState(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
