package santosh.models;

import santosh.CreditCardTypes;

public class CreditCardValidationResult {
    private final boolean valid;
    private final CreditCardTypes type;

    public CreditCardValidationResult(boolean valid, CreditCardTypes type) {
        this.valid = valid;
        this.type = type;
    }

    public boolean isValid() {
        return valid;
    }

    public CreditCardTypes getType() {
        return type;
    }
}
