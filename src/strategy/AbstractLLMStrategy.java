package strategy;

import model.LLMResponse;
import studyassistant.client.OllamaClient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public abstract class AbstractLLMStrategy implements LLMStrategy {

    private final OllamaClient client;
    private final String displayName;
    private final String modelId;

    protected AbstractLLMStrategy(OllamaClient client, String displayName, String modelId){
        this.client = Objects.requireNonNull(client, "client");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.modelId = Objects.requireNonNull(modelId, "modelId");
    }

    @Override
    public String getDisplayName(){
        return displayName;
    }

    public String getModelId(){
        return modelId;
    }

    @Override
    public CompletableFuture<LLMResponse> generate(String question){
        if(question == null || question.isBlank()){
            return CompletableFuture.failedFuture(new IllegalArgumentException("Question must not be empty"));
        }

        final long startNanos = System.nanoTime();
        final CompletableFuture<String> raw;

        try{
            raw = client.generate(modelId, JsonText.escape(question));
        } catch (RuntimeException e){
            return CompletableFuture.failedFuture(e);
        }

        if(raw == null){
            return CompletableFuture.failedFuture(new IllegalStateException("OllamaClient returned no future"));
        }

        return raw.thenApply(text -> toResponse(JsonText.unescape(text), startNanos));
    }

    private LLMResponse toResponse(String rawText, long startNanos){
        long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
        String cleaned = postProcess(rawText);
        if(cleaned == null || cleaned.isBlank()){
            return LLMResponse.invalid(displayName, "Model " + modelId + " returned an empty response", elapsedMs);
        }
        return LLMResponse.success(displayName, cleaned.strip(), elapsedMs);
    }

    protected String postProcess(String rawText){
        return rawText;
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() + "[" + displayName + " -> " + modelId + "]";
    }
}
