package bulldozer.main;


import com.sun.istack.internal.Interned;
import com.sun.org.apache.xpath.internal.SourceTree;

public class Line extends Brains {


    private int lastCurveDegrees;
    private Direction lastDirection;


    public Line(Hardware hardware) {
        super(hardware);

        lastCurveDegrees = 0;
        lastDirection = Direction.LEFT;




    }

    private enum Direction{
        RIGHT, LEFT
    }



    @Override
    protected void doLogic(){
        System.out.println("Going on the line");

        //we should be on the white line
        while( ! hardware.isOnWhite()){
            System.out.println("We are not on the white line!");
            hardware.beep();
            mySleep(100);
        }






    }




}
