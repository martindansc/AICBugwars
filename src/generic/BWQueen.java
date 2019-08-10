package generic;

import bugwars.*;

public class BWQueen extends Unit {

    int antObjectivesNum = 0;

    BWQueen(Injection in) {
        super(in);
        behaviour = in.behaviour.SAFE;
    }

    @Override
    public void selectObjective() {

        if(in.unitController.getRound() < 40) {
            objectiveLocation = in.unitController.getEnemyQueensLocation()[0];
            return;
        }

        if(in.unitController.getRound() % 20 == 0) {
            int randomNumber = (int)(Math.random()*8);
            Direction dir = Direction.values()[randomNumber];
            objectiveLocation = in.unitController.getLocation().add(dir);
        }
        else objectiveLocation = null;
    }

    @Override
    public void beforePlay() {

        Location myLocation = in.unitController.getLocation();

        int antObjectiveType = unitTypeToInt(UnitType.ANT);

        antObjectivesNum = 0;
        for(int i = 0; i < in.objectives.MAX_OBJECTIVES; i++) {
            Objective newObjective = in.objectives.getObjective(i, antObjectiveType);
            if(newObjective == null) break;
            newObjective.remove();
            antObjectivesNum++;
        }

        Location[] locations = in.unitController.getVisibleLocations(5);

        for(Location location: locations) {
            if(location.distanceSquared(myLocation) > 3) {
                Objective newObjective = new Objective(in, location, 1);
                in.objectives.addObjective(newObjective, unitTypeToInt(UnitType.ANT));
            }
        }

        if(getCounterValueUnitType(UnitType.ANT) < antObjectivesNum) {
            spawn(UnitType.ANT);
        }
    }

    @Override
    public void beforeAnything() {
        addCocoonUnits();
    }

    public void spawn(UnitType unitType) {
        Direction[] dirs = Direction.values();
        for(int i = 0; i < dirs.length; i++) {
            if(in.unitController.canSpawn(dirs[i], unitType)) {
                in.unitController.spawn(dirs[i], unitType);
            }
        }
    }


}
