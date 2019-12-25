package basic;

import bugwars.UnitController;

public class Spider extends MyUnit {

    Spider(UnitController unitController){
        super(unitController);
    }

    public void play() {

    }

    public void countMe() {
        counters.increaseValueByOne(UNIT_INDEX_COUNTER_SPIDER);
    }
}
