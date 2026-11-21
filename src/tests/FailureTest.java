package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import controller.StudyAssistantController;
import model.*;
import strategy.LLMStrategy;
import view.StudyAssistantView;

public class FailureTest {
    @Test
    public void exceptionalMockCompletionResultsInFailedResponse() {
        LLMStrategy failingStrategy = new MockLLMStrategy();

        QuerySession model = new QuerySession();
        StudyAssistantController controller = new StudyAssistantController(new StudyAssistantView(model),
                model, List.of());
        LLMResponse failedResponse = controller.request(failingStrategy, "What is polymorphism?", 15).join();

        assertNotNull(failedResponse);
        assertEquals(ResponseStatus.FAILED, failedResponse.getStatus());
        assertEquals("Mock", failedResponse.getModelName());
    }

    private static class MockLLMStrategy implements LLMStrategy {

        @Override
        public String getDisplayName() {
            return "Mock";
        }

        @Override
        public CompletableFuture<LLMResponse> generate(String question) {
            return CompletableFuture.failedFuture(
                    new RuntimeException("Mock error"));
        }
    }
}
