package generic;

public class BWBeetle extends Unit {

    BWBeetle(Injection in) {
        super(in);
        behaviour = in.behaviour.SAFE;
    }

    @Override
    public void selectObjective() {
        objectiveLocation = in.unitController.getEnemyQueensLocation()[0];
    }
}
