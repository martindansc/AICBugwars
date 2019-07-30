package generic;

import bugwars.*;

public class UnitPlayer {

    Injection in;

    public void run(UnitController uc) {
        in = new Injection(uc, instantiateClass());

        while (true){
            in.unit.play();
            uc.yield();
        }

    }


    private Unit instantiateClass() {
        UnitType myType = in.unitController.getType();
        if(UnitType.QUEEN == myType) {
            return new BWQueen(in);
        }

        return null;
    }
}
