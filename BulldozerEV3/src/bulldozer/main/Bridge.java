package bulldozer.main;

import java.util.concurrent.ThreadLocalRandom;

public class Bridge extends Brains {

    //private int motorMaxSpeedProcentage = 10;
    //default value is 6000
    //private int motorAccelaration = 6000;
    private int turnSpeedProcentage = 20;
    private int safeForwardSpeed = 30;
    private int initialAngle;

    int odd = -1;


    public Bridge(Hardware hardware) {
        super(hardware);

        this.setSearchForBeacon(false);

        beaconColor.add(hardware.blue);
		//beaconColor.add(hardware.blueblack);

        //hardware.setMotorMaxSpeedProcentage(motorMaxSpeedProcentage);
       //hardware.setMotorAccelaration(motorAccelaration);

        hardware.setTurnSpeedProcentage(turnSpeedProcentage);
        hardware.motorSetSpeedProcentage(safeForwardSpeed);
    }

    @Override
    public void run() {
        hardware.led(9);


        System.out.println("trying to find the wall");
        hardware.servoGoUp();
        int oldTurnSpeed = turnSpeedProcentage;
        int newTurnSpeed = 5;
        hardware.setTurnSpeedProcentage(newTurnSpeed);

        while(hardware.getDistance() > 15){
            hardware.rotateLeftMotor(10);
            //hardware.robotTurn(10);
            mySleep(50);
        }

        System.out.println("found wall");

        hardware.motorStop();
        hardware.setTurnSpeedProcentage(oldTurnSpeed);
        mySleep(50);


        hardware.motorForward(90);


        //going up the ramp
        hardware.servoGoDown();

        initialAngle = hardware.getAngle();

        long last = System.currentTimeMillis();
		System.out.println("Running");
        while(running) {

        	CColor actualColor = hardware.readRGBColor();
			if( actualColor.getIntensity() < 0.001f  ){

				System.out.println("Nothing under color sensor");
				System.out.println(" Falling! Trying to safe");
				hardware.motorStop();
				hardware.motorSetSpeedProcentage(safeForwardSpeed);
				hardware.motorForwardBlock(-150);

				//mySleep(50); after robotTurnBlock not needed
				hardware.robotTurnBlock(-90);

				last = System.currentTimeMillis();
				this.setSearchForBeacon(true);

			}

			//hit a wall
			if(hardware.isTouchPressed()){
			    int turnAngle = 10;

			    hardware.servoGoUp();

			    mySleep(50);
				hardware.motorForwardBlock(-100);

				hardware.robotTurnBlock(turnAngle);


				hardware.motorForward(360);


				/*
				int randomAngle = ThreadLocalRandom.current().nextInt(1, 10 )*5;
				odd *= -1;
				randomAngle *= odd;
				System.out.println("generating random int " + randomAngle);
				mySleep(50);
				hardware.robotTurnBlock(randomAngle);
				-*/



				mySleep(50);
				hardware.servoGoDown();
				mySleep(50);
			}



        	if(hardware.getDistance() > 15){
				System.out.println("No ground found, trying to fix");
        		hardware.motorStop();
				mySleep(50);
				hardware.motorSetSpeedProcentage(safeForwardSpeed);
				hardware.motorForwardBlock(-150);

				hardware.motorStop();

				hardware.robotTurnBlock(15);
        		last = System.currentTimeMillis();
        	} else {
        		hardware.motorSetSpeedProcentage(safeForwardSpeed);
            	hardware.motorForward(45);
        	}

        	//after 2 sec
        	if(System.currentTimeMillis() - last > 2000){
        		hardware.motorStop();
				mySleep(50);
        		hardware.rotateLeftMotorBlock(-60);
        		last = System.currentTimeMillis();
        	}
        	

            mySleep(20);
        }
    }
}
