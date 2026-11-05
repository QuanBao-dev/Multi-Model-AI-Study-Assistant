package view;
import java.awt.*;
import javax.swing.*;

/**
 * A view for displaying the response and status of a specific LLM.
 */
public class LLMResponseView extends JPanel implements ModelObserver{
    private String modelName;
    private QuerySession model;
    private JLabel statusLabel;
    private JTextArea responseArea;

    /**
     * Constructs a response view for a specific LLM model.
     * @param modelName
     * @param model
     */
    public LLMResponseView(String modelName, QuerySession model){
        this.modelName = modelName;
        this.model = model;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(modelName);
        statusLabel = new JLabel("Waiting");
        responseArea = new JTextArea();
        responseArea.setEditable(false);

        JPanel top = new JPanel(new BorderLayout());
        top.add(titleLabel,BorderLayout.NORTH);
        top.add(statusLabel,BorderLayout.SOUTH);

        add(top,BorderLayout.NORTH);
        add(new JScrollPane(responseArea),BorderLayout.CENTER);

        model.addObserver(this);

    }

    /**
     * Refreshes panel when model changes, updating the status and response text.
     */
    @Override
    public void modelChanged(){
        LLMResponse response = model.getResponse(modelName);

        String status = response.getStatusString();
        String modelResponse = response.getText();

        statusLabel.setText(status);
        responseArea.setText(modelResponse);

    }
}
