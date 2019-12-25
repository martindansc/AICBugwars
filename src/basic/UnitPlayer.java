package basic;

import bugwars.*;

public class UnitPlayer {

    public void run(UnitController uc) {

        MyUnit me;

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

            me.countMe();
            me.play();
            me.reportFood();

            uc.yield(); //End of turn
        }

    }
}
