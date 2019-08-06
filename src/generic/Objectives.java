package generic;

import bugwars.*;

import java.util.stream.IntStream;

public class Objectives {

    private Injection in;
    private UnitController uc;

    public int MAX_OBJECTIVES = 20;
    public int OBJECTIVE_SIZE;

    Objectives(Injection in) {
        this.in = in;
        this.uc = in.unitController;
        Objective tmp = new Objective(in);
        this.OBJECTIVE_SIZE = tmp.getObjectiveSize();
    }

    private int getObjectiveId(int type, int num) {
        return in.constants.SHARED_OBJECTIVES_ID + (type - 1) * OBJECTIVE_SIZE * MAX_OBJECTIVES + num * OBJECTIVE_SIZE;
    }

    public void addObjective(Objective objective) {
        int type = objective.getObjectiveClass();
        for (int i = 0; i < MAX_OBJECTIVES; i++) {
            int id = getObjectiveId(type, i);
            if (id != 0) {
                objective.save(id);
                in.map.setValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, objective.getLocation(), id);
                break;
            }
        }
    }

    public Objective getObjective(int id) {
        return new Objective(in, id);
    }

    public Objective getObjective(Location loc) {
        int id = in.map.getValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, loc);
        return getObjective(id);
    }

    public void removeObjective(int id) {
        for(int i = 0; i < OBJECTIVE_SIZE; i++) {
            in.unitController.write(id, 0);
        }
    }

    public void removeObjective(Location loc) {
        int id = in.map.getValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, loc);
        removeObjective(id);
    }

    public boolean existsObjectiveInLocation(Location loc) {
        return 0 != in.map.getValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, loc);
    }

}
