package controller;

import java.util.List;
import javax.swing.*;
import model.*;
import view.*;
import strategy.LLMStrategy;

public class StudyAssistantController {
    private static final long REQUEST_TIMEOUT_SECONDS = 15;
    private final QuerySession model;
    private final List<LLMStrategy> strategies;
    private JButton askButton = new JButton("Ask Models");
    private JTextArea questionArea = new JTextArea();

    public StudyAssistantController(StudyAssistantView view, QuerySession model, List<LLMStrategy> strategies) {

    }

    private void updateModelNames(List<LLMStrategy> strategies) {

    }

    private void askModels() {

    }

    private void startRequest(LLMStrategy strategy, String question) {

    }

    private static long elapsed(long start) {

    }
}