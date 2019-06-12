package data;

public class State implements Comparable<State> {

    private String illnessName;

    private Integer illnessPriority;

    public State(String illnessName, Integer illnessPriority) {
        this.illnessName = illnessName;
        this.illnessPriority = illnessPriority;
    }

    @Override
    public int compareTo(State o) {
        return o.illnessPriority.compareTo(this.illnessPriority);
    }
}