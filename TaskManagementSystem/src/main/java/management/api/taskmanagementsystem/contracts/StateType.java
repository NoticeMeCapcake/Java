package management.api.taskmanagementsystem.contracts;

public enum StateType {
    DONE(5),
    IN_PROGRESS(10),
    NOT_STARTED(15);
    private final int value;

    StateType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
