package generic;

import bugwars.*;

public class Map {
    private Injection in;

    int offsetX;
    int offsetY;

    int MAX_SIZE_MAP;
    int INFO_PER_CELL;
    int INFO_STATIC_SIZE;

    Map(Injection in) {
        this.in = in;

        MAX_SIZE_MAP = getMapMaxSize();
        INFO_PER_CELL = getInfoCellSize();
        INFO_STATIC_SIZE = getStaticInfoSize();

        int readX = in.unitController.read(in.constants.SHARED_MAP_ID);
        if(readX == 0) {
            Location loc = in.unitController.getLocation();
            offsetX = MAX_SIZE_MAP/2 - loc.x;
            offsetY = MAX_SIZE_MAP/2 - loc.y;
            in.unitController.write(in.constants.SHARED_MAP_ID, offsetX);
            in.unitController.write(in.constants.SHARED_MAP_ID + 1, offsetY);
        }
        else {
            offsetX = readX;
            offsetY = in.unitController.read(in.constants.SHARED_MAP_ID + 1);
        }

    }

    private int getIndexMap(int locX, int locY) {
        return in.constants.SHARED_MAP_ID + INFO_STATIC_SIZE + locationToInt(locX, locY) * INFO_PER_CELL;
    }

    // helpers

    public int locationToInt(int locX, int locY) {
        int x = (locX - offsetX);
        if(x < 0) x += MAX_SIZE_MAP;

        int y = (locY - offsetY);
        if(y < 0) y += MAX_SIZE_MAP;

        return x * MAX_SIZE_MAP + y;
    }

    public Location intToLocation(int loc) {
        int x = loc / MAX_SIZE_MAP + offsetX;
        if(x > MAX_SIZE_MAP) x -= MAX_SIZE_MAP;

        int y = loc / MAX_SIZE_MAP + offsetX;
        if(y > MAX_SIZE_MAP) y -= MAX_SIZE_MAP;

        return new Location(x, y);
    }

    public int locationToInt(Location loc) {
        return locationToInt(loc.x, loc.y);
    }

    public int distanceBetween(Location loc1, Location loc2) {
        return loc1.distanceSquared(loc2);
    }

    // general prop

    public int getValueInLocation(int key, Location loc) {
        int id = key + locationToInt(loc.x, loc.y);
        return in.unitController.read(key + id);
    }

    public void setValueInLocation(int key, Location loc, int value) {
        int id = key + locationToInt(loc.x, loc.y);
        in.unitController.write(key + id, value);
    }

    // Space functions

    public static int getSimpleMapSpace() {
        return getMapMaxSize() * getMapMaxSize();
    }

    public static int getMapSpace() {
       return getStaticInfoSize() + getInfoCellSize() * getMapMaxSize() * getMapMaxSize();
    }

    public static int getMapMaxSize() {
        return 80;
    }

    public static int getInfoCellSize() {
        return 20;
    }

    public static int getStaticInfoSize() {
        return 2;
    }
}
