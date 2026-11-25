package strategy;

import model.LLMResponse;
import client.OllamaClient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Provides shared implementation logic for Ollama-backed strategies.
 */
public abstract class AbstractLLMStrategy implements LLMStrategy {

    private final OllamaClient client;
    private final String displayName;
    private final String modelId;

    /**
     * Creates an Ollama-backed strategy.
     *
     * @param client      the shared client used to communicate with Ollama
     * @param displayName the name shown to the user
     * @param modelId     the model identifier accepted by Ollama
     * @throws NullPointerException if any argument is {@code null}
     */
    protected AbstractLLMStrategy(OllamaClient client, String displayName, String modelId) {
        this.client = Objects.requireNonNull(client, "client");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.modelId = Objects.requireNonNull(modelId, "modelId");
    }

    /**
     * Returns the configured user-facing model name.
     *
     * @return the display name of this strategy
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sends a question through Ollama and converts the generated text into an
     * LLMResponse.
     *
     * @param question the question or prompt sent to the configured model
     * @return a future containing the model response
     */
    @Override
    public CompletableFuture<LLMResponse> generate(String question) {
        long start = System.currentTimeMillis();
        return client.generate(modelId, question)
                .thenApply(text -> LLMResponse.success(displayName, text, System.currentTimeMillis() - start));
    }
}
