package view;
import java.awt.*;
import javax.swing.*;

public class StudyAssistantView {
    private QuerySession model;
    private JFrame frame;

    public StudyAssistantView(QuerySession model){
        this.model = model;
    }

    public void buildInterface(JButton askButton, JTextArea questionArea){
        createFrame();
        buildInputPanel(askButton, questionArea);
        buildResponses();

        frame.setVisible(true);
    }

    private void buildInputPanel(JButton askButton, JTextArea questionArea){
        JPanel inputPanel = new JPanel(new BorderLayout());
        
        inputPanel.add(new JScrollPane(questionArea),BorderLayout.CENTER);
        inputPanel.add(askButton,BorderLayout.EAST);

        frame.add(inputPanel,BorderLayout.NORTH);
    }

    private void createFrame(){
        frame = new JFrame("Multi-Model AI Study Assistant");
        frame.setLayout(new BorderLayout());
        frame.setSize(1000,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void buildResponses(){
        JPanel responsePanels = new JPanel(new GridLayout(1,3));
        
        LLMResponseView llamaView = new LLMResponseView("Llama",model);
        LLMResponseView gemmaView = new LLMResponseView("Gemma",model);
        LLMResponseView qwenView = new LLMResponseView("Qwen",model);
        responsePanels.add(llamaView);
        responsePanels.add(gemmaView);
        responsePanels.add(qwenView);

        frame.add(responsePanels,BorderLayout.CENTER);
    }
}
