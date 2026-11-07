package strategy;

import ollama.OllamaClient;

public class LLamaStrategy extends AbstractLLMStrategy {

    public static final String DISPLAY_NAME = "Llama";
    public static final String MODEL_ID = "llama3.2:1b";

    public LLamaStrategy(OllamaClient client){
        super(client, DISPLAY_NAME, MODEL_ID);
    }

}
