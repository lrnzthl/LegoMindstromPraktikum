package bulldozer.main;




public class Menu {

    private ParcourState state;
    private ParcourState selectedState;
    private Hardware hardware;

    private Brains brain;


    public Menu(Hardware hardware){
        //beginning state is always menu
        state = ParcourState.MENU;
        selectedState = ParcourState.values()[1];

        //checking if the hardware is initisalized properly
        if(hardware != null || hardware.isInit() ){
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
        showOptions();
        while (running) {
            
            int newSelectedState;
            switch (hardware.getButtonType()) {
            	case NONE:
            		break;
                case LEFT:	
                	newSelectedState = selectedState.getId() - 1;
                    
                    if (newSelectedState < 1) {
                        newSelectedState = ParcourState.values().length - 1;
                    }
                    selectedState = ParcourState.values()[newSelectedState];
                    showOptions();
                    break;
                case RIGHT:
                    newSelectedState = selectedState.getId() + 1;

                    if (newSelectedState > ParcourState.values().length - 1) {
                        newSelectedState = 1;
                    }
                    selectedState = ParcourState.values()[newSelectedState];
                	showOptions();
                    break;
                case ENTER:
                    state = selectedState;
                    startBrain();
                    break;
                case ESCAPE:
                    running = false;
                    break;
                default:
                    break;
            }
        }

        System.exit(1);
    }

    private void startBrain() {
        switch (state){
            case LINE:
                brain = new Line(hardware);
                break;
            case LABYRINTH:
                brain = new Labyrinth(hardware);
                break;
            case BARRIER:
                //
                break;
            case BRIDGE:
                //
                break;
         //   case FINDCOLOUR:
                //
          //      break;
            default:
                System.out.println("Cannot start MENU state");
                break;
        }

        int returnState = 0;
        try {
            returnState = brain.mainLoop();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        switch (returnState){
        case -1:
        	//state = ParcourState.MENU;
        	//%TODO: add back to menu
            System.exit(0);
        	break;
        case 0:
        	break;
        case 1:
        	if(state.getId() < state.values().length - 1){
        		state = state.values()[state.getId() + 1];
        		selectedState = state;
        		startBrain();
        	} else {
        		state = ParcourState.MENU;
        		System.out.println("Completed last task :)");
        	}
        	break;
        default:
        	break;	
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
