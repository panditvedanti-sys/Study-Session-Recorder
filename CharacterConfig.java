package com.example.StudySessionRecorder;

public class CharacterConfig {
    private final String name;
    private String behavior;

    public CharacterConfig(String name) {
        this.name = name;
        this.behavior = "IDLE";
    }

    public void changeState(String newState) {
        switch (newState) {
            case "IDLE":
            case "STUDYING":
            case "BREAK":
                this.behavior = newState;
                break;
            default:
                System.out.println("Invalid state ignored: " + newState);
                break;
        }
    }

    public String getState() {
        return behavior;
    }

    public String getName() {
        return name;
    }
}
