package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Model in MVC: holds the current question and each model's answer.
 * Knows nothing about Swing or strategies, only model names.
 */
public class QuerySession {
    private String question = "";
    private final Map<String, LLMResponse> responses = new HashMap<>();
    private final List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    /**
     * Starts a new question and marks every model as running.
     * @throws IllegalArgumentException if the question is blank
     */
    public void beginQuery(String question, List<String> modelNames) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question can't be blank");
        }
        this.question = question;
        responses.clear();
        for (String name : modelNames) {
            responses.put(name, LLMResponse.running());
        }
        notifyObservers();
    }

    /**
     * Saves one model's answer. Call this on the Swing thread
     * (wrap it in SwingUtilities.invokeLater) since observers update the UI.
     */
    public void setResponse(String modelName, LLMResponse response) {
        responses.put(modelName, response);
        notifyObservers();
    }

    /** Returns a "Waiting" response if this model has no answer yet. */
    public LLMResponse getResponse(String modelName) {
        return responses.getOrDefault(modelName, LLMResponse.waiting());
    }

    public String getQuestion() {
        return question;
    }

    private void notifyObservers() {
        for (ModelObserver observer : observers) {
            observer.modelChanged();
        }
    }
}
