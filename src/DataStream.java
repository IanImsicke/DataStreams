import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;

public class DataStream extends JFrame
{
    private File inputFile;
    private String searchTerm;
    private final String DELIMS = "\\W+";
    private JOptionPane optionPane;
    private JPanel mainPanel;
    private JPanel textAreaPanel;
    private JPanel controlsPanel;
    private JButton inputFileButton;
    private JButton runButton;
    private JButton quitButton;
    private JTextField chooseField;
    private JScrollPane inputFileScroller;
    private JScrollPane outputFileScroller;
    private JTextArea inputFileField;
    private JTextArea outputFileField;
    public static void main(String[] args)
    {
        DataStream ds = new DataStream();
    }
    public DataStream()
    {
        optionPane = new JOptionPane();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2,1));

        controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(1,4));
        chooseField = new JTextField(20);
        chooseField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Choose a filter word", TitledBorder.LEFT, TitledBorder.TOP));
        inputFileButton = new JButton("Input File");
        inputFileButton.addActionListener(e ->
        {
            inputFile = ChooseFile();
        });
        runButton = new JButton("Run");
        runButton.addActionListener(e ->
        {

            if (inputFile != null && chooseField.getText() != null)
            {
                searchTerm = chooseField.getText().trim().toLowerCase();
                SearchFile(inputFile, searchTerm);
            }
            else
            {
                optionPane.showInternalMessageDialog(null, "Missing selection",
                        "Missing selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        quitButton = new JButton("Quit");
        quitButton.addActionListener(e ->
        {
            System.exit(0);
        });
        controlsPanel.add(chooseField);
        controlsPanel.add(inputFileButton);
        controlsPanel.add(runButton);
        controlsPanel.add(quitButton);

        textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new GridLayout(1,2));
        inputFileField = new JTextArea(30, 20);
        outputFileField = new JTextArea(30, 20);
        inputFileScroller = new JScrollPane(inputFileField);
        inputFileScroller.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Original File", TitledBorder.LEFT, TitledBorder.TOP));
        outputFileScroller = new JScrollPane(outputFileField);
        outputFileScroller.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Filtered File", TitledBorder.LEFT, TitledBorder.TOP));
        textAreaPanel.add(inputFileScroller);
        textAreaPanel.add(outputFileScroller);

        mainPanel.add(controlsPanel);
        mainPanel.add(textAreaPanel);
        BuildWindow();
    }
    public void BuildWindow()
    {
        add(mainPanel);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize((screenWidth / 4) * 3, screenHeight);
        setLocation(screenWidth / 8, 0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    private File ChooseFile()
    {
        File chosenFile = null;
        JFileChooser chooser = new JFileChooser();
        try
        {
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);
            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                chosenFile = chooser.getSelectedFile();
                Path file = chosenFile.toPath();
                InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                optionPane.showInternalMessageDialog(null, "File selected: " + chosenFile.toString(),
                        "File info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (FileNotFoundException e)
        {
            optionPane.showInternalMessageDialog(null, "File not found!",
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return chosenFile;
    }
    private void SearchFile(File chosenFile, String chosenString)
    {
        inputFileField.setText("");
        outputFileField.setText("");
        try (Stream<String> lines = Files.lines(chosenFile.toPath()))
        {
            lines.forEachOrdered(l ->
            {
                String lineString = l.toLowerCase();
                inputFileField.append(l + "\n");
                String[] words = lineString.split("\\W");
                Set<String> wordsSet = new HashSet<>(Arrays.asList(words));
                if (wordsSet.contains(chosenString))
                {
                    outputFileField.append(l + "\n\n");
                }
            });

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}