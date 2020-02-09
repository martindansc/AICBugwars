package generic;

import bugwars.*;

import java.util.Random;

public class Objective {
    Injection in;

    int sharedArrayPosition = -1;
    private int locationX;
    private int locationY;
    private int objectiveClass;
    private int units = 1;
    private int needsMapLocation = 0;

    int UNIT_COUNTER = 5;

    Objective(Injection in) {
        this.in = in;
    }

    Objective(Injection in, int sharedArrayPosition) {
        this.in = in;
        this.sharedArrayPosition = sharedArrayPosition;
    }

    Objective(Injection in, Location loc) {
        this.in = in;
        this.locationX = loc.x;
        this.locationY = loc.y;
        this.objectiveClass = (int)(Math.random()*Integer.MAX_VALUE);
    }

    public void setUnitNumber(int num) {
        units = num;
        if(sharedArrayPosition != -1) {
            in.unitController.write(sharedArrayPosition + 3, units);
        }
    }

    public int getObjectiveClass() {
        if(sharedArrayPosition != -1) {
            this.objectiveClass = in.unitController.read(sharedArrayPosition );
        }

        return this.objectiveClass;

    }

    public void save(int id) {
        this.sharedArrayPosition = id;
        in.unitController.write(sharedArrayPosition, objectiveClass);
        in.unitController.write(sharedArrayPosition + 1, locationX);
        in.unitController.write(sharedArrayPosition + 2, locationY);
        in.unitController.write(sharedArrayPosition + 3, units);
        in.unitController.write(sharedArrayPosition + 4, needsMapLocation);

        // counter
        in.counter.reset(id + UNIT_COUNTER);

        // map location
        if(needsMapLocation == 1) {
            Location myLocation = getLocation();
            if(in.map.getValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, myLocation) == 0) {
                in.map.setValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, myLocation, id);
            }
            else {
               in.unitController.println("Can't add objective to location");
            }
        }

    }

    public void changeLocation(Location newLocation) {
        if(getNeedsMapLocation() == 1) {
            in.map.setValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID,
                    newLocation, sharedArrayPosition);
            in.map.setValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID,
                    getLocation(), 0);
        }

        this.locationX = newLocation.x;
        this.locationY = newLocation.y;

        if(sharedArrayPosition != -1) {
            in.unitController.write(sharedArrayPosition + 1, locationX);
            in.unitController.write(sharedArrayPosition + 2, locationY);
        }
    }

    public Location getLocation() {
        if(sharedArrayPosition != -1) {
            this.locationX = in.unitController.read(sharedArrayPosition + 1);
            this.locationY = in.unitController.read(sharedArrayPosition + 2);
        }

        return new Location(this.locationX, this.locationY);
    }

    public int getNeedsMapLocation() {
        if(sharedArrayPosition != -1) {
            this.needsMapLocation = in.unitController.read(sharedArrayPosition + 4);
        }

        return this.needsMapLocation;
    }

    public void claim() {
        in.counter.increaseValueByOne(sharedArrayPosition + UNIT_COUNTER);
    }

    public boolean isFull() {
        return getUnitNumber() <= getCounterUnits();
    }

    public int getCounterUnits() {
        return in.counter.read(sharedArrayPosition + UNIT_COUNTER);
    }

    public int getCounterUnitsThisRound() {
        if(sharedArrayPosition != -1) {
            return in.counter.readThisRoundOnly(sharedArrayPosition + UNIT_COUNTER);
        }
        return -1;
    }

    public int getUnitNumber() {
        if(sharedArrayPosition != -1) {
            this.units = in.unitController.read(sharedArrayPosition + 3);
        }
        return this.units;
    }

    public void remove() {
        Location myLocation = getLocation();

        int needsMapLocation = getNeedsMapLocation();
        if(needsMapLocation == 1 &&
                in.map.getValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, myLocation) == sharedArrayPosition) {
            in.map.setValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID,  myLocation, 0);
        }

        int size =  getObjectiveSize();
        for(int i = 0; i < size; i++) {
            in.unitController.write(sharedArrayPosition + i, 0);
        }
    }

    public boolean stillPresent() {
        int readClass = getObjectiveClass();
        if(this.objectiveClass != 0 && readClass != this.objectiveClass) {
            return false;
        }

        this.objectiveClass = readClass;
        return true;
    }

    public static int getObjectiveSize() {
        return 5 + Counter.getCounterSpace();
    }
}
