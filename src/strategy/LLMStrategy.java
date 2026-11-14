package strategy;

import model.LLMResponse;

import java.util.concurrent.CompletableFuture;


public interface LLMStrategy {

    String getDisplayName();

    CompletableFuture<LLMResponse> generate(String question);

}
