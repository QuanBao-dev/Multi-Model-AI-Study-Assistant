package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import controller.StudyAssistantController;
import model.*;
import strategy.LLMStrategy;
import view.StudyAssistantView;

public class InvalidResponseTest {
    @Test
    public void emptyResponseResultsInInvalidResponse() {
        LLMStrategy invalidStrategy = new MockLLMStrategy();
        LLMResponse response = StudyAssistantController.request(invalidStrategy, "", 15).join();

        assertNotNull(response);
        assertEquals(ResponseStatus.INVALID_RESPONSE, response.getStatus());
        assertEquals("Mock", response.getModelName());
    }

    private static class MockLLMStrategy implements LLMStrategy {

        @Override
        public String getDisplayName() {
            return "Mock";
        }

        @Override
        public CompletableFuture<LLMResponse> generate(String question) {
            LLMResponse response = LLMResponse.success("Mock", "", 10);

            return CompletableFuture.completedFuture(response);
        }
    }
}
