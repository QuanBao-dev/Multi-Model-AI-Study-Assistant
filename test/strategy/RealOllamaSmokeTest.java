package strategy;

import model.LLMResponse;
import studyassistant.client.OllamaClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * MANUAL check, not a JUnit test: asks the three REAL Ollama models one question and
 * prints each answer as it arrives. Needs Ollama running with the three models pulled.
 *
 * Run it from IntelliJ (green arrow next to main). Optional: put a question in
 * Run > Edit Configurations > Program arguments.
 *
 * It only uses getStatusString() and getText(), so it works with any version of LLMResponse.
 */
public class RealOllamaSmokeTest {

    private static final long TIMEOUT_SECONDS = 60;

    public static void main(String[] args) throws Exception {
        String question = args.length > 0 ? String.join(" ", args) : "What is polymorphism?";
        OllamaClient client = new OllamaClient();
        List<LLMStrategy> strategies = List.of(
                new LlamaStrategy(client), new GemmaStrategy(client), new QwenStrategy(client));

        System.out.println("Question: " + question + "\n");
        long start = System.currentTimeMillis();

        // Start all three at once, exactly like the Controller does.
        CompletableFuture<?>[] all = new CompletableFuture<?>[strategies.size()];
        for (int i = 0; i < strategies.size(); i++) {
            LLMStrategy strategy = strategies.get(i);
            String name = strategy.getDisplayName();
            all[i] = strategy.generate(question)
                    .orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .thenAccept(r -> print(name, r.getStatusString(), r.getText(), start))
                    .exceptionally(error -> {
                        Throwable cause = error.getCause() != null ? error.getCause() : error;
                        print(name, "FAILED (" + cause.getClass().getSimpleName() + ")",
                                String.valueOf(cause.getMessage()), start);
                        return null;
                    });
        }
        CompletableFuture.allOf(all).join();
        System.out.println("\nAll three finished.");
    }

    private static synchronized void print(String name, String status, String text, long start) {
        System.out.println("=== " + name + " | " + status + " | +" + (System.currentTimeMillis() - start) + " ms");
        String shown = text == null ? "" : text.strip();
        System.out.println(shown.length() > 600 ? shown.substring(0, 600) + " ..." : shown);
        System.out.println();
    }
}
