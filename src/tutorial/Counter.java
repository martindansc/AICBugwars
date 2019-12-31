package tutorial;


import bugwars.UnitController;

public class Counter {

    UnitController uc;
    public int COUNTERS_SPACE = 4;

    Counter(UnitController unitController) {
        uc = unitController;
    }
    
    public void reset(int key) {
        for(int i = 0; i < COUNTERS_SPACE; i++) {
            uc.write(key + i, 0);
        }
    }

    public void roundClear(int key) {
        int shift = uc.getRound() & 1;
        if(uc.read(key + shift) != uc.getRound()) {
            uc.write(key + shift, uc.getRound());
            uc.write(key + shift + 2, 0);
        }
    }

    public void increaseValue(int key, int amount) {
        this.roundClear(key);

        int shift = uc.getRound() & 1;

        int realId = key + 2 + shift;
        int value = uc.read(realId);
        uc.write(realId, value + amount);
    }

    public void increaseValueByOne(int key) {
        this.increaseValue(key, 1);
    }

    public int read(int key) {
        int lshift = uc.getRound() & 1;
        int rshift = (uc.getRound() + 1) & 1;

        int left = 0;
        int right = 0;

        if(uc.read(key + lshift) == uc.getRound()) {
            left = uc.read(key + lshift + 2);
        }

        if(uc.read(key + rshift) == uc.getRound() - 1) {
            right = uc.read(key + rshift + 2);
        }

        return Math.max(left, right);
    }

    public int readThisRoundOnly(int key) {
        int lshift = uc.getRound() & 1;
        int left = 0;
        if(uc.read(key + lshift) != uc.getRound()) {
            left = uc.read(key + lshift + 2);
        }
        return left;
    }

}
