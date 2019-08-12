package generic;

import bugwars.*;

public abstract class Unit {
    Injection in;
    Objective objective;
    Location objectiveLocation;

    int myType;

    int behaviour;

    Direction lastDirectionMoved;
    Direction pathfinderDirection;
    boolean moved = true;

    MicroInfo[] microInfo;
    MicroInfo bestMicro;


    Unit(Injection in) {
        this.in = in;
        behaviour = in.behaviour.SAFE;
        myType = unitTypeToInt(in.unitController.getType());
    }

    public abstract void selectObjective();

    private void move(Direction dir) {
        moved = true;
        lastDirectionMoved = dir;
        in.unitController.move(dir);
    }

    public void move() {
        Direction dir =
                in.unitController.getLocation().directionTo(bestMicro.moveLocation());

        if(dir != null && dir != Direction.ZERO && in.unitController.canMove(dir)) {
            move(dir);
        }

    }

    public void updatePathfinderDirection() {
        if(objectiveLocation == null) pathfinderDirection = null;
        else pathfinderDirection = in.pathfinder.getNextLocationTarget(objectiveLocation);
    }

    public boolean compareMicroMove(MicroInfo a, MicroInfo b) {
        // true means a is better than b
        return computeMicroScore(a) > computeMicroScore(b);
    }

    public float computeMicroScore(MicroInfo a) {
        // TODO: this should be changed to behaviours
        if(!a.canMoveLocation) {
            return Integer.MIN_VALUE;
        }

        float score = 0;

        score -= a.numEnemies;

        if(a.canKillEnemy()) {
            score += 5;
        }

        if(a.enemyToAttack != null) {
            score += 1;
        }

        if(a.isPathfinderDirection) {
            score += 0.5;
        }

        return score;
    }


    public void claimObjective(){
        if(objective != null) {
            objective.claim();
        }
    }

    public void initMicro(){
        Location myLocation = in.unitController.getLocation();
        microInfo = new MicroInfo[9];
        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            microInfo[i] = new MicroInfo(in, myLocation.add(directions[i]),
                    pathfinderDirection == directions[i]);
        }
    }

    public void updateMicro(UnitInfo unitInfo) {
        Direction[] directions = Direction.values();
        for(int i = 0; i < directions.length; i++) {
            microInfo[i].update(unitInfo);
        }
    }

    public void chooseBestMicro() {
        bestMicro = microInfo[0];

        Direction[] directions = Direction.values();
        for(int i = 1; i < directions.length; i++) {
            if(compareMicroMove( microInfo[i], bestMicro)) {
                bestMicro = microInfo[i];
            }
        }
    }

    public void attack(boolean beforeMove) {
        if(beforeMove && bestMicro.needsToMoveToAttack) return;

        Location loc = bestMicro.attackLocation();
        if(loc != null) {
            in.unitController.attack(loc);
        }
    }

    public void beforeAnything() {}

    public void beforePlay(){}

    public void afterPlay() {}

    public void forEachUnit() {
        UnitInfo[] units = in.unitController.senseUnits();
        for (UnitInfo unit: units) {
            if(in.unitController.getEnergyLeft() < 5000) return;
            unitSensed(unit);
            updateMicro(unit);
        }
    }

    public void unitSensed(UnitInfo unit) {}

    public void setBehaviour(int behaviour) {
        this.behaviour = behaviour;
    }

    public void play() {
        moved = false;

        this.beforeAnything();

        in.counter.increaseValueByOne(in.constants.SHARED_UNIT_COUNTER);
        this.increaseCounterUnitType(myType);

        this.selectObjective();
        if(objective != null) {
            this.objectiveLocation = this.objective.getLocation();
        }

        this.updatePathfinderDirection();

        this.initMicro();

        this.forEachUnit();

        this.chooseBestMicro();

        this.beforePlay();

        if(in.unitController.canAttack()) this.attack(true);
        if(in.unitController.canMove()) this.move();
        if(in.unitController.canAttack()) this.attack(false);
        if(objectiveLocation != null) this.claimObjective();

        this.afterPlay();

        // debug objective
        if(objectiveLocation != null) {
            in.unitController.drawPoint(objectiveLocation, "red");
        }
    }


    // Unit utils
    int unitTypeToInt(UnitType unitType) {
        UnitType types[] = UnitType.values();
        for(int i = 0; i < types.length; i++) {
            if(types[i] == unitType) return i;
        }
        return -1;
    }

    UnitType intToUnitType(int type) {
        return UnitType.values()[type];
    }

    public void addCocoonUnit(UnitInfo unit) {
        if(unit.isCocoon() && unit.getTeam() == in.unitController.getTeam()) {
            increaseCounterUnitType(unit.getType());
            in.counter.increaseValueByOne(in.constants.SHARED_UNIT_COUNTER);
        }
    }

    // Unit counters

    public void increaseCounterUnitType(UnitType unitType) {
        int type = unitTypeToInt(unitType);
        increaseCounterUnitType(type);
    }

    public void increaseCounterUnitType(int type) {
        int counterId = getCounterIdFromType(type);
        in.counter.increaseValueByOne(counterId);
    }

    public int getCounterValueUnitType(UnitType unitType) {
        int type = unitTypeToInt(unitType);
        return getCounterValueUnitType(type);
    }

    public int getCounterValueUnitType(int type) {
        int counterId = getCounterIdFromType(type);
        return in.counter.read(counterId);
    }

    public int getCounterIdFromType(int type) {
        return in.constants.SHARED_UNIT_COUNTER_TYPE + (type - 1) * Counter.getCounterSpace();
    }

    public static int getCountersUnitTypeSpace() {
        return Counter.getCounterSpace() * UnitType.values().length;
    }


}
