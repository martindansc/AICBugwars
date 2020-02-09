package tutorial;

import bugwars.*;

public class UnitPlayer {

    public void run(UnitController uc) {

        // declare that we are a unit
        MyUnit me;

        // instantiate the class according to our unit type
        UnitType myType = uc.getType();
        if(myType == UnitType.ANT) {
            me = new Ant(uc);
        }
        else if(myType == UnitType.QUEEN) {
            me = new Queen(uc);
        }
        else if(myType == UnitType.BEETLE) {
            me = new Beetle(uc);
        }
        else if(myType == UnitType.BEE) {
            me = new Bee(uc);
        }
        else {
            me = new Spider(uc);
        }

        while(true){
            // MyUnit class should define all the below functions
            // if it's not implemented,
            // you have to declare it as abstract
            me.countMe();
            me.play();
            me.reportFood();

            //if(uc.getRound() >500) return;

            uc.yield(); //End of turn
        }

    }
}
