package basic;

import bugwars.Direction;
import bugwars.UnitController;
import bugwars.UnitInfo;
import bugwars.UnitType;

public class Queen extends MyUnit {

    Queen(UnitController unitController){
        super(unitController);
    }

    public void play() {
        move();
        trySpawn();
        countCocoonUnits();
    }

    public void move() {
        UnitInfo[] enemies = uc.senseUnits(uc.getOpponent());

        if(enemies.length == 0) {
            /* if there are no enemies close we move into a random direction.
                consider that this strategy is normally a bad one, since what we
                want is to explore the map as much as possible, cleverer things
                can be done...  */
            moveRandom();
        }
        else {
            /* We are a high value non combat unit and we are in danger!
                we will try to escape from them! */
            moveFarFromEnemies();
        }
    }

    public void countMe() {
        // it's empty on propose since we don't want to count queens
    }

    void moveRandom() {
        Direction newDirection = Helper.getRandomDirection();
        if(uc.canMove(newDirection)) {
            uc.move(newDirection);
        }
    }

    void trySpawn() {
        UnitInfo[] enemies = uc.senseUnits(uc.getOpponent());
        Direction[] allDirections = Direction.values();

        // let's try to get new allies :)
        // first decide what unit type we need
        UnitType spawnType = null;
        int numAnts = counters.read(UNIT_INDEX_COUNTER_ANT);
        int numSeenCookies = foodTracker.getSeenCookies();
        if(enemies.length == 0 && numSeenCookies > 0 &&
                (numAnts == 0 || numSeenCookies/numAnts >= 3)) {
            spawnType = UnitType.ANT;
        }
        else {
            spawnType = UnitType.BEETLE;
        }

        // then we try to spawn it
        if(spawnType != null) {
            for (Direction direction: allDirections) {
                if(uc.canSpawn(direction, spawnType)) {
                    uc.spawn(direction, spawnType);
                }
            }
        }

    }

    void countCocoonUnits() {
        UnitInfo[] units = uc.senseUnits(uc.getTeam());
        for (UnitInfo unit: units) {
            if(unit.isCocoon()) {
                UnitType unitType = unit.getType();
                if(unitType == UnitType.BEETLE) {
                    counters.increaseValueByOne(UNIT_INDEX_COUNTER_BEETLE);
                }
                else if(unitType == UnitType.ANT) {
                    counters.increaseValueByOne(UNIT_INDEX_COUNTER_ANT);
                }
                else if(unitType == UnitType.SPIDER) {
                    counters.increaseValueByOne(UNIT_INDEX_COUNTER_SPIDER);
                }
                else if(unitType == UnitType.BEE) {
                    counters.increaseValueByOne(UNIT_INDEX_COUNTER_BEE);
                }
            }
        }
    }
}
