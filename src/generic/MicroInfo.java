package generic;

import bugwars.*;

public class MicroInfo {

    Injection in;
    Location location;
    UnitType myType;
    int numEnemies;
    int minDistToEnemy;
    UnitInfo enemyToAttack;

    Direction direction;
    boolean canMoveLocation;
    boolean needsToMoveToAttack;

    boolean isPathfinderDirection;

    MicroInfo(Injection in, Location location, boolean isPathfinderDirection) {
        this.location = location;
        this.myType = in.unitController.getType();
        this.isPathfinderDirection = isPathfinderDirection;
        this.in = in;
        numEnemies = 0;
        minDistToEnemy = Integer.MAX_VALUE;
        enemyToAttack = null;

        direction = in.unitController.getLocation().directionTo(location);
        canMoveLocation = in.unitController.canMove(direction);
    }

    public void update(UnitInfo unit) {
        if(unit.getTeam() != in.unitController.getTeam() && canMoveLocation) {
            Location enemyLocation = unit.getLocation();
            Direction directionToMe = enemyLocation.directionTo(location);
            Location oneStepEnemyLocation = enemyLocation.add(directionToMe);

            int distance = location.distanceSquared(enemyLocation);

            canEnemyAttackMe(unit, oneStepEnemyLocation);
            canIAttackEnemy(unit, enemyLocation, distance);

            if (distance < minDistToEnemy) minDistToEnemy = distance;
        }
    }

    private void canEnemyAttackMe(UnitInfo unit, Location oneStepEnemyLocation) {
        UnitType enemyType = unit.getType();
        if(enemyType.getAttack() == 0) return;

        // can he attack me if I move here?
        int distance = oneStepEnemyLocation.distanceSquared(location);
        if (distance <= enemyType.getAttackRangeSquared() &&
                distance >= unit.getType().getMinAttackRangeSquared() &&
                !in.unitController.isObstructed(oneStepEnemyLocation, location)) {
            numEnemies++;
        }
    }

    private void canIAttackEnemy(UnitInfo unit, Location enemyLocation, int distance) {
        if(myType.getAttack() == 0) return;

        // can I attack him if I move here?
        if (distance <= myType.getAttackRangeSquared() &&
                distance >= myType.getMinAttackRangeSquared() &&
                !in.unitController.isObstructed(enemyLocation, location)) {

            if(enemyToAttack == null || isBetterToAttack(unit, enemyToAttack)) {
                setUnitToAttack(unit, enemyLocation);
            }
        }
    }

    public boolean canKillEnemy() {
        if(enemyToAttack == null) return false;
        return enemyToAttack.getHealth() < myType.getAttack();
    }

    private void setUnitToAttack(UnitInfo unit, Location enemyLocation) {
        enemyToAttack = unit;

        int distance = in.unitController.getLocation().distanceSquared(enemyLocation);
        needsToMoveToAttack = distance <= myType.getAttackRangeSquared() &&
                distance >= myType.getMinAttackRangeSquared() &&
                !in.unitController.isObstructed(enemyLocation, location);
    }

    public Location moveLocation() {
        return location;
    }

    public Location attackLocation() {
        if(enemyToAttack != null) {
            return enemyToAttack.getLocation();
        }
        return null;
    }

    private static boolean isBetterToAttack(UnitInfo a, UnitInfo b) {

        if(a == null && b == null) return false;
        if(a == null) {
            return false;
        }
        if(b == null) {
            return true;
        }

        int aEnemyHealth = a.getHealth();
        int bEnemyHealth = b.getHealth();
        int damage = (int) b.getType().getAttack();

        if(bEnemyHealth > damage && aEnemyHealth < damage) {
            return true;
        }

        if(bEnemyHealth < damage && aEnemyHealth > damage) {
            return false;
        }

        if(aEnemyHealth < damage) {
            return aEnemyHealth > bEnemyHealth;
        }

        return aEnemyHealth < bEnemyHealth;
    }
}
