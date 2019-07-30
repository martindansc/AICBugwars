package generic;

import bugwars.*;

public class Map {
    private Injection in;

    int offsetX;
    int offsetY;

    int MAX_SIZE_MAP = 80;
    int INFO_PER_CELL = 10;
    int INFO_STATIC_SIZE = 2;

    Map(Injection in) {
        this.in = in;

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

    public int locationToInt(int locX, int locY) {
        return (locX - offsetX) * 100 + (locY - offsetY);
    }

    public int locationToInt(Location loc) {
        return locationToInt(loc.x, loc.y);
    }
}
