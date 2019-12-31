package tutorial;

import bugwars.*;

public class FoodTracker {
    int MAX_MAP_SIZE = 64;
    int MAX_QUEUE_ELEMENTS = 64;

    int FOOD_START_MEMORY_INDEX; // takes MAX_MAP_SIZE * MAX_MAP_SIZE * 2 * 2 space
    int QUEUE_START_INDEX; // takes MAX_QUEUE_ELEMENTS space
    int FOOD_SEEN_INDEX;
    int QUEUE_COUNTER_INDEX;

    Location referenceLocation;

    UnitController uc;
    FoodTracker(UnitController unitController, int foodStartMemoryIndex, Location rl){
        FOOD_SEEN_INDEX = foodStartMemoryIndex;
        FOOD_START_MEMORY_INDEX = FOOD_SEEN_INDEX + 1;
        QUEUE_COUNTER_INDEX = FOOD_START_MEMORY_INDEX + 1;
        QUEUE_START_INDEX = QUEUE_COUNTER_INDEX + MAX_MAP_SIZE * MAX_MAP_SIZE * 2;
        uc = unitController;
        referenceLocation = rl;
    }


    public int getFoodIndex(int intLocation) {
        return FOOD_START_MEMORY_INDEX + intLocation * 2 + 1;
    }

    public void saveFoodSeen(Location loc) {
        int intLocation = Helper.locationToInt(loc, referenceLocation);

        int previous = uc.read(FOOD_START_MEMORY_INDEX + intLocation*2 + 1);

        if(previous == 0) {
            // mark that the location has food but no one claimed
            uc.write(getFoodIndex(intLocation), -1);

            // insert to the queue of foods
            int currentQueueIndex = uc.read(QUEUE_COUNTER_INDEX);
            uc.write(QUEUE_START_INDEX  + 1 + currentQueueIndex, intLocation);

            // add +1 to the insert index
            uc.write(QUEUE_COUNTER_INDEX, (currentQueueIndex + 1)%MAX_QUEUE_ELEMENTS);

            // add +1 to food seen
            int foodSeen = uc.read(FOOD_SEEN_INDEX);
            uc.write(FOOD_SEEN_INDEX, foodSeen + 1);
        }
    }

    public void claimMine(Location loc) {
        int intLocation = Helper.locationToInt(loc, referenceLocation);
        int index = getFoodIndex(intLocation);
        uc.write(index, uc.getRound());
        uc.write(index + 1, uc.getInfo().getID());
    }

    public boolean isMine(Location loc) {
        int intLocation = Helper.locationToInt(loc, referenceLocation);
        return uc.read(getFoodIndex(intLocation) + 1) == uc.getInfo().getID();
    }

    public Location getNearestUnclaimedDiscoveredFood(Location loc) {
        return getNearestUnclaimedDiscoveredFood(loc, Integer.MAX_VALUE);
    }

    public Location getNearestUnclaimedDiscoveredFood(Location loc, int maxDistance) {

        int bestIntLocation = -1;
        int closest = Integer.MAX_VALUE;

        for(int i = 0; i < MAX_QUEUE_ELEMENTS; i++) {
            int nextIntLocation = uc.read(QUEUE_START_INDEX + 1 + i);

            int index = getFoodIndex(nextIntLocation);

            int lastRoundClaimed = uc.read(index);
            int lastIdClaimed = uc.read(index + 1);

            if(lastIdClaimed < 0 || uc.getRound() - lastRoundClaimed > 20) {
                int distance = Helper.intToLocation(nextIntLocation, referenceLocation).distanceSquared(loc);
                if((bestIntLocation == -1 || distance < closest) && distance < maxDistance) {
                    bestIntLocation = nextIntLocation;
                    closest = distance;
                }
            }
        }

        if(bestIntLocation == -1) return null;
        return Helper.intToLocation(bestIntLocation, referenceLocation);
    }

    public Location getAdjacentUnclaimedDiscoveredFood(Location loc) {
        Direction[] directions = Direction.values();
        for (Direction dir: directions) {
            Location newLoc = loc.add(dir);
            int intLocation = Helper.locationToInt(newLoc, referenceLocation);
            int index = getFoodIndex(intLocation);

            int lastRoundClaimed = uc.read(index);
            int lastIdClaimed = uc.read(index + 1);

            if(lastIdClaimed < 0 || uc.getRound() - lastRoundClaimed > 20){
                return newLoc;
            }
        }

        return null;
    }

    public int getSeenCookies() {
        return uc.read(FOOD_SEEN_INDEX);
    }
}
