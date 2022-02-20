package com.dexonline.classes;

public class WordOfDay {
    private String year, word, reason, image;

    public WordOfDay() {
        this.year = "";
        this.word = "";
        this.reason = "";
        this.image = "";
    }

    public WordOfDay(String year, String word, String reason, String image) {
        this.year = year;
        this.word = word;
        this.reason = reason;
        this.image = image;
    }

    public String getYear() {
        return year;
    }

    public String getWord() {
        return word;
    }

    public String getReason() {
        return reason;
    }

    public String getImage() {
        return image;
    }
}
