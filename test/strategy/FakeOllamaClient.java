package strategy;

import studyassistant.client.OllamaClient;

import java.util.concurrent.CompletableFuture;


/**
 * Test double for the instructor's OllamaClient. It extends the real class, so the
 * strategies cannot tell the difference, but it never opens a network connection.
 * It records the prompt it was given and returns a canned reply (or a failure).
 */
public class FakeOllamaClient extends OllamaClient {

    private final String cannedReply;
    private final RuntimeException failure;
    private volatile String lastModelName;
    private volatile String lastPrompt;

    /** Replies with {@code cannedReply}, exactly as the provided client would hand it back. */
    public FakeOllamaClient(String cannedReply) {
        this.cannedReply = cannedReply;
        this.failure = null;
    }

    /** Fails the way a down server or HTTP error would. */
    public FakeOllamaClient(RuntimeException failure) {
        this.cannedReply = null;
        this.failure = failure;
    }

    @Override
    public CompletableFuture<String> generate(String modelName, String prompt) {
        lastModelName = modelName;
        lastPrompt = prompt;
        if (failure != null) {
            return CompletableFuture.failedFuture(failure);
        }
        return CompletableFuture.completedFuture(cannedReply);
    }

    public String getLastModelName() {
        return lastModelName;
    }

    public String getLastPrompt() {
        return lastPrompt;
    }
}