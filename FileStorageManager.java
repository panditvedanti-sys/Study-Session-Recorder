package com.example.StudySessionRecorder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileStorageManager {
    private static final String FILE_NAME = "study_history.csv";

    public void saveSession(StudySession session) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            String line = session.getTime() + "," + session.getIntention() + "," + session.getAccomplishment() + System.lineSeparator();
            writer.write(line);
            System.out.println("Success! The session has securely been saved to your daily logs!");
        } catch (IOException e) {
            System.out.println("Sorry, the session ran into an error and could not be saved.");
        }
    }

    public List<StudySession> loadAllSessions() {
        List<StudySession> sessions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",", 3);
                if (tokens.length >= 3) {
                    LocalDateTime timestamp = LocalDateTime.parse(tokens[0]);
                    String goal = tokens[1];
                    String reflection = tokens[2];
                    sessions.add(new StudySession(goal, reflection, timestamp));
                }
            }
        } catch (IOException e) {
            System.out.println("No files found in history. Starting afresh!");
        }
        return sessions;
    }

    public int calculateCurrentStreak() {
        List<StudySession> sessions = loadAllSessions();
        Set<LocalDate> dates = new HashSet<>();
        for (StudySession session : sessions) {
            dates.add(session.getTime().toLocalDate());
        }

        int count = 0;
        LocalDate current = LocalDate.now();
        if (!dates.contains(current)) {
            current = current.minusDays(1);
        }

        while (dates.contains(current)) {
            count++;
            current = current.minusDays(1);
        }
        return count;
    }

    public void loadHistory() {
        System.out.println("\nYour Study Logs History:");
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            int totalSessions = 0;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",", 3);
                if (tokens.length >= 3) {
                    System.out.printf("[%s] INTENTION: %s | ACCOMPLISHED: %s%n", tokens[0], tokens[1], tokens[2]);
                    totalSessions++;
                }
            }

            if (totalSessions == 0) {
                System.out.println("You currently have no sessions recorded.");
            } else {
                System.out.println("\nTotal completed habits recorded: " + totalSessions);
            }
        } catch (IOException e) {
            System.err.print("An error occurred while reading the file: " + e.getMessage());
        }
    }
}
