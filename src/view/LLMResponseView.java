package view;
import java.awt.*;
import javax.swing.*;
import model.LLMResponse;
import model.QuerySession;
import model.ModelObserver;

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
     * @param modelName the name of the LLM whose respose this panel will display.
     * @param model the shared query session model contianing the response data.s
     */
    public LLMResponseView(String modelName, QuerySession model){
        this.modelName = modelName;
        this.model = model;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(modelName);
        statusLabel = new JLabel("Waiting");
        responseArea = new JTextArea();
        responseArea.setEditable(false);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);

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
        SwingUtilities.invokeLater(() -> {
        LLMResponse response = model.getResponse(modelName);

        if(response == null){
            statusLabel.setText("Waiting");
            responseArea.setText("");
            return;
        }

        String status = response.getStatusString();
        String modelResponse = response.getText();

        statusLabel.setText(status);
        responseArea.setText(modelResponse);;
        });
    }
}
