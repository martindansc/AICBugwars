package generic;

import bugwars.*;

public abstract class Unit {
    Injection in;
    Objective objective;
    Location objectiveLocation;

    int myType;

    int behaviour;

    Direction lastDirectionMoved;
    boolean moved = true;

    MicroInfo[] MicroInfo;
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
        if(bestMicro == null) {
            Direction dir = in.pathfinder.getNextLocationTarget(objectiveLocation);
            if(dir != null) {
                move(dir);
            }
        }

    }

    public boolean compareMicro(MicroInfo a, MicroInfo b) {
        // true means a is better than b
        return true;
    }

    public void claimObjective(){
        if(objective != null) {
            objective.claim();
        }
    }

    public void updateMicro(){
        Location myLocation = in.unitController.getLocation();
        MicroInfo[] microInfo = new MicroInfo[9];
        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            microInfo[i] = new MicroInfo(myLocation.add(directions[i]));
        }

        for (UnitInfo unitInfo : in.unitController.senseUnits()) {
            for(int i = 0; i < directions.length; i++) {
                microInfo[i].update(unitInfo);
            }
        }
    }

    public void chooseBestMicro() {}

    public void attack() {}

    public void beforeAnything() {}

    public void beforePlay(){}

    public void afterPlay() {}

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

        this.updateMicro();
        this.chooseBestMicro();

        this.beforePlay();

        if(in.unitController.canAttack()) this.attack();
        if(in.unitController.canMove()) this.move();
        if(in.unitController.canAttack()) this.attack();
        if(objectiveLocation != null) this.claimObjective();

        this.afterPlay();
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

    public void addCocoonUnits() {
        int number = 0;
        UnitInfo[] units = in.unitController.senseUnits(5);
        for (UnitInfo unit: units) {
            if(unit != null && unit.isCocoon()) {
                increaseCounterUnitType(unit.getType());
                number++;
            }
        }
        in.counter.increaseValue(in.constants.SHARED_UNIT_COUNTER, number);
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
