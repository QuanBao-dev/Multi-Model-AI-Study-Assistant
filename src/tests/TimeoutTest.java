package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import controller.StudyAssistantController;
import model.*;
import strategy.*;
import view.*;

public class TimeoutTest {
    private static final long REQUEST_TIMEOUT_SECONDS = 0;

    @Test
    void slowMockCompletionResultsInTimeoutResponse() {
        LLMStrategy slowStrategy = new MockLLMStrategy();

        QuerySession model = new QuerySession();
        StudyAssistantController controller = new StudyAssistantController(new StudyAssistantView(model),
                model, List.of());

        LLMResponse timeoutResponse = controller.request(slowStrategy, "What is polymorphism?", REQUEST_TIMEOUT_SECONDS)
                .join();

        assertNotNull(timeoutResponse);
        assertEquals(ResponseStatus.TIMEOUT, timeoutResponse.getStatus());
        assertEquals("Mock", timeoutResponse.getModelName());
    }

    private static class MockLLMStrategy implements LLMStrategy {
        @Override
        public String getDisplayName() {
            return "Mock";
        }

        @Override
        public CompletableFuture<LLMResponse> generate(String question) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(3 * 1000); // sleep 3 seconds
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(exception);
                }

                return LLMResponse.success("Mock", "Late response", 200);
            });
        }
    }

}
