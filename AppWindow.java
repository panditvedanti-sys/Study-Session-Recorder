package com.example.StudySessionRecorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class AppWindow {
    private HabitEngine engine;
    private FileStorageManager storage;

    private JFrame window;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private JTextField goalField;
    private JTextArea historyArea;
    private JButton actionButton;
    
    private Timer guiTimer;
    private int remainingSeconds;

    public AppWindow() {
        CharacterConfig defaultConfig = new CharacterConfig("Study Buddy");
        this.engine = new HabitEngine(defaultConfig, 25*60, false);
        this.storage = new FileStorageManager();
        
        setupWindow();
        refreshHistoryLog();
    }

    private void setupWindow() {
        window = new JFrame("🌱 Companion Study Space");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(450, 550);
        window.setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        statusLabel = new JLabel("Status: IDLE", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.GRAY);
        
        goalField = new JTextField();
        goalField.setToolTipText("What is your focus target today?");
        
        actionButton = new JButton("Start Focus Block");
        
        topPanel.add(statusLabel);
        topPanel.add(goalField);
        topPanel.add(actionButton);

        timerLabel = new JLabel("25:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 64));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(new JLabel("📜 Past Study Ledger History:"), BorderLayout.NORTH);
        
        historyArea = new JTextArea(8, 30);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        window.add(topPanel, BorderLayout.NORTH);
        window.add(timerLabel, BorderLayout.CENTER);
        window.add(bottomPanel, BorderLayout.SOUTH);

        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonClick();
            }
        });

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private boolean sessionIsActive = false; 

    private void handleButtonClick() {
        if (!sessionIsActive) {
            String goal = goalField.getText().trim();
            if (goal.isEmpty()) {
                JOptionPane.showMessageDialog(window, "Anchor yourself to a goal to increase productivity!");
                return;
            }

            engine.startFocusBlock(goal); 
            sessionIsActive = true; 
            statusLabel.setText("Status: STUDYING");
            statusLabel.setForeground(Color.ORANGE);
            actionButton.setText("Session in Progress...");
            actionButton.setEnabled(false);
            goalField.setEditable(false);

            remainingSeconds = 25 * 60; 
            startCountdownLoop();
        } else {
            String reflection = JOptionPane.showInputDialog(window, "What were you able to accomplish? Describe it briefly:");
            if (reflection == null || reflection.trim().isEmpty()) {
                JOptionPane.showMessageDialog(window, "Please document what you accomplished to save the ledger entry!");
                return;
            }

            StudySession session = new StudySession(goalField.getText(), reflection, LocalDateTime.now());
            storage.saveSession(session);

            sessionIsActive = false; 
            statusLabel.setText("Status: IDLE");
            statusLabel.setForeground(Color.GRAY);
            actionButton.setText("Start Focus Block");
            actionButton.setEnabled(true);
            goalField.setEditable(true);
            goalField.setText("");
            timerLabel.setText("25:00");
            
            refreshHistoryLog();
        }
    }


    private void startCountdownLoop() {
        guiTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingSeconds--;
                
                int mins = remainingSeconds / 60;
                int secs = remainingSeconds % 60;
                timerLabel.setText(String.format("%02d:%02d", mins, secs));

                if (remainingSeconds > 0 && (25 * 60 - remainingSeconds) % (10 * 60) == 0) {
                    JOptionPane.showMessageDialog(window, "Micro-Break time! Stand up, stretch, and relax!");
                }

                if (remainingSeconds <= 0) {
                    guiTimer.stop();
                    statusLabel.setText("Status: BREAK");
                    statusLabel.setForeground(Color.GREEN);
                    actionButton.setText("Complete Session Logs");
                    actionButton.setEnabled(true);
                    Toolkit.getDefaultToolkit().beep(); // Alert beep audio notification
                    JOptionPane.showMessageDialog(window, "Focus block finished! Click the main button to document your reflections.");
                }
            }
        });
        guiTimer.start();
    }

        private void refreshHistoryLog() {
        java.util.List<StudySession> logs = storage.loadAllSessions(); 
        
        if (logs.isEmpty()) {
            historyArea.setText("No history recorded yet. Complete a session to populate logs!");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = logs.size() - 1; i >= 0; i--) {
            StudySession session = logs.get(i);            
            sb.append(String.format("⏰ %s\n🎯 Target: %s\n📝 Accomplished: %s\n", 
                session.getTime().toString().replace("T", " ").substring(0, 16),
                session.getIntention(),
                session.getAccomplishment()
            ));
            sb.append("--------------------------------------------------\n");
        }
        historyArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppWindow();
            }
        });
    }
}