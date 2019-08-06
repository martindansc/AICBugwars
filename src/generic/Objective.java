package generic;

import bugwars.*;

public class Objective {
    Injection in;

    int sharedArrayPosition = -1;
    private int locationX;
    private int locationY;
    private int objectiveClass;
    private int units;

    int UNIT_COUNTER = 4;

    Objective(Injection in) {
        this.in = in;
    }

    Objective(Injection in, int sharedArrayPosition) {
        this.in = in;
        this.sharedArrayPosition = sharedArrayPosition;
    }

    Objective(Injection in, Location loc, int type) {
        this.in = in;
        this.locationX = loc.x;
        this.locationY = loc.y;
        this.objectiveClass = type;
    }

    public void setUnitNumber(int num) {
        units = num;
        if(sharedArrayPosition != -1) {
            in.unitController.write(sharedArrayPosition + 3, units);
        }
    }

    public int getObjectiveClass() {
        if(sharedArrayPosition != -1) {
            return in.unitController.read(sharedArrayPosition );
        }
        else {
            return this.objectiveClass;
        }
    }

    public void save(int id) {
        this.sharedArrayPosition = id;
        in.unitController.write(sharedArrayPosition, objectiveClass);
        in.unitController.write(sharedArrayPosition + 1, locationX);
        in.unitController.write(sharedArrayPosition + 2, locationY);
        in.unitController.write(sharedArrayPosition + 3, units);

        // counter
        in.counter.reset(id + UNIT_COUNTER);
    }

    public Location getLocation() {
        if(sharedArrayPosition != -1) {
            this.locationX = in.unitController.read(sharedArrayPosition + 1);
            this.locationY = in.unitController.read(sharedArrayPosition + 2);
        }

        return new Location(this.locationX, this.locationY);
    }

    public void claim() {
        in.counter.increaseValueByOne(sharedArrayPosition + UNIT_COUNTER);
    }

    public boolean isFull() {
        units = in.unitController.read(units);
        return units >= in.counter.read(sharedArrayPosition + UNIT_COUNTER);
    }

    public int getObjectiveSize() {
        return 3 + in.counter.getCounterSpace();
    }
}
