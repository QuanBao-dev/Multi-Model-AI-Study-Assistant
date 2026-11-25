package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import model.*;

public class QuerySessionTest {
  @Test
  void updateResponseStoresExpectedLLMResponse() {
    QuerySession model = new QuerySession();

    LLMResponse expected = LLMResponse.success(
        "Mock",
        "Polymorphism allows objects to take many forms.",
        100);

    model.updateResponse("Mock", expected);

    LLMResponse actual = model.getResponse("Mock");

    assertNotNull(actual);
    assertEquals("Mock", actual.getModelName());
    assertEquals(
        "Polymorphism allows objects to take many forms.",
        actual.getText());
    assertEquals(ResponseStatus.SUCCESS, actual.getStatus());
    assertEquals(100, actual.getResponseTimeMs());
  }
}
