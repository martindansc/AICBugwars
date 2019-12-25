package basic;

import bugwars.*;

// this class is for small utility functions that can be called from anywhere in the code
public class Helper {

    static public int directionToInt(Direction dir) {
        return dir.ordinal();
    }

    static public Direction intToDirection (int num) {
        return Direction.values()[num];
    }

    static public int typeToInt(UnitType type) {
        return type.ordinal();
    }

    static public UnitType intToType (int num) {
        return UnitType.values()[num];
    }


    static public Direction getRandomDirection() {
        int randomNumber = (int)(Math.random()*Direction.values().length);
        return Direction.values()[randomNumber];
    }

    static public int locationToInt(Location loc, Location base) {
        int MAX_MAP_SIZE = 64;
        int x = (loc.x - base.x) + MAX_MAP_SIZE;
        int y = (loc.y - base.y)+ MAX_MAP_SIZE;
        return x * MAX_MAP_SIZE*2 + y;
    }

    static public Location intToLocation(int loc, Location base) {
        int MAX_MAP_SIZE = 64;

        int x = loc/(MAX_MAP_SIZE*2) - MAX_MAP_SIZE + base.x;
        int y = loc%(MAX_MAP_SIZE*2) - MAX_MAP_SIZE + base.y;

        return new Location(x, y);
    }
}
