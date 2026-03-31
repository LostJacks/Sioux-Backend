package org.spms.spmsbackend.domain;

public enum OrderSize {
    VERY_SMALL("Very Small", 5),
    SMALL("Small", 15),
    MEDIUM("Medium", 25),
    LARGE("Large", 50),
    VERY_LARGE("Very Large", 75);

    private final String label;
    private final int percentage;

    OrderSize(String label, int percentage) {
        this.label = label;
        this.percentage = percentage;
    }

    public String getLabel() {
        return label;
    }

    public int getPercentage() {
        return percentage;
    }
}

