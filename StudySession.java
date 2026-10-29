package com.example.StudySessionRecorder;

import java.time.LocalDateTime;

public class StudySession {
    private final String intention;
    private final String accomplishment;
    private final LocalDateTime time;

    public StudySession(String intention, String accomplishment) {
        this(intention, accomplishment, LocalDateTime.now());
    }

    public StudySession(String intention, String accomplishment, LocalDateTime time) {
        this.intention = intention;
        this.accomplishment = accomplishment;
        this.time = time;
  
    }

    public String getIntention() {
        return intention;
    }

    public String getAccomplishment() {
        return accomplishment;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
