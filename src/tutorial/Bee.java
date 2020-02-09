package tutorial;

import bugwars.UnitController;

public class Bee extends MyUnit {

    Bee(UnitController unitController){
        super(unitController);
    }

    public void play() {

    }

    public void countMe() {
        counters.increaseValueByOne(UNIT_INDEX_COUNTER_BEE);
    }
}
