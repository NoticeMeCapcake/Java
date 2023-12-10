package management.api.taskmanagementsystem.services;

import management.api.taskmanagementsystem.contracts.Validator;
import org.springframework.stereotype.Service;

@Service
public class StringValidator implements Validator<String> {
    public boolean isValid(String value) {
        return value != null && !value.isEmpty() && !value.isBlank();
    }
}
