package pl.sk.coinTracker.Support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.validation.ObjectError;

import java.util.List;

public class Validation {

    public static final int WALLET_NAME_MAX_LENGTH = 45;
    public static final int TRANSACTION_NOTE_MAX_LENGTH = 70;

    public static ObjectNode getErrorResponse(String message) {

        ObjectNode response = new ObjectMapper().createObjectNode();
        ArrayNode errors = response.putArray("errors");

        errors.add(new ObjectMapper().createObjectNode()
                .put("message", message));
        return response;
    }

    public static ObjectNode getErrorResponse(List<ObjectError> errors) {

        ObjectNode response = new ObjectMapper().createObjectNode();
        ArrayNode e = response.putArray("errors");

        for (ObjectError error : errors)
            e.add(new ObjectMapper().createObjectNode()
                    .put("message", error.getDefaultMessage()));
        return response;
    }
}
