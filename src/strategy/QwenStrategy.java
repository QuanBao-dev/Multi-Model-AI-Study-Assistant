package strategy;

import client.OllamaClient;

/**
 * Strategy for generating responses with the Qwen Ollama model.
 */
public class QwenStrategy extends AbstractLLMStrategy {

    public static final String DISPLAY_NAME = "Qwen";

    public static final String MODEL_ID = "qwen3:1.7b";

    /**
     * Returns the display name used by the user interface.
     *
     * @return "Qwen"
     */
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /**
     * Creates a Qwen strategy using the shared Ollama client.
     *
     * @param client the client used to send requests
     * @throws NullPointerException if client is null
     */
    public QwenStrategy(OllamaClient client) {
        super(client, DISPLAY_NAME, MODEL_ID);
    }
}
