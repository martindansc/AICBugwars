package generic;

import bugwars.*;

public class Injection {

    public UnitController unitController;
    public Macro macro;
    public Map map;
    public Constants constants;
    public Counter counter;
    public Unit unit;
    public Objectives objectives;
    public Pathfinder pathfinder;
    public Behaviour behaviour;

    Injection(UnitController unitController) {
        this.unitController = unitController;
        constants = new Constants();
        macro = new Macro(this);
        map = new Map(this);
        counter = new Counter(this);
        objectives = new Objectives(this);
        pathfinder = new Pathfinder(this);
        behaviour = new Behaviour();
    }

    void setUnit(Unit unit) {
        this.unit = unit;
    }

}
