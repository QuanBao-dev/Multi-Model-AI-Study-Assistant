package strategy;

import client.OllamaClient;

/**
 * Strategy for generating responses with the Llama Ollama model.
 */
public class LlamaStrategy extends AbstractLLMStrategy {

    public static final String DISPLAY_NAME = "Llama";

    public static final String MODEL_ID = "llama3.2:1b";

    /**
     * Returns the display name used by the user interface.
     *
     * @return "Llama"
     */
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /**
     * Creates a Llama strategy using the shared Ollama client.
     *
     * @param client the client used to send requests
     * @throws NullPointerException if client is null
     */
    public LlamaStrategy(OllamaClient client) {
        super(client, DISPLAY_NAME, MODEL_ID);
    }

}
