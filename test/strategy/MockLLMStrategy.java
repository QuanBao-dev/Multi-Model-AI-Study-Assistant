package strategy;

import model.LLMResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Test double for the JUnit suite (strategy substitution, success, failure,
 * timeout and invalid-response tests). Uses the same LLMStrategy interface as
 * the real strategies, so the Controller cannot tell the difference.
 */
public class MockLLMStrategy implements LLMStrategy {

    public enum Mode { SUCCESS, FAIL, SLOW, EMPTY }

    private final String displayName;
    private final Mode mode;
    private final long delayMs;

    public MockLLMStrategy(String displayName, Mode mode, long delayMs) {
        this.displayName = displayName;
        this.mode = mode;
        this.delayMs = delayMs;
    }

    public MockLLMStrategy(String displayName, Mode mode) {
        this(displayName, mode, 0);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public CompletableFuture<LLMResponse> generate(String question) {
        switch (mode) {
            case FAIL:
                return CompletableFuture.failedFuture(
                        new RuntimeException("Simulated connection failure"));
            case EMPTY:
                return CompletableFuture.completedFuture(
                        LLMResponse.invalid(displayName, "Empty response", 0));
            case SLOW:
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return LLMResponse.success(displayName, "late answer", delayMs);
                });
            case SUCCESS:
            default:
                return CompletableFuture.completedFuture(
                        LLMResponse.success(displayName, "Mock answer to: " + question, 1));
        }
    }
}
