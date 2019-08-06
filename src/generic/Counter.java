package generic;

public class Counter {

    private Injection in;
    
    public int COUNTERS_SPACE;

    Counter(Injection in) {
        this.in = in;
        COUNTERS_SPACE = this.getCounterSpace();
    }
    
    public void reset(int key) {
        for(int i = 0; i < COUNTERS_SPACE; i++) {
            in.unitController.write(key + i, 0);
        }
    }

    public void roundClear(int key) {
        in.unitController.write(key + (in.unitController.getRound() + 1)%COUNTERS_SPACE, 0);
    }

    public void increaseValue(int key, int amount) {
        this.roundClear(key);
        int realId = key + in.unitController.getRound()%COUNTERS_SPACE;
        int value = in.unitController.read(realId);
        in.unitController.write(realId, value + amount);
    }

    public void increaseValueByOne(int key) {
        this.increaseValue(key, 1);
    }

    public int read(int key) {
        this.roundClear(key);
        int realId = key + (in.unitController.getRound() - 1)%COUNTERS_SPACE;
        int realIdThisRound = key + (in.unitController.getRound())%COUNTERS_SPACE;
        return Math.max(in.unitController.read(realId), in.unitController.read(realIdThisRound));
    }

    public int readThisRoundOnly(int key) {
        int realId = key + (in.unitController.getRound())%COUNTERS_SPACE;
        return in.unitController.read(realId);
    }

    public int getCounterSpace() {
        return 3;
    }
}
