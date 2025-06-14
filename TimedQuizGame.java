import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class TimedQuizGame extends JFrame implements ActionListener {
    class Question {
        String category;
        String text;
        String[] options;
        int correctIndex;

        public Question(String category, String text, String[] options, int correctIndex) {
            this.category = category;
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    private ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<String> reviewLog = new ArrayList<>();

    private int current = 0;
    private int score = 0;
    private int timeLeft = 300; // 5 minutes in seconds
    private Timer timer;

    private JLabel categoryLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel timerLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
    private JButton[] optionButtons = new JButton[4];

    public TimedQuizGame() {
        setTitle("🌟 Timed Quiz Game");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color bgColor = new Color(232, 245, 233);
        Color primaryColor = new Color(76, 175, 80);
        Color buttonColor = new Color(139, 195, 74);

        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.setBackground(bgColor);

        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        categoryLabel.setForeground(primaryColor);
        topPanel.add(categoryLabel);

        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        questionLabel.setForeground(primaryColor);
        topPanel.add(questionLabel);

        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        topPanel.add(timerLabel);

        add(topPanel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setBackground(bgColor);

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].setBackground(buttonColor);
            optionButtons[i].setForeground(Color.WHITE);
            optionButtons[i].setFont(new Font("Segoe UI", Font.BOLD, 16));
            optionButtons[i].addActionListener(this);
            optionsPanel.add(optionButtons[i]);
        }

        add(optionsPanel, BorderLayout.CENTER);

        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scoreLabel.setForeground(primaryColor);
        add(scoreLabel, BorderLayout.SOUTH);

        loadQuestions();
        loadQuestion();
        startTimer();

        setVisible(true);
    }

    private void loadQuestions() {
        questions.add(new Question("Geography", "What is the capital of France?",
                new String[]{"Paris", "London", "Berlin", "Madrid"}, 0));
        questions.add(new Question("Technology", "Which language is used for Android development?",
                new String[]{"Python", "Java", "Swift", "C#"}, 1));
        questions.add(new Question("Java", "What does JVM stand for?",
                new String[]{"Java Virtual Machine", "Java Very Much", "Just Virtual Machine", "Java Verified Module"}, 0));
        questions.add(new Question("Art", "Who painted the Mona Lisa?",
                new String[]{"Leonardo da Vinci", "Pablo Picasso", "Vincent Van Gogh", "Claude Monet"}, 0));
        // You can add more questions!
    }

    private void loadQuestion() {
        if (current < questions.size()) {
            Question q = questions.get(current);
            categoryLabel.setText("Category: " + q.category);
            questionLabel.setText((current + 1) + ". " + q.text);
            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText(q.options[i]);
                optionButtons[i].setEnabled(true);
            }
        } else {
            finishQuiz();
        }
    }

    private void startTimer() {
        timerLabel.setText("Time left: " + formatTime(timeLeft));
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time left: " + formatTime(timeLeft));
            if (timeLeft <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "⏰ Time's up!", "Time Over", JOptionPane.WARNING_MESSAGE);
                finishQuiz();
            }
        });
        timer.start();
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 4; i++) {
            if (e.getSource() == optionButtons[i]) {
                handleAnswer(i);
                break;
            }
        }
    }

    private void handleAnswer(int selectedIndex) {
        Question q = questions.get(current);
        String result;
        if (selectedIndex == q.correctIndex) {
            score++;
            result = "✅ Correct!";
        } else {
            result = "❌ Wrong! Correct: " + q.options[q.correctIndex];
        }

        reviewLog.add("Q" + (current + 1) + ": " + q.text + " | Your answer: "
                + q.options[selectedIndex] +
                " | Correct: " + q.options[q.correctIndex]);

        JOptionPane.showMessageDialog(this, result, "Result", JOptionPane.INFORMATION_MESSAGE);
        scoreLabel.setText("Score: " + score);
        current++;
        loadQuestion();
    }

    private void finishQuiz() {
        if (timer != null) {
            timer.stop();
        }

        StringBuilder review = new StringBuilder();
        review.append("🎉 Final Score: ").append(score).append("/").append(questions.size()).append("\n\n");
        review.append("----- Answer Review -----\n");
        for (String r : reviewLog) {
            review.append(r).append("\n");
        }

        JTextArea reviewArea = new JTextArea(review.toString());
        reviewArea.setEditable(false);
        reviewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reviewArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Quiz Review", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TimedQuizGame());
    }
}
