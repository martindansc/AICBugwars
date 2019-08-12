package generic;

import bugwars.*;

public class UnitPlayer {

    Injection in;

    public void run(UnitController uc) {
        in = new Injection(uc);
        instantiateUnitClass(in);

        while (true){
            if(uc.getRound() > 200) return;

            in.unit.play();
            uc.yield();
        }

    }


    private void instantiateUnitClass(Injection in) {
        UnitType myType = in.unitController.getType();
        if(UnitType.QUEEN == myType) {
            in.setUnit(new BWQueen(in));
        }
        else if(UnitType.ANT == myType) {
            in.setUnit(new BWAnt(in));
        }
        else if(UnitType.BEETLE == myType) {
            in.setUnit(new BWBeetle(in));
        }
    }
}
