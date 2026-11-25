package strategy;

import model.LLMResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Defines the common interface for a language-model request strategy.
 */
public interface LLMStrategy {

    /**
     * Returns the display name used for the model's response panel.
     *
     * @return the user-facing model name
     */
    String getDisplayName();

    /**
     * Asynchronously generates a response to a study question.
     *
     * @param question the question or prompt sent to the model
     * @return a future that completes with the model response
     */
    CompletableFuture<LLMResponse> generate(String question);

}
