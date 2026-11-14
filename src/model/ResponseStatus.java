package model;

/**
 * The state of one model's answer.
 */
public enum ResponseStatus {
    WAITING("Waiting"),
    RUNNING("Running..."),
    SUCCESS("Done"),
    FAILED("Failed"),
    TIMEOUT("Timed out"),
    INVALID_RESPONSE("Invalid response");

    private final String label;

    ResponseStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
