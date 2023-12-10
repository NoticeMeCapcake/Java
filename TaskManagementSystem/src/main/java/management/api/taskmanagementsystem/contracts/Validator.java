package management.api.taskmanagementsystem.contracts;

public interface Validator<T> {
    boolean isValid(T value);
}
