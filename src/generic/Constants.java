package generic;

import bugwars.UnitType;

import java.util.function.Supplier;

public class Constants {

    int counter = 0;

    public int SHARED_MAP_ID = 80000;
    public int SHARED_OBJECTIVES_ID;
    public int SHARED_OBJECTIVES_MAP_ID;

    public int SHARED_UNIT_COUNTER;
    public int SHARED_UNIT_COUNTER_TYPE;

    Constants() {
        SHARED_UNIT_COUNTER = add(Counter::getCounterSpace);
        SHARED_UNIT_COUNTER_TYPE = add(() -> Counter.getCounterSpace() * UnitType.values().length);

        SHARED_OBJECTIVES_ID = add(Objectives::getObjectiveSpace);

        SHARED_OBJECTIVES_MAP_ID = add(Map::getSimpleMapSpace);
    }

    private int add(Supplier<Integer> sizeFunction) {
        int previous = counter;
        counter += sizeFunction.get();
        return previous;
    }

}
