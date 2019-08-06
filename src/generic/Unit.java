package generic;

import bugwars.*;

public abstract class Unit {
    Injection in;
    Objective objective;
    Location objectiveLocation;

    int behaviour;

    MicroInfo[] MicroInfo;
    MicroInfo bestMicro;

    Unit(Injection in) {
        this.in = in;
        behaviour = in.behaviour.SAFE;
    }

    public abstract void selectObjective();

    public void move() {

    }

    public boolean compareMicro(MicroInfo a, MicroInfo b) {
        // true means a is better than b
        return true;
    }

    public void claimObjective(){
        if(objectiveLocation != null) {
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

    public void chooseBestMicro() {

    }

    public void attack() {

    }

    public void beforePlay(){}

    public void afterPlay() {}

    public void setBehaviour(int behaviour) {
        this.behaviour = behaviour;
    }

    public void play() {
        this.selectObjective();
        this.objectiveLocation = this.objective.getLocation();

        this.updateMicro();
        this.chooseBestMicro();

        this.beforePlay();

        if(in.unitController.canAttack()) this.attack();
        if(in.unitController.canMove()) this.move();
        if(in.unitController.canAttack()) this.attack();
        if(objectiveLocation != null) this.claimObjective();

        this.afterPlay();
    }

}
