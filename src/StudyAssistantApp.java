import java.util.List;
import javax.swing.*;
import client.*;
import controller.*;
import model.*;
import strategy.*;
import view.*;

/**
 * This class helps run the Multi-Model AI Study Assistant application.
 */
public class StudyAssistantApp {
    /**
     * Starts the Study Assistant application. The application uses one shared
     * OllamaClient for the Llama, Gemma, and Qwen strategies. It then creates the
     * MVC components and initializes the controller.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OllamaClient client = new OllamaClient();

            List<LLMStrategy> strategies = List.of(
                    new LlamaStrategy(client),
                    new GemmaStrategy(client),
                    new QwenStrategy(client));

            QuerySession model = new QuerySession();
            StudyAssistantView view = new StudyAssistantView(model);
            new StudyAssistantController(view, model, strategies);
        });
    }
}
