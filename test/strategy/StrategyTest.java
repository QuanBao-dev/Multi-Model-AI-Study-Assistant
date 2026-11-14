package strategy;

import model.LLMResponse;
import model.ResponseStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Strategy-layer tests (JUnit 4). For JUnit 5 change the imports to
 * org.junit.jupiter.api.* . No real LLM or network is used and no exact LLM text
 * is compared. strategy.FakeOllamaClient stands in for the instructor's OllamaClient.
 */
public class StrategyTest {

    private static LLMResponse run(LLMStrategy s) throws Exception {
        return s.generate("What is polymorphism?").get(5, TimeUnit.SECONDS);
    }

    // ---------- Strategy substitution ----------

    @Test
    public void strategySubstitution_allStrategiesUsableThroughInterface() throws Exception {
        FakeOllamaClient client = new FakeOllamaClient("ok");
        List<LLMStrategy> strategies = List.of(
                new LlamaStrategy(client), new GemmaStrategy(client), new QwenStrategy(client),
                new MockLLMStrategy("Mock", MockLLMStrategy.Mode.SUCCESS));
        for (LLMStrategy s : strategies) {
            LLMResponse r = run(s);
            assertEquals(ResponseStatus.SUCCESS, r.getStatus());
        }
    }

    @Test
    public void eachStrategyUsesItsOwnModelId() throws Exception {
        FakeOllamaClient client = new FakeOllamaClient("ok");
        run(new LlamaStrategy(client));
        Assert.assertEquals("llama3.2:1b", client.getLastModelName());
        run(new GemmaStrategy(client));
        Assert.assertEquals("gemma3:1b", client.getLastModelName());
        run(new QwenStrategy(client));
        Assert.assertEquals("qwen3:1.7b", client.getLastModelName());
    }

    @Test
    public void displayNames_matchInstructions() {
        FakeOllamaClient client = new FakeOllamaClient("ok");
        assertEquals("Llama", new LlamaStrategy(client).getDisplayName());
        assertEquals("Gemma", new GemmaStrategy(client).getDisplayName());
        assertEquals("Qwen", new QwenStrategy(client).getDisplayName());
    }

    // ---------- Success ----------

    @Test
    public void success_validReplyBecomesSuccess() throws Exception {
        LLMResponse r = run(new LlamaStrategy(new FakeOllamaClient("Polymorphism means many forms.")));
        assertEquals(ResponseStatus.SUCCESS, r.getStatus());
        assertEquals("Polymorphism means many forms.", r.getText());
    }

    @Test
    public void success_jsonEscapesInReplyAreDecoded() throws Exception {
        // exactly what the provided client returns: raw text between the JSON quotes
        LLMResponse r = run(new LlamaStrategy(new FakeOllamaClient("Line1\\nLine2 caf\\u00e9")));
        assertEquals("Line1\nLine2 café", r.getText());
    }

    @Test
    public void prompt_specialCharactersAreEscapedBeforeSending() throws Exception {
        FakeOllamaClient client = new FakeOllamaClient("ok");
        new LlamaStrategy(client).generate("He said \"hi\"\nline2").get(5, TimeUnit.SECONDS);
        Assert.assertEquals("He said \\\"hi\\\"\\nline2", client.getLastPrompt());
    }

    // ---------- Invalid response ----------

    @Test
    public void invalidResponse_emptyTextBecomesInvalid() throws Exception {
        assertEquals(ResponseStatus.INVALID,
                run(new GemmaStrategy(new FakeOllamaClient("   "))).getStatus());
    }

    @Test
    public void invalidResponse_mockEmptyMode() throws Exception {
        assertEquals(ResponseStatus.INVALID,
                run(new MockLLMStrategy("Mock", MockLLMStrategy.Mode.EMPTY)).getStatus());
    }

    // ---------- Qwen reasoning text ----------

    @Test
    public void qwen_thinkBlockIsStripped() throws Exception {
        LLMResponse r = run(new QwenStrategy(new FakeOllamaClient("<think>hmm\\n</think>\\n\\nFinal answer")));
        assertEquals("Final answer", r.getText());
    }

    @Test
    public void qwen_thinkBlockWithGoStyleEscapesIsStripped() throws Exception {
        LLMResponse r = run(new QwenStrategy(
                new FakeOllamaClient("\\u003cthink\\u003ehmm\\u003c/think\\u003eFinal answer")));
        assertEquals("Final answer", r.getText());
    }

    @Test
    public void qwen_unclosedThinkBecomesInvalid() throws Exception {
        assertEquals(ResponseStatus.INVALID,
                run(new QwenStrategy(new FakeOllamaClient("<think>ran out of tokens"))).getStatus());
    }

    // ---------- Truncation by the provided client ----------

    @Test
    public void truncatedReply_isMarked() throws Exception {
        // provided client cuts at an escaped quote, leaving a trailing backslash
        LLMResponse r = run(new LlamaStrategy(new FakeOllamaClient("He said \\")));
        assertEquals(ResponseStatus.SUCCESS, r.getStatus());
        assertEquals("He said" + JsonText.TRUNCATED_NOTE, r.getText());
    }

    // ---------- Failure ----------

    @Test
    public void failure_clientErrorCompletesExceptionally() throws Exception {
        LLMStrategy s = new LlamaStrategy(new FakeOllamaClient(new RuntimeException("Ollama HTTP error 500")));
        try {
            run(s);
            fail("expected exceptional completion");
        } catch (ExecutionException e) {
            assertTrue(e.getCause().getMessage().contains("500"));
        }
    }

    @Test
    public void failure_mockCompletesExceptionally() {
        assertTrue(new MockLLMStrategy("Mock", MockLLMStrategy.Mode.FAIL)
                .generate("q").isCompletedExceptionally());
    }

    @Test
    public void failure_blankOrNullQuestionIsRejected() {
        LLMStrategy s = new LlamaStrategy(new FakeOllamaClient("ok"));
        assertTrue(s.generate("  ").isCompletedExceptionally());
        assertTrue(s.generate(null).isCompletedExceptionally());
    }

    // ---------- Timeout ----------

    @Test
    public void timeout_slowMockExceedsAllowedTime() {
        CompletableFuture<LLMResponse> f =
                new MockLLMStrategy("Mock", MockLLMStrategy.Mode.SLOW, 600)
                        .generate("q").orTimeout(100, TimeUnit.MILLISECONDS);
        try {
            f.join();
            fail("expected timeout");
        } catch (CompletionException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
        }
    }
}