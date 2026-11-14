package strategy;

import java.util.regex.Pattern;
import studyassistant.client.OllamaClient;

public class QwenStrategy extends AbstractLLMStrategy {

    public static final String DISPLAY_NAME = "Qwen";
    public static final String MODEL_NAME = "qwen3:1.7b";

    private static final Pattern THINK_BLOCK = Pattern.compile("(?s)<think>.*?</think>");

    public QwenStrategy(OllamaClient client){
        super(client, DISPLAY_NAME, MODEL_NAME);
    }

    @Override
    protected String postProcess(String rawText){
        if(rawText == null){
            return null;
        }
        String withoutThinking = THINK_BLOCK.matcher(rawText).replaceAll("");
        if(withoutThinking.contains("<think>")){
            return null;
        }
        return withoutThinking.strip();
    }

}
