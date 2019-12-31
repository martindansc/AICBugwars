package tutorial;

import bugwars.*;

public class Beetle extends MyUnit {

    Beetle(UnitController unitController){
        super(unitController);
    }

    public void play() {
        boolean attackedThisTurn = tryGenericAttack();
        move(attackedThisTurn);
        tryGenericAttack();
    }

    public void move(boolean attackedThisTurn) {
        // decide macro target, we are going for the first enemy queen
        Location targetLocation = uc.getQueensLocation(uc.getOpponent())[0];

        // now we move, different moves if we attacked or we haven't
        if(!attackedThisTurn) {
            // we don't see enemies or we see them but we can't attack them so
            // we continue to our path
            // alternatively we could try to avoid them since they may attack us
            pathfinding.moveTo(targetLocation);
        }
        else {
            doMicro();
        }
    }

    public void countMe() {
        counters.increaseValueByOne(UNIT_INDEX_COUNTER_BEETLE);
    }
}
