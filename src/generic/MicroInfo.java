package generic;

import bugwars.*;

public class MicroInfo {
    Location location;
    int numEnemies;
    int minDistToEnemy;

    MicroInfo(Location location) {
        this.location = location;
        numEnemies = 0;
        minDistToEnemy = Integer.MAX_VALUE;
    }

    public void update(UnitInfo unit) {

    }
}
