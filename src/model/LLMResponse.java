package model;

/**
 * One model's answer. Immutable: make a new one instead of changing it.
 */
public final class LLMResponse {
    private final ResponseStatus status;
    private final String text;

    private LLMResponse(ResponseStatus status, String text) {
        this.status = status;
        this.text = text;
    }

    public static LLMResponse waiting() {
        return new LLMResponse(ResponseStatus.WAITING, "");
    }

    public static LLMResponse running() {
        return new LLMResponse(ResponseStatus.RUNNING, "");
    }

    public static LLMResponse success(String text) {
        return new LLMResponse(ResponseStatus.SUCCESS, text);
    }

    public static LLMResponse failure(String error) {
        return new LLMResponse(ResponseStatus.FAILURE, error);
    }

    public static LLMResponse timeout() {
        return new LLMResponse(ResponseStatus.TIMEOUT, "No answer in time.");
    }

    public static LLMResponse invalid(String reason) {
        return new LLMResponse(ResponseStatus.INVALID, reason);
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getStatusString() {
        return status.getLabel();
    }

    public String getText() {
        return text;
    }
}
