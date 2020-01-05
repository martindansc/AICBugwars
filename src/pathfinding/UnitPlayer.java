package pathfinding;

import bugwars.*;

import java.nio.file.Path;

public class UnitPlayer {

    //my UnitController
    UnitController uc;
    Pathfinding pathfinding;

    public void run(UnitController uc) {
        /*Insert here the code that should be executed only at the beginning of the unit's lifespan*/

        this.uc = uc;
        if (uc.getType() == UnitType.QUEEN) spawnBeetle();

        pathfinding = new Pathfinding(uc);

        while (true){
            /*Insert here the code that should be executed every round*/

            //If I'm a beetle and I can move this turn, I try to go to the first enemy queen
            pathfinding.moveTo(uc.getEnemyQueensLocation()[0]);

            uc.yield(); //End of turn
        }

    }

    void spawnBeetle(){
        //initialize dir pointing to enemy queen
        Direction dir = uc.getLocation().directionTo(uc.getEnemyQueensLocation()[0]);
        for (int i = 0; i < 8; ++i){
            if (uc.canSpawn(dir, UnitType.BEETLE)){
                uc.spawn(dir, UnitType.BEETLE);
                break;
            }
            dir = dir.rotateRight();
        }
    }

    //*************PATHFINDING*********************//

    final int INF = 1000000;

    boolean rotateRight = true; //if I should rotate right or left
    Location lastObstacleFound = null; //latest obstacle I've found in my way
    int minDistToEnemy = INF; //minimum distance I've been to the enemy while going around an obstacle
    Location prevTarget = null; //previous target

    void moveTo(Location target){
        //No target? ==> bye!
        if (target == null) return;

        //different target? ==> previous data does not help!
        if (prevTarget == null || !target.isEqual(prevTarget)) resetPathfinding();

        //If I'm at a minimum distance to the target, I'm free!
        Location myLoc = uc.getLocation();
        int d = myLoc.distanceSquared(target);
        if (d <= minDistToEnemy) resetPathfinding();

        //Update data
        prevTarget = target;
        minDistToEnemy = Math.min(d, minDistToEnemy);

        //If there's an obstacle I try to go around it [until I'm free] instead of going to the target directly
        Direction dir = myLoc.directionTo(target);
        if (lastObstacleFound != null) dir = myLoc.directionTo(lastObstacleFound);

        //This should not happen for a single unit, but whatever
        if (uc.canMove(dir)) resetPathfinding();

        //I rotate clockwise or counterclockwise (depends on 'rotateRight'). If I try to go out of the map I change the orientation
        //Note that we have to try at most 16 times since we can switch orientation in the middle of the loop. (It can be done more efficiently)
        for (int i = 0; i < 16; ++i){
            if (uc.canMove(dir)){
                uc.move(dir);
                return;
            }
            Location newLoc = myLoc.add(dir);
            if (uc.isOutOfMap(newLoc)) rotateRight = !rotateRight;
                //If I could not go in that direction and it was not outside of the map, then this is the latest obstacle found
            else lastObstacleFound = myLoc.add(dir);
            if (rotateRight) dir = dir.rotateRight();
            else dir = dir.rotateLeft();
        }

        if (uc.canMove(dir)) uc.move(dir);
    }

    //clear some of the previous data
    void resetPathfinding(){
        lastObstacleFound = null;
        minDistToEnemy = INF;
    }

}
