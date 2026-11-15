package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Sends prompts to the local Ollama server
 * and returns the generated response text.
 */
public class OllamaClient {

    // Create one HTTP client for sending requests to Ollama.
    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Sends a prompt asynchronously to the specified Ollama model.
     * 
     * Preconditions: modelName and prompt is not null or blank.
     * Postcondition: The returned future completes with the generated response
     * text, or completes exceptionally if the request fails.
     *
     * @param modelName the name of the Ollama model to use
     * @param prompt    the question or instruction sent to the model
     * @return a future containing the generated response text
     */
    public CompletableFuture<String> generate(
            String modelName,
            String prompt) {

        // Create the JSON request body required by Ollama.
        String json = "{\"model\":\"" + escapeJson(modelName)
                + "\",\"prompt\":\"" + escapeJson(prompt)
                + "\",\"stream\":false}";

        // Build the HTTP request.
        HttpRequest request = HttpRequest.newBuilder()

                // Specify the Ollama API address.
                .uri(URI.create(
                        "http://localhost:11434/api/generate"))

                // Tell Ollama that the request body contains JSON.
                .header(
                        "Content-Type",
                        "application/json")

                // Send the JSON data using HTTP POST.
                .POST(
                        HttpRequest.BodyPublishers
                                .ofString(json))

                // Finish building the request.
                .build();

        // Send the HTTP request asynchronously.
        return client.sendAsync(
                request,
                HttpResponse.BodyHandlers.ofString())

                // Process the response when it becomes available.
                .thenApply(response -> {

                    // Check whether Ollama returned a successful response.
                    if (response.statusCode() != 200) {

                        // Report an error if the request failed.
                        throw new RuntimeException(
                                "Ollama HTTP error "
                                        + response.statusCode());
                    }

                    // Extract and return the generated text.
                    return getResponseText(
                            response.body());
                });
    }

    /**
     * Extract the generated text from Ollama's JSON response.
     * 
     * Precondition: The JSON string contains a valid response field.
     * Postcondition: Returns the generated text with escaped characters decoded.
     * 
     * @param json the raw JSON response returned by Ollama
     * @return the generated response text
     */
    private String getResponseText(String json) {
        // Locate the beginning of the "response" value.
        String marker = "\"response\":\"";

        // Find where the actual generated text starts.
        int start = json.indexOf(marker)
                + marker.length();

        // Return only the generated response text.
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char ch = json.charAt(i);

            if (!escaped && ch == '"') {
                break;
            }

            if (!escaped && ch == '\\') {
                escaped = true;
                continue;
            }

            if (escaped) {
                escaped = false;
                if (ch == 'u' && i + 4 < json.length()) {
                    String hex = json.substring(i + 1, i + 5);
                    result.append((char) Integer.parseInt(hex, 16));
                    i += 4;
                    continue;
                }

                result.append(switch (ch) {
                    case 't' -> '\t';
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 'b' -> '\b';
                    case 'f' -> '\f';
                    default -> ch;
                });
                continue;
            }

            result.append(ch);
        }

        return result.toString();
    }

    /**
     * Escapes special characters in a string so that it can be safely embedded
     * as a JSON string value.
     *
     * @param value the raw string to escape
     * @return the escaped string safe for use inside a JSON string literal
     */
    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
