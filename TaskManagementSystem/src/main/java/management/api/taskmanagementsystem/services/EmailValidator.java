package management.api.taskmanagementsystem.services;

import management.api.taskmanagementsystem.contracts.Validator;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class EmailValidator implements Validator<String> {
    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,11}$");
    public boolean isValid(String email) {
        return emailPattern.matcher(email).matches();
    }
}
