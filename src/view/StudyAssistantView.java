package view;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import model.QuerySession;

/**
 * The View in MVC: holds the GUI components and is responsible for displaying the model's data to the user.
 */
public class StudyAssistantView {
    private QuerySession model;
    private JFrame frame;

    /**
     * Constructor for the StudyAssistantView class.
     * @param model the shared query session model used by response panels.
     */
    public StudyAssistantView(QuerySession model){
        this.model = model;
    }

    /**
     * Builds the GUI interface
     * @param askButton the button to ask the LLMs a question
     * @param questionArea the text area where the user can input their question
     */
    public void buildInterface(JButton askButton, JTextArea questionArea){
        createFrame();
        buildInputPanel(askButton, questionArea);
        buildResponses();

        frame.setVisible(true);
    }

    /**
     * Builds the input panel containing the question area and ask button
     * @param askButton the button to ask the LLMs a question
     * @param questionArea the text area where the user can input their question
     */
    private void buildInputPanel(JButton askButton, JTextArea questionArea){
        JPanel inputPanel = new JPanel(new BorderLayout());
        
        inputPanel.add(new JScrollPane(questionArea),BorderLayout.CENTER);
        inputPanel.add(askButton,BorderLayout.EAST);

        frame.add(inputPanel,BorderLayout.NORTH);
    }

    /**
     * Builds the main frame of the application
     */
    private void createFrame(){
        frame = new JFrame("Multi-Model AI Study Assistant");
        frame.setLayout(new BorderLayout());
        frame.setSize(1000,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Builds the response panels for each LLM model and adds them to the main frame
     */
    private void buildResponses(){
        List<String> modelNames = model.getModelNames();

        JPanel responsePanels = new JPanel(new GridLayout(1,modelNames.size()));
        
        for(String name:modelNames){
            responsePanels.add(new LLMResponseView(name,model));
        }

        frame.add(responsePanels,BorderLayout.CENTER);
    }
}
