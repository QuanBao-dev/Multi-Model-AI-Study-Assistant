package controller;

import java.util.concurrent.*;
import java.util.*;
import javax.swing.*;
import model.*;
import view.*;
import strategy.*;

/**
 * This class is a controllers that handles user input, requests changes to
 * the model, and starts asynchronous requests for each language model, and
 * update the model with the results.
 */
public class StudyAssistantController {
    private static final long REQUEST_TIMEOUT_SECONDS = 15;
    private final QuerySession model;
    private final List<LLMStrategy> strategies;
    private JButton askButton = new JButton("Ask Models");
    private JTextArea questionArea = new JTextArea(3, 60);

    /**
     * Creates a controller and add a listener for GUI button.
     * 
     * Precondition: view, model, and strategies are not null
     * Postcondition: The Ask Models is connected to the controller, the modelNames
     * is updated, and the interface is built.
     *
     * @param view       the application view
     * @param model      the query-session model
     * @param strategies the language-model strategies
     */
    public StudyAssistantController(StudyAssistantView view, QuerySession model, List<LLMStrategy> strategies) {
        this.model = model;
        this.strategies = List.copyOf(strategies);
        askButton.addActionListener(e -> askModels());
        updateModelNames(strategies);
        view.buildInterface(askButton, questionArea);
    }

    /**
     * Updates the model with the display names of all available strategies.
     * 
     * Precondition: strategies is not null.
     * Postcondition: modelNames is updated.
     *
     * @param strategies the language-model strategies
     */
    private void updateModelNames(List<LLMStrategy> strategies) {
        ArrayList<String> modelNames = new ArrayList<>();
        for (LLMStrategy strategy : strategies) {
            modelNames.add(strategy.getDisplayName());
        }

        model.setModelNames(modelNames);
    }

    /**
     * Reads and validates the user's question, begins a new query, and starts
     * a request for each language-model strategy.
     * 
     * Precondition: questionArea has been initialized
     * Postcondition: If the question is blank, no request is started. Otherwise,
     * the model begins a new query and one asynchronous request starts for each
     * strategy.
     */
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

    /**
     * Starts an asynchronous request for one language model.
     *
     * Preconditions: strategy is not null and question is not null
     * or blank.
     * Postcondition: The model is eventually updated with a new response.
     *
     * @param strategy the strategy used to generate the response
     * @param question the user's question
     */
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

    /**
     * Calculates the elapsed time since a request started.
     *
     * Precondition: start represents a time in milliseconds.
     * Postcondition: Returns the elapsed time in milliseconds.
     *
     * @param start the request start time
     * @return the elapsed time in milliseconds
     */
    private static long elapsed(long start) {
        return System.currentTimeMillis() - start;
    }
}