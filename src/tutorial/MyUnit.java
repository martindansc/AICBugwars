package tutorial;

import bugwars.*;

// this class have the functions that can be called from unit classes
public abstract class MyUnit {

    int UNIT_INDEX_COUNTER_BEETLE = 0;
    int UNIT_INDEX_COUNTER_ANT = 4;
    int UNIT_INDEX_COUNTER_BEE = 8;
    int UNIT_INDEX_COUNTER_SPIDER = 12;
    int FOOD_START_INDEX = 16;

    UnitController uc;
    Pathfinding pathfinding;
    FoodTracker foodTracker;
    Counter counters;

    Location referenceLocation;

    MyUnit(UnitController unitController) {
        uc = unitController;

        // always make sure the reference location is the same for all the units
        referenceLocation = uc.getTeam().getInitialLocations()[0];

        pathfinding = new Pathfinding(uc);
        foodTracker = new FoodTracker(uc, FOOD_START_INDEX, referenceLocation);
        counters = new Counter(uc);
    }

    /* here we can declare the functions that all the units must have, but the behaviour
       for each unit should be different.
       For example, countMe function should add +1 to the unit type,
       Ants must add one to Ant's counter, Bees to Bee's counter, etc. If we look at Ant class we will see
       that exists a function called countMe() that adds one to the Ant's counter.
       The function is called from UnitPlayer, so it must be declared that all the units have this function.
     */

    abstract void play();
    abstract void countMe();


    /* Functions that can be used for all the units, that means that you can call this function from Ant, Bee, Beetle,
       and any class that extends this class (MyUnit). This is useful to reuse common code for all the units.
     */

    public void doMicro() {
        Location myLocation = uc.getLocation();
        Direction[] directions = Direction.values();

        // initialize the micros
        MicroInfo[] microInfos = new MicroInfo[directions.length];
        for(int i = 0; i < directions.length; i++) {
            Location newLocation = myLocation.add(directions[i]);
            microInfos[i] = new MicroInfo(newLocation, uc);
        }

        // update micros for each enemy
        UnitInfo[] enemies = uc.senseUnits(uc.getOpponent());
        for(UnitInfo enemy: enemies) {
            for(int i = 0; i < directions.length; i++) {
                microInfos[i].update(enemy);
            }
        }

        // choose best micro
        MicroInfo bestMicro = microInfos[Helper.directionToInt(Direction.ZERO)];
        for(MicroInfo micro: microInfos) {
            if(uc.canMove(myLocation.directionTo(micro.loc)) && micro.ImBetterThan(bestMicro)) {
                bestMicro = micro;
            }
        }

        // move
        Direction bestMoveDirection = myLocation.directionTo(bestMicro.loc);
        if(uc.canMove(bestMoveDirection)) {
            uc.move(bestMoveDirection);
        }
    }

    public void reportFood() {
        FoodInfo[] foodSeen = uc.senseFood();
        for(FoodInfo foodInfo: foodSeen) {
            Location foodLocation = foodInfo.getLocation();
            if(!uc.isObstructed(uc.getLocation(), foodLocation)) {
                foodTracker.saveFoodSeen(foodInfo.getLocation());
            }
        }
    }

    // Given a list of units, return the distance scores from all of them for all the directions
    public double[] getDistanceDirectionFromUnits(UnitInfo[] units) {
        Direction[] allDirections = Direction.values();

        double[] scores = new double[allDirections.length];
        for (UnitInfo unit: units) {
            Location unitLocation = unit.getLocation();
            Direction dirToLocation = uc.getLocation().directionTo(unitLocation);

            scores[Helper.directionToInt(dirToLocation)] += 1;
            scores[Helper.directionToInt(dirToLocation.rotateLeft())] += 0.7;
            scores[Helper.directionToInt(dirToLocation.rotateRight())] += 0.7;

            Direction opositeDirection = dirToLocation.opposite();
            scores[Helper.directionToInt(opositeDirection)] += -1;
            scores[Helper.directionToInt(opositeDirection.rotateLeft())] += -0.7;
            scores[Helper.directionToInt(opositeDirection.rotateRight())] += -0.7;
        }
        return scores;
    }

    public boolean tryGenericAttack() {
        UnitInfo[] enemies = uc.senseUnits(uc.getOpponent());
        UnitInfo bestUnitToAttack;

        if(enemies.length > 0) {
            bestUnitToAttack = getGenericBestUnitToAttack(enemies);
            if(bestUnitToAttack != null) {
                uc.attack(bestUnitToAttack);
                return true;
            }
        }
        return false;
    }

    public UnitInfo getGenericBestUnitToAttack(UnitInfo[] units) {

        UnitInfo unitToAttack = null;
        // we will get the minimum health unit that we can attack
        // there is a lot to improve here, go on and try :)
        for (UnitInfo unit: units) {
            if((unitToAttack == null || unitToAttack.getHealth() > unit.getHealth()) &&
                    uc.canAttack(unit)) {
                unitToAttack = unit;
            }
        }

        return unitToAttack;
    }
}
