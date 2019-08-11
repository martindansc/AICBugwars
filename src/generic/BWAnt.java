package generic;

import bugwars.*;

public class BWAnt extends Unit {

    BWAnt(Injection in) {
        super(in);
        behaviour = in.behaviour.SAFE;
    }

    public static void check() {

    }

    @Override
    public void selectObjective() {

        // forget the objective if it's already covered by someone else
        if(objective != null && objective.getCounterUnitsThisRound() >= objective.getUnitNumber()) {
            objective = null;
        }

        if(objective != null && objective.stillPresent()) return;

        int objectiveDistance = Integer.MAX_VALUE;

        for(int i = 0; i < in.objectives.MAX_OBJECTIVES; i++) {
            Objective newObjective = in.objectives.getObjective(i, myType);
            if(newObjective == null) continue;

            int newObjectiveDistance = in.map.distanceBetween(newObjective.getLocation(),
                    in.unitController.getLocation());

            if(!newObjective.isFull() && newObjectiveDistance < objectiveDistance) {
                objective = newObjective;
                objectiveDistance = newObjectiveDistance;
            }
        }
    }
}
