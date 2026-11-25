package strategy;

import client.OllamaClient;

/**
 * Strategy for generating responses with the Gemma Ollama model.
 */
public class GemmaStrategy extends AbstractLLMStrategy {

    public static final String DISPLAY_NAME = "Gemma";

    public static final String MODEL_ID = "gemma3:1b";

    /**
     * Returns the display name used by the user interface.
     *
     * @return "Gemma"
     */
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /**
     * Creates a Gemma strategy using the shared Ollama client.
     *
     * @param client the client used to send requests
     * @throws NullPointerException if client is null
     */
    public GemmaStrategy(OllamaClient client) {
        super(client, DISPLAY_NAME, MODEL_ID);
    }

}
