package bulldozer.main;

public abstract class Brains{

    protected boolean running;
    protected Hardware hardware;


    public Brains(Hardware hardware){
        running = false;

        //checking if the hardware is initialized properly
        if(hardware != null || hardware.isInit() ){
            this.hardware = hardware;
        }


       /*
        hardware.robotTurn(540);

        mySleep(5000);


        hardware.robotTurn(-540);


        hardware.robotTurn(550);*/
    }



    protected abstract int doLogic();

    protected void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

