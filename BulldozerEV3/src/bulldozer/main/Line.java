package bulldozer.main;


import com.sun.istack.internal.Interned;
import com.sun.org.apache.xpath.internal.SourceTree;

public class Line extends Brains {


    private int currentAngle;
    private Direction lastDirection;


    public Line(Hardware hardware) {
        super(hardware);

        currentAngle = 0;
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



        while(true){
            go();
        }



    }


    private void go(){
        do {
            hardware.motorMoveForwardMs(20);
        }
        while(hardware.isOnWhite());


        turnAndFindTheWhiteLine();

    }



    private void changeDirection(){

        System.out.println("Changing direction");

        if(lastDirection == Direction.LEFT){
            lastDirection = Direction.RIGHT;
            hardware.motorTurn(90);

        }else{
            lastDirection = Direction.LEFT;
            hardware.motorTurn(-90);
        }


        currentAngle = 0;
    }


    private void turnAndFindTheWhiteLine(){
        boolean alreadyTurned = false;

        do {

            currentAngle = currentAngle + 5;


            if (currentAngle > 90) {
                //change direction

                if(alreadyTurned == false){
                    changeDirection();
                    alreadyTurned = true;
                }else {
                    System.out.println("Already turned two times and cannot find white line :(");
                }



            }

            if (lastDirection == Direction.LEFT) {
                hardware.motorTurn(-5);
            } else {
                hardware.motorTurn(5);
            }

        }while (!hardware.isOnWhite());

    }



}
