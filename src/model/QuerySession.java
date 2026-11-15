package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Model in MVC: holds the model names, the current question and each model's answer.
 * Knows nothing about Swing or strategies, only model names.
 */
public class QuerySession {
    private List<String> modelNames = new ArrayList<>();
    private String question = "";
    private final Map<String, LLMResponse> responses = new LinkedHashMap<>();
    private final List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public List<String> getModelNames() {
        return Collections.unmodifiableList(modelNames);
    }

    /** Sets which models get asked. Every model starts as "Waiting". */
    public void setModelNames(List<String> modelNames) {
        this.modelNames = new ArrayList<>(modelNames);
        responses.clear();
        for (String name : this.modelNames) {
            responses.put(name, LLMResponse.waiting(name));
        }
        notifyObservers();
    }

    /**
     * Starts a new question and marks every model as running.
     * @throws IllegalArgumentException if the question is blank
     */
    public void beginQuery(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question can't be blank");
        }
        this.question = question;
        responses.clear();
        for (String name : modelNames) {
            responses.put(name, LLMResponse.running(name));
        }
        notifyObservers();
    }

    /**
     * Saves one model's answer. Call this on the Swing thread
     * (wrap it in SwingUtilities.invokeLater) since observers update the UI.
     * Answers for a model that isn't in modelNames are ignored.
     */
    public void updateResponse(String modelName, LLMResponse response) {
        if (!modelNames.contains(modelName)) {
            return;
        }
        responses.put(modelName, response);
        notifyObservers();
    }

    /** Returns a "Waiting" response if this model has no answer yet. */
    public LLMResponse getResponse(String modelName) {
        LLMResponse response = responses.get(modelName);
        return response != null ? response : LLMResponse.waiting(modelName);
    }

    /** Every model's current answer, in the same order as modelNames. */
    public List<LLMResponse> getResponses() {
        List<LLMResponse> list = new ArrayList<>();
        for (String name : modelNames) {
            list.add(getResponse(name));
        }
        return list;
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
