package generic;

import bugwars.*;

public abstract class Unit {
    Injection in;
    Location objectiveLocation;

    Behaviour behaviour;

    BWMicroInfo[] BWMicroInfo;
    BWMicroInfo bestMicro;

    Unit(Injection in) {
        this.in = in;
        behaviour = Behaviour.SAFE;
    }

    public abstract void selectObjective();

    public void move() {

    }

    public boolean compareMicro(BWMicroInfo a, BWMicroInfo b) {
        // true means a is better than b
        return true;
    }

    public void claimObjective(){
        if(objectiveLocation != null) {
            int objective = in.objectives.getObjectiveIdInLocation(objectiveLocation);
            in.objectives.claimObjective(objective);
        }
    }

    public void updateMicro(){

    }

    public void chooseBestMicro() {

    }

    public void attack() {

    }

    public void beforePlay(){}

    public void afterPlay() {}

    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public void play() {
        this.beforePlay();

        this.selectObjective();
        this.updateMicro();
        this.chooseBestMicro();

        if(in.unitController.canAttack()) this.attack();
        if(in.unitController.canMove()) this.move();
        if(in.unitController.canAttack()) this.attack();
        if(objectiveLocation != null) this.claimObjective();

        this.afterPlay();
    }

}
