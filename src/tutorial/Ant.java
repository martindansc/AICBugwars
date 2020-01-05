package tutorial;

import bugwars.*;

public class Ant extends MyUnit {

    Location[] myFood= {null, null, null};

    Ant(UnitController unitController){
        super(unitController);
    }
    public void play() {
        assignFood();
        tryCollectFood();
        move();
        tryCollectFood();
        tryGenericAttack();
    }

    public void tryCollectFood() {

        FoodInfo bestFood = null;

        FoodInfo[] foodsInfo = uc.senseFood();
        for(FoodInfo currentFoodInfo: foodsInfo) {
            // first we skip some foodsInfo...
            if(currentFoodInfo.getFood() > 3) {
                // then we compare which one is better
                if (isBetterFoodAThanB(currentFoodInfo, bestFood)) {
                    bestFood = currentFoodInfo;
                }
            }
        }

        if(bestFood != null && uc.canMine(bestFood)) {
            uc.mine(bestFood);
        }
    }

    public boolean isBetterFoodAThanB(FoodInfo a, FoodInfo b) {
        // first we try with our assigned food
        // if we can't, we then try to get any food

        // check if one is null and return the other
        if(a == null) return false;
        if(b == null) return true;

        Location locationA = a.getLocation();
        Location locationB = b.getLocation();

        // if one is mine and the other isn't, we return the one that is mine no matter what
        if(foodTracker.isMine(locationA) && !foodTracker.isMine(locationB)) {
            return true;
        }

        if(!foodTracker.isMine(locationA) && foodTracker.isMine(locationB)) {
            return false;
        }

        // else, we get from the one that has more
        return a.getFood() > b.getFood();
    }

    public void assignFood() {
        // check if we have any food already assigned, if not, get our food
        if(myFood[0] == null) {
            myFood[0] = foodTracker.getNearestUnclaimedDiscoveredFood(uc.getLocation());
            if(myFood[0] != null) foodTracker.claimMine(myFood[0]);
        }

        if(myFood[0] != null) {
            if(myFood[1] == null) {
                myFood[1] = foodTracker.getAdjacentUnclaimedDiscoveredFood(myFood[0]);
                if(myFood[1] != null) foodTracker.claimMine(myFood[1]);
            }

            if(myFood[2] == null) {
                myFood[2] = foodTracker.getAdjacentUnclaimedDiscoveredFood(myFood[0]);
                if(myFood[2] != null) foodTracker.claimMine(myFood[2]);
            }
        }

        // claim our assigned food
        for (Location foodLocation: myFood) {
            if (foodLocation != null) {
                foodTracker.claimMine(foodLocation);
            }
        }

    }

    public void move() {
        UnitInfo[] enemies = uc.senseUnits(uc.getOpponent());

        if(enemies.length == 0) {
            /* if there are no enemies we go to our objective location  */
            pathfinding.moveTo(myFood[0]);
        }
        else {
            doMicro();
        }
    }

    public void countMe() {
        counters.increaseValueByOne(UNIT_INDEX_COUNTER_ANT);
    }

}
