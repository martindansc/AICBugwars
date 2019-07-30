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

    Injection(UnitController unitController, Unit unit) {
        this.unitController = unitController;
        macro = new Macro(this);
        map = new Map(this);
        constants = new Constants();
        counter = new Counter(this);
        this.unit = unit;
        objectives = new Objectives(this);
        pathfinder = new Pathfinder(this);

    }

}
