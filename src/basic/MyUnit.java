package basic;

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

    abstract void play();
    abstract void countMe();

    public void moveFarFromEnemies() {
        UnitInfo[] enemies = uc.senseUnits(uc.getOpponent());
        Direction bestMoveDirection = getFurthestDirectionFromUnits(enemies);
        if(bestMoveDirection != null && uc.canMove(bestMoveDirection)) {
            uc.move(bestMoveDirection);
        }
    }

    public void reportFood() {
        FoodInfo[] foodSeen = uc.senseFood();
        for(FoodInfo foodInfo: foodSeen) {
            Location foodLocation = foodInfo.getLocation();
            if(!isObstructed(foodLocation)) {
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

    public Direction getFurthestDirectionFromUnits(UnitInfo[] units) {
        double[] scores = getDistanceDirectionFromUnits(units);
        // from the scores we get index of the lowest value and we return the direction
        // associated to it

        double minScore = Double.MAX_VALUE;
        int minIndex = -1;
        for(int i = 0; i < scores.length; i++) {
            if(minScore > scores[i] && uc.canMove(Helper.intToDirection(i))) {
                minScore = scores[i];
                minIndex = i;
            }
        }

        // if we haven't found any valid direction...
        if(minIndex == -1) {
            return null;
        }

        return Helper.intToDirection(minIndex);
    }

     public boolean isObstructed(Location end) {
        Location nextLocation = uc.getLocation();

        while(!nextLocation.isEqual(end)) {

            if(uc.hasObstacle(nextLocation)) {
                return true;
            }
            nextLocation = nextLocation.add(nextLocation.directionTo(end));
        }

        return false;
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
