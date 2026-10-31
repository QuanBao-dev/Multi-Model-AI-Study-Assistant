package view;
import java.awt.*;
import javax.swing.*;

public class LLMResponseView extends JPanel implements ModelObserver{
    private String modelName;
    private QuerySession model;
    private JLabel statusLabel;
    private JTextArea responseArea;

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

    @Override
    public void modelChanged(){
        LLMResponse response = model.getResponse(modelName);

        String status = response.getStatusString();
        String modelResponse = response.getText();

        statusLabel.setText(status);
        responseArea.setText(modelResponse);

    }
}
