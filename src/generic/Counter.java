package generic;

import bugwars.*;

public class Counter {

    private Injection in;
    
    public int COUNTERS_SPACE;

    Counter(Injection in) {
        this.in = in;
        COUNTERS_SPACE = getCounterSpace();
    }
    
    public void reset(int key) {
        for(int i = 0; i < COUNTERS_SPACE; i++) {
            in.unitController.write(key + i, 0);
        }
    }

    public void roundClear(int key) {
        int shift = in.unitController.getRound() & 1;
        if(in.unitController.read(key + shift) != in.unitController.getRound()) {
            in.unitController.write(key + shift, in.unitController.getRound());
            in.unitController.write(key + shift + 2, 0);
        }
    }

    public void increaseValue(int key, int amount) {
        this.roundClear(key);

        int shift = in.unitController.getRound() & 1;

        int realId = key + 2 + shift;
        int value = in.unitController.read(realId);
        in.unitController.write(realId, value + amount);
    }

    public void increaseValueByOne(int key) {
        this.increaseValue(key, 1);
    }

    public int read(int key) {
        int lshift = in.unitController.getRound() & 1;
        int rshift = (in.unitController.getRound() + 1) & 1;

        int left = 0;
        int right = 0;

        if(in.unitController.read(key + lshift) == in.unitController.getRound()) {
            left = in.unitController.read(key + lshift + 2);
        }

        if(in.unitController.read(key + rshift) == in.unitController.getRound() - 1) {
            right = in.unitController.read(key + rshift + 2);
        }

        return Math.max(left, right);
    }

    public int readThisRoundOnly(int key) {
        int lshift = in.unitController.getRound() & 1;
        int left = 0;
        if(in.unitController.read(key + lshift) != in.unitController.getRound()) {
            left = in.unitController.read(key + lshift + 2);
        }
        return left;
    }

    // space

    public static int getCounterSpace() {
        return 4;
    }
}
