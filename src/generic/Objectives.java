package generic;

import bugwars.*;

public class Objectives {

    private Injection in;
    private UnitController uc;
    
    public int OBJECTIVE_SIZE;
    public int MAX_OBJECTIVES;

    Objectives(Injection in) {
        this.in = in;
        this.uc = in.unitController;
    }

    private int[] newEmptyObjective() {
        return new int[OBJECTIVE_SIZE];
    }

    private int getObjectiveId(int type, int num) {
        // types start at 1
        return in.constants.SHARED_OBJECTIVES_ID + (type - 1) * OBJECTIVE_SIZE* MAX_OBJECTIVES +
                num * OBJECTIVE_SIZE;
    }

    private void updateObjective(int id, int[] params) {
        if(params[0] != 0) uc.write(id, params[0]);
        if(params[1] != 0) uc.write(id + 1, params[1]);
        if(params[2] != 0) uc.write(id + 2, params[2]);
        if(params[3] != 0) uc.write(id + 3, params[3]);
        if(params[4] != 0) uc.write(id + 4, params[4]);

        if(params[9] != 0) uc.write(id + 9, params[9]);
        if(params[10] != 0) uc.write(id + 10, params[10]);
        if(params[11] != 0) uc.write(id + 11, params[11]);
        if(params[12] != 0) uc.write(id + 12, params[12]);
    }

    private void insertObjective(int id, int[] params) {
        uc.write(id, params[0]);
        uc.write(id + 1, params[1]);
        uc.write(id + 2, params[2]);
        uc.write(id + 3, params[3]);
        uc.write(id + 4, params[4]);

        // reset counters
        in.counter.reset(id + 6);

        // add location objective
        this.setObjectiveIdInLocation(params[2], params[3], id);

        uc.write(id + 9, params[9]);
        uc.write(id + 10, params[10]);
        uc.write(id + 11, params[11]);
        uc.write(id + 12, params[12]);
    }

    public int[] addObjective(int type, int[] params) {

        //todo: check that the objective doesn't exists, if it does maybe update it
        int maybeId = this.getObjectiveIdInLocation(params[2], params[3]);
        if(maybeId > 0) {
            return this.getObjective(maybeId, -1);
        }

        int worstObjective = 0;
        int worstObjectiveValue = Integer.MIN_VALUE;

        int lastId = -1;

        for(int i = 0; i < MAX_OBJECTIVES; i++) {
            int id = this.getObjectiveId(type, i);
            if(uc.read(id) == 0) {
                this.insertObjective(id, params);
                lastId = id;
                break;
            }
            else {
                int value = uc.read(id + 11);
                if (value > worstObjectiveValue){
                    worstObjective = id;
                    worstObjectiveValue = value;
                }
            }
        }

        if(lastId == -1 && worstObjective != 0 && params[11] < worstObjective) {
            this.removeObjectiveIdInLocation(uc.read(worstObjective + 2), uc.read(worstObjective + 3));
            this.insertObjective(worstObjective, params);
            lastId = worstObjective;
        }

        if(lastId == -1) return this.newEmptyObjective();

        return this.getObjective(lastId, -1);

    }

    public int[] getObjective(int id, int objectiveType) {
        int[] objective = this.newEmptyObjective();

        int readObjectiveType = uc.read(id);

        if(uc.read(id) != 0 && (objectiveType == -1 || objectiveType == readObjectiveType)) {
            objective[0] = readObjectiveType;
            objective[1] = uc.read(id + 1);
            objective[2] = uc.read(id + 2);
            objective[3] = uc.read(id + 3);
            objective[4] = uc.read(id + 4);
            objective[5] = id;

            // reset counters
            objective[6] = in.counter.read(id + 6);
            objective[7] = in.counter.readThisRoundOnly(id + 6);

            objective[9] = uc.read(id + 9);
            objective[10] = uc.read(id + 10);

            // value
            objective[11] = uc.read(id + 11);

            objective[12] = uc.read(id + 12);

        }

        return objective;
    }

    public int[] getObjective(Location loc) {
        int id = this.getObjectiveIdInLocation(loc);
        return getObjective(id,-1);
    }

    public int[][] getObjectives(int type, int objectiveType) {
        int[][] objectives = new int[MAX_OBJECTIVES][OBJECTIVE_SIZE];

        for(int i = 0; i < MAX_OBJECTIVES; i++) {
            int id = this.getObjectiveId(type, i);
            objectives[i] = this.getObjective(id, objectiveType);
        }

        return objectives;
    }

    public void removeObjective(int id) {
        // remove location
        this.removeObjectiveIdInLocation(uc.read(id + 2), uc.read(id + 3));

        for(int i = 0; i < OBJECTIVE_SIZE; i++) {
            uc.write(id + i, 0);
        }
    }

    public void removeObjective(Location loc) {
        int idObjectiveLocation = in.map.locationToInt(loc.x, loc.y);
        if (idObjectiveLocation == 0) return;
        removeObjective(getObjectiveIdInLocation(loc.x, loc.y));
    }

    public boolean existsObjectiveInLocation(int locX, int locY) {
        int idObjectiveLocation = in.map.locationToInt(locX, locY);
        if (idObjectiveLocation == 0) return false;
        return 0 < uc.read(in.constants.SHARED_OBJECTIVES_MAP_ID + idObjectiveLocation);
    }

    public int getObjectiveIdInLocation(int locX, int locY) {
        int idObjectiveLocation = in.map.locationToInt(locX, locY);
        return uc.read(in.constants.SHARED_OBJECTIVES_MAP_ID + idObjectiveLocation);
    }

    private void setObjectiveIdInLocation(int locX, int locY, int id) {
        int idObjectiveLocation = in.map.locationToInt(locX, locY);
        uc.write(in.constants.SHARED_OBJECTIVES_MAP_ID  + idObjectiveLocation, id);
    }

    private void removeObjectiveIdInLocation(int locX, int locY) {
        int idObjectiveLocation = in.map.locationToInt(locX, locY);
        uc.write(in.constants.SHARED_OBJECTIVES_MAP_ID  + idObjectiveLocation, 0);
    }

    public int getObjectiveIdInLocation(Location loc) {
        int idObjectiveLocation = in.map.locationToInt(loc);
        return uc.read(in.constants.SHARED_OBJECTIVES_MAP_ID + idObjectiveLocation);
    }

    public int getObjectiveType(int id) {
        return uc.read(id);
    }

    public void claimObjective(int idObjective) {
        in.counter.increaseValueByOne(idObjective + 6);
    }

    public void updateRoundAction(int idObjective) {
        uc.write(idObjective + 12, in.unitController.getRound());
    }
}
