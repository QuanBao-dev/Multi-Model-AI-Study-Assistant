package strategy;

import java.client.OllamaClient;

public class LlamaStrategy extends AbstractLLMStrategy {

    public static final String DISPLAY_NAME = "Llama";
    public static final String MODEL_ID = "llama3.2:1b";

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public LlamaStrategy(OllamaClient client){
        super(client, DISPLAY_NAME, MODEL_ID);
    }

}
