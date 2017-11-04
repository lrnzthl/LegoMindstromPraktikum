package bulldozer.main;

public enum ParcourState {
    MENU (0), LINE (1), LABYRINTH (2), BRIDGE (3), BARRIER (4);//, FINDCOLOUR (5);

    private final int id;

    ParcourState(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
