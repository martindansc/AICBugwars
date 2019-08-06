package generic;

public class BWQueen extends Unit {

    BWQueen(Injection in) {
        super(in);
        behaviour = in.behaviour.SAFE;
    }

    @Override
    public void selectObjective() {

    }

}
