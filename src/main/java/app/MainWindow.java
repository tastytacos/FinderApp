package app;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MainWindow extends JFrame implements ActionListener, Runnable {
    private JButton startTimeButton, stopTimeButton;
    private JLabel timeLabel = new JLabel();
    private Font defaultFont = new Font("Default", Font.BOLD + Font.ITALIC, 18);
    private Clock clock;
    private JPanel toolbar;
    private JPanel mainPanel;
    private File file = null;
    private JLabel fileNameLabel;
    private JTextArea keyWordArea = new JTextArea(3, 20);
    private JTextArea resultTextArea = new JTextArea(10, 20);
    private String keyWord = "";

    public MainWindow() {
        initValues();
    }

    public MainWindow(File file, String keyWord) {
        this.file = file;
        this.keyWord = keyWord;
        initValues();
        findWords();
    }

    private void findWords() {
        System.out.println("Finding words");
    }

    private void initValues() {
        setSize(800, 800);

        toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        startTimeButton = new JButton("Start time");
        startTimeButton.setActionCommand("START_TIME");
        startTimeButton.setFont(defaultFont);
        startTimeButton.addActionListener(this);
        toolbar.add(startTimeButton);

        stopTimeButton = new JButton("Stop Time");
        stopTimeButton.setActionCommand("STOP_TIME");
        stopTimeButton.setFont(defaultFont);
        stopTimeButton.addActionListener(this);
        toolbar.add(stopTimeButton);

        timeLabel.setText("Some Text");
        timeLabel.setFont(defaultFont);
        toolbar.add(timeLabel);

        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel subMainPanel = new JPanel();
        subMainPanel.setLayout(new BoxLayout(subMainPanel, BoxLayout.Y_AXIS));

        JButton chooseFileButton = new JButton("Choose the file");
        chooseFileButton.setFont(defaultFont);
        chooseFileButton.setActionCommand("CHOOSE");
        chooseFileButton.addActionListener(this);
        subMainPanel.add(chooseFileButton);
        String filename;
        try {
            filename = file.getName();
        } catch (NullPointerException e) {
            filename = "The name of the file after loading will be shown here";
        }
        fileNameLabel = new JLabel(filename);
        fileNameLabel.setFont(defaultFont);
        subMainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        subMainPanel.add(fileNameLabel);

        JLabel tipLabel = new JLabel("Enter the substring, which you want to find in text file");
        tipLabel.setFont(defaultFont);
        subMainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        subMainPanel.add(tipLabel);

        keyWordArea.setLineWrap(true);
        keyWordArea.setFont(defaultFont);
        JScrollPane textScrollPane = new JScrollPane(keyWordArea);
        keyWordArea.setText(keyWord);
        subMainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        subMainPanel.add(textScrollPane);

        JButton findButton = new JButton("Find");
        findButton.setFont(defaultFont);
        findButton.setActionCommand("FIND");
        findButton.addActionListener(this);
        subMainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        subMainPanel.add(findButton);

        JButton findNewTabButton = new JButton("Find in new Tab");
        findNewTabButton.setFont(defaultFont);
        findNewTabButton.setActionCommand("NEW_TAB");
        findNewTabButton.addActionListener(this);
        subMainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        subMainPanel.add(findNewTabButton);

        resultTextArea.setLineWrap(true);
        resultTextArea.setFont(defaultFont);
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        subMainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        subMainPanel.add(resultScrollPane);


        mainPanel.add(subMainPanel);
        add(toolbar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        clock = new Clock(this);
        new Thread(clock).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        switch (e.getActionCommand()) {
            case "START_TIME":
                if (clock == null) {
                    clock = new Clock(this);
                    new Thread(clock).start();
                }
                break;
            case "STOP_TIME":
                if (clock != null) {
                    clock.stop();
                    clock = null;
                }
                break;
            case "CHOOSE":
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showDialog(this, "Choose the file");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    try {
                        StringUtils.getFileExtension(file);
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(this, "Wrong format of file");
                        file = null;
                        e1.printStackTrace();
                    }
                    fileNameLabel.setText(file.getName());
                }
                break;
            case "FIND":
                if (file != null && !keyWordArea.getText().equals("")) {
                    System.out.println("Do smth");
                    findWords();
                    LineSearcher lineSearcher = new LineSearcher(file, keyWordArea.getText());
                    try {
                        CompletableFuture<List<String>> completableFuture = CompletableFuture.supplyAsync(lineSearcher);
                        if (!completableFuture.isDone()){
                            resultTextArea.setText("Your query is handling...");
                        }
                        completableFuture.thenAccept(this::handle);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "You didn't set file or key word");
                }
                break;
            case "NEW_TAB":
                if (file != null && !keyWordArea.getText().equals("")) {
                    System.out.println("Do smth in new tab");
                    findWords();
                    new Thread(new MainWindow(file, keyWordArea.getText())).start();
                } else {
                    JOptionPane.showMessageDialog(this, "You didn't set file or key word");
                }
                break;
        }

    }

    private void handle(List<String> result) {
        if (result.size() != 0) {
            String resultString = "";
            for (String str: result) {
                resultString += str;
            }
            resultTextArea.setText(resultString);
        }
        else {
            resultTextArea.setText("No data found");
        }
    }

    public void setTime(String time) {
        timeLabel.setText(time);
    }

    @Override
    public void run() {
        System.out.println("App in " + Thread.currentThread().getName() + " is showing window");
        SwingUtilities.invokeLater(() -> {
            System.out.println("App in " + Thread.currentThread().getName() + " is showing window");
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setVisible(true);
        });
    }
}


           
         