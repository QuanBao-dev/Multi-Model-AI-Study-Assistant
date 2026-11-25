package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import model.*;

public class ObserverNotificationTest {
    @Test
    public void observerIsNotifiedWhenResponseIsUpdated() {
        QuerySession model = new QuerySession();
        Observer observer = new Observer();

        model.addObserver(observer);

        LLMResponse response = LLMResponse.success("Mock", "Test response", 100);

        model.updateResponse("Mock", response);

        assertEquals(1, observer.notificationCount);
    }

    private static class Observer implements ModelObserver {
        private int notificationCount = 0;

        @Override
        public void modelChanged() {
            notificationCount++;
        }
    }

}
