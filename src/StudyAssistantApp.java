import java.util.List;
import javax.swing.*;
import client.*;
import controller.*;
import model.*;
import strategy.*;
import view.*;

public class StudyAssistantApp {
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
