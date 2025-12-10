package com.bis5.fitjourney.models;

public class DailyStep {
    private final String dayOfWeek;
    private final String date;
    private final int steps;

    public DailyStep(String dayOfWeek, String date, int steps) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.steps = steps;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }
}
