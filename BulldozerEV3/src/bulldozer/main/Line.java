package bulldozer.main;

public class Line extends Brains {



    public Line(Hardware hardware) {
        super(hardware);
    }

    @Override
    protected void doLogic() {
        System.out.println("I .. Line Brains");
    }

}
