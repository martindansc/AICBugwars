package generic;

import bugwars.*;

public class Objectives {

    private Injection in;
    private UnitController uc;

    public int MAX_OBJECTIVES;
    public int OBJECTIVE_SIZE;

    Objectives(Injection in) {
        this.in = in;
        this.uc = in.unitController;
        //Objective tmp = new Objective(in);
        this.OBJECTIVE_SIZE = Objective.getObjectiveSize();
        this.MAX_OBJECTIVES = Objectives.getMaxObjectives();
    }

    private int getObjectiveId(int type, int num) {
        return in.constants.SHARED_OBJECTIVES_ID + (type - 1) * OBJECTIVE_SIZE * MAX_OBJECTIVES + num * OBJECTIVE_SIZE;
    }

    public void addObjective(Objective objective, int type) {
        for (int i = 0; i < MAX_OBJECTIVES; i++) {
            int id = getObjectiveId(type, i);
            if (in.unitController.read(id) == 0) {
                objective.save(id);
                break;
            }
        }
    }

    public Objective getObjective(int id) {
        if(in.unitController.read(id) != 0) {
            return new Objective(in, id);
        }
        return null;
    }

    public Objective getObjective(Location loc) {
        int id = in.map.getValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, loc);
        return getObjective(id);
    }

    public Objective getObjective(int i, int type) {

        if(i >= MAX_OBJECTIVES) {
            in.unitController.println("Error, trying to get more objectives than the maximum");
        }

        int id = getObjectiveId(type, i);
        return getObjective(id);
    }

    public void removeObjective(int id) {
        Objective objectiveToRemove = new Objective(in, id);
        objectiveToRemove.remove();
    }

    public void removeObjective(Objective objectiveToRemove) {
        objectiveToRemove.remove();
    }

    public void removeObjective(Location loc) {
        int id = in.map.getValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, loc);
        removeObjective(id);
    }

    public boolean existsObjectiveInLocation(Location loc) {
        return 0 != in.map.getValueInLocation(in.constants.SHARED_OBJECTIVES_MAP_ID, loc);
    }

    // space
    public static int getObjectiveSpace() {
        return getMaxTypes() * Objective.getObjectiveSize() * getMaxObjectives();
    }

    public static int getMaxObjectives() {
        return 20;
    }

    public static int getMaxTypes() {
        return 20;
    }

}
