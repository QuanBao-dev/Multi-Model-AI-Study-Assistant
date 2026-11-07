package strategy;

import ollama.OllamaClient;

public class GemmaStrategy extends AbstractLLMStrategy{

    public static final String DISPLAY_NAME = "Gemma";
    public static final String MODEL_ID = "gemma3:1b";

    public GemmaStrategy(OllamaClient client){
        super(client, DISPLAY_NAME, MODEL_ID);
    }

}
