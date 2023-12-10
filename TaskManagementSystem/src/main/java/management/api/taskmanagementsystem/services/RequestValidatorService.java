package management.api.taskmanagementsystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RequestValidatorService {
    private final StringValidator stringValidator;
    public RequestValidatorService(@Autowired StringValidator stringValidator) {
        this.stringValidator = stringValidator;
    }

    /**
     * Validate request body fields
     * @param requestBody is a map of request body
     * @param rules is required fields
     * @return ResponseEntity that contains error code and message or ok
     * @see StringValidator
    */
    public ResponseEntity<Map<String, Object>> validateRequestBody(Map<String, String> requestBody, String[] rules) {
        for (String rule : rules) {
            if (!requestBody.containsKey(rule) || !stringValidator.isValid(requestBody.get(rule))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required field: " + rule));
            }
        }
        return ResponseEntity.ok().build();
    }
}
