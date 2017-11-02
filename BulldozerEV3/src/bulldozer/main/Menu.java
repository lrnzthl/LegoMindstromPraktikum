package bulldozer.main;


import sun.security.krb5.internal.PAData;

import java.io.IOException;

public class Menu {

    private ParcourState state;
    private ParcourState selectedState;

    private Hardware hardware;

    public Menu(Hardware hardware){

        //beginning state is always menu
        state = ParcourState.MENU;
        selectedState = ParcourState.values()[1];



        //checking if the hardware is initisalized properly
        if(hardware != null || hardware.isInit()){
            this.hardware = hardware;
        }


    }




    private void setState(ParcourState state){
        if (state != null){
            System.out.println("Changing state to " + state);
            this.state = state;
        }
    }

    public ParcourState getState() {
        return state;
    }


    public void start(){
        boolean running = true;

        while (running) {



            showOptions();

            switch (hardware.getButtonType()) {
                case LEFT:


                    int newSelectedState = selectedState.getId() - 1;

                    if (newSelectedState < 1) {
                        newSelectedState = ParcourState.values().length - 1;
                    }
                    selectedState = ParcourState.values()[newSelectedState];


                    break;


                case RIGHT:

                    newSelectedState = selectedState.getId() + 1;

                    if (newSelectedState > ParcourState.values().length - 1) {
                        newSelectedState = 1;
                    }
                    selectedState = ParcourState.values()[newSelectedState];


                    break;
                case ENTER:

                    break;
                case ESCAPE:
                    running = false;
                    break;
                default:

                    break;
            }
        }
    }



    private void showOptions(){


        for (ParcourState s : ParcourState.values()){
            if (s.equals(ParcourState.MENU)){
                continue;
            }


            if(s.equals(this.selectedState)){
                System.out.print("[" + s.name() + "]");
            }else{
                System.out.print(s.name());
            }

            System.out.print("  ");

        }

        System.out.println();


    }



}
