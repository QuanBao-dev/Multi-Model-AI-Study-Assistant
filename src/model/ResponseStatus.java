package model;

/**
 * The state of one model's answer.
 */
public enum ResponseStatus {
    WAITING("Waiting"),
    RUNNING("Running..."),
    SUCCESS("Success"),
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

    /**
     * The label followed by how long the request took in ms, e.g. "Success - 1200
     * ms".
     */
    public String getLabelWithTime(long timeMs) {
        return label + " - " + timeMs + " ms";
    }
}
