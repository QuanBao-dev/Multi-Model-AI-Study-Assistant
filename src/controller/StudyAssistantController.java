package controller;

import java.util.concurrent.TimeUnit;
import java.util.*;
import javax.swing.*;
import model.*;
import view.*;
import strategy.LLMStrategy;

public class StudyAssistantController {
    private static final long REQUEST_TIMEOUT_SECONDS = 15;
    private final QuerySession model;
    private final List<LLMStrategy> strategies;
    private JButton askButton = new JButton("Ask Models");
    private JTextArea questionArea = new JTextArea(3, 60);

    public StudyAssistantController(StudyAssistantView view, QuerySession model, List<LLMStrategy> strategies) {
        this.model = model;
        this.strategies = List.copyOf(strategies);
        askButton.addActionListener(e -> askModels());
        updateModelNames(strategies);
        view.buildInterface(askButton, questionArea);
    }

    private void updateModelNames(List<LLMStrategy> strategies) {
        ArrayList<String> modelNameList = new ArrayList<>();
        for (LLMStrategy strategy : strategies) {
            modelNameList.add(strategy.getDisplayName());
        }

        model.setModelNames(modelNameList);
    }

    private void askModels() {
        String question = questionArea.getText().trim();

        if (question.isBlank()) {
            JOptionPane.showMessageDialog(null, "Please enter a question.", "Input error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.beginQuery(question);

        for (LLMStrategy strategy : strategies) {
            startRequest(strategy, question);
        }
    }

    private void startRequest(LLMStrategy strategy, String question) {
        long start = System.currentTimeMillis();
        String modelName = strategy.getDisplayName();
        strategy.generate(question)
                .orTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .thenAccept(response -> model.updateResponse(modelName, response))
                .exceptionally(error -> {
                    LLMResponse response;

                    if (error.getCause() instanceof TimeoutException) {
                        response = LLMResponse.timeout(modelName, "Request timed out.", elapsed(start));
                    } else {
                        response = LLMResponse.failure(modelName, "Request failed.", elapsed(start));
                    }

                    model.updateResponse(modelName, response);
                    return null;
                });
    }

    private static long elapsed(long start) {
        return System.currentTimeMillis() - start;
    }
}