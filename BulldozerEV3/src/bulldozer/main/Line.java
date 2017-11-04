package bulldozer.main;

public class Line extends Brains {



    public Line(Hardware hardware) {
        super(hardware);
    }

    @Override
    protected void doLogic() {
        System.out.println("I .. Line Brains");

        while( ! hardware.isTouchPressed()){
            hardware.motorForwardBlock(360);
        }
    }

}
