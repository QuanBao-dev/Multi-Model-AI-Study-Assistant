package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import controller.StudyAssistantController;
import model.*;
import strategy.*;

public class SuccessfulResponseTest {

    @Test
    void validMockResponseResultsInSuccess() {
        LLMStrategy mockStrategy = new MockLLMStrategy();

        LLMResponse response = StudyAssistantController
                .request(mockStrategy, "What is polymorphism?", 15)
                .join();

        assertNotNull(response);
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        assertEquals("Mock", response.getModelName());
        assertFalse(response.getText().isBlank());
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
