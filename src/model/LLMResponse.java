package model;

/**
 * One model's answer. Immutable: make a new one instead of changing it.
 */
public final class LLMResponse {
    private final String modelName;
    private final ResponseStatus status;
    private final String text;
    private final long responseTimeMs;
    private final String errorMessage;

    private LLMResponse(String modelName, ResponseStatus status, String text,
                        long responseTimeMs, String errorMessage) {
        this.modelName = modelName;
        this.status = status;
        this.text = text;
        this.responseTimeMs = responseTimeMs;
        this.errorMessage = errorMessage;
    }

    public static LLMResponse waiting(String modelName) {
        return new LLMResponse(modelName, ResponseStatus.WAITING, "", 0, null);
    }

    public static LLMResponse running(String modelName) {
        return new LLMResponse(modelName, ResponseStatus.RUNNING, "", 0, null);
    }

    public static LLMResponse success(String modelName, String text, long timeMs) {
        return new LLMResponse(modelName, ResponseStatus.SUCCESS, text, timeMs, null);
    }

    public static LLMResponse failure(String modelName, String error, long timeMs) {
        return new LLMResponse(modelName, ResponseStatus.FAILED, error, timeMs, error);
    }

    public static LLMResponse timeout(String modelName, String error, long timeMs) {
        return new LLMResponse(modelName, ResponseStatus.TIMEOUT, error, timeMs, error);
    }

    public static LLMResponse invalid(String modelName, String reason, long timeMs) {
        return new LLMResponse(modelName, ResponseStatus.INVALID_RESPONSE, reason, timeMs, reason);
    }

    public String getModelName() {
        return modelName;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getStatusString() {
        return status.getLabel();
    }

    /** The answer on success, or the error message otherwise, so the view can always show it. */
    public String getText() {
        return text;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    /** Null when the request succeeded or hasn't finished. */
    public String getErrorMessage() {
        return errorMessage;
    }
}
