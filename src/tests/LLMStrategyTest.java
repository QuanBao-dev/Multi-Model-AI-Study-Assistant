package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import model.LLMResponse;
import model.ResponseStatus;
import strategy.LLMStrategy;

public class LLMStrategyTest {
    @Test
    public void mockStrategyCanBeUsedThroughLLMStrategyInterface() {
        LLMStrategy strategy = new MockLLMStrategy();

        assertEquals("Mock", strategy.getDisplayName());

        LLMResponse response = strategy
                .generate("What is polymorphism?")
                .join();

        assertNotNull(response);
        assertEquals("Mock", response.getModelName());
        assertEquals("Mock response", response.getText());
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }

    private static class MockLLMStrategy implements LLMStrategy {

        @Override
        public String getDisplayName() {
            return "Mock";
        }

        @Override
        public CompletableFuture<LLMResponse> generate(String question) {
            LLMResponse response = LLMResponse.success(
                    "Mock",
                    "Mock response",
                    10);

            return CompletableFuture.completedFuture(response);
        }
    }
}
