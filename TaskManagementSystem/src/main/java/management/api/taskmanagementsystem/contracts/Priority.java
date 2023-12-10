package management.api.taskmanagementsystem.contracts;

// Potentially extendable with other priorities
public enum Priority {
    HIGH(5),
    MEDIUM(10),
    LOW(15);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
