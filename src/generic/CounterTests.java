package generic;

public class CounterTests extends TestUnit {

    int counter = 0;

    CounterTests(Injection in) {
        super(in);
    }

    public void play() {
        // check no more space than needed used
        if(in.unitController.read(Counter.getCounterSpace()) != 0) {
            in.unitController.println("ERROR!!! Counter test SPACE");
        }

        if(in.unitController.getRound() == 0) {
            in.counter.reset(counter);
            in.counter.increaseValueByOne(counter);
            in.counter.increaseValue(counter, 2);

            if(in.counter.read(counter) != 3) {
                in.unitController.println("ERROR!!! Counter test 0");
            }
            return;
        }

        if(in.unitController.getRound() == 1) {
            if(in.counter.read(counter) != 3) {
                in.unitController.println("ERROR!!! Counter test 1");
            }

            in.counter.increaseValueByOne(counter);
        }

        if(in.unitController.getRound() == 2) {
            if(in.counter.read(counter) != 1) {
                in.unitController.println("ERROR!!! Counter test 2");
            }
        }

        if(in.unitController.getRound() == 3) {
            if(in.counter.read(counter) != 0) {
                in.unitController.println("ERROR!!! Counter test 4");
            }
        }

        if(in.unitController.getRound() == 4) {
            if(in.counter.read(counter) != 0) {
                in.unitController.println("ERROR!!! Counter test 4");
            }
        }
    }
}
