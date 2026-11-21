package strategy;

import java.util.regex.Pattern;
import client.OllamaClient;

public class QwenStrategy extends AbstractLLMStrategy {

    public static final String DISPLAY_NAME = "Qwen";
    public static final String MODEL_ID = "qwen3:1.7b";

    private static final Pattern THINK_BLOCK = Pattern.compile("(?s)<think>.*?</think>");

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public QwenStrategy(OllamaClient client){
        super(client, DISPLAY_NAME, MODEL_ID);
    }
}
