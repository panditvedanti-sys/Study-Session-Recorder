package com.example.StudySessionRecorder;

public class HabitEngine {
    private final CharacterConfig user;
    private int currentTimerSeconds;
    private boolean running;
    private String currentIntention;

    public HabitEngine(CharacterConfig user, int initialSeconds, boolean running) {
        this.user = user;
        this.currentTimerSeconds = initialSeconds;
        this.running = running;
        this.currentIntention = "";
    }

    public void startFocusBlock(String goal) {
        if (goal == null || goal.trim().isEmpty()) {
            return;
        }

        this.currentIntention = goal.trim();
        this.user.changeState("STUDYING");
        this.running = true;
        this.currentTimerSeconds = 1500;
    }

    public void tick() {
        if (!running) {
            return;
        }

        if (currentTimerSeconds > 0) {
            currentTimerSeconds--;
            if (currentTimerSeconds == 0) {
                user.changeState("BREAK");
                running = false;
            }
        }
    }

    public StudySession finishedSession(String reflection) {
        StudySession session = new StudySession(currentIntention, reflection);
        currentIntention = "";
        user.changeState("IDLE");
        running = false;
        return session;
    }

    public int getCurrentTimerSeconds() {
        return currentTimerSeconds;
    }

    public boolean isRunning() {
        return running;
    }
}
