package com.dexonline.classes;

public class WordOfDay {
    private String year, day, month, word, reason, image;

    public WordOfDay() {
        this.year = "";
        this.day = "";
        this.month = "";
        this.word = "";
        this.reason = "";
        this.image = "";
    }

    public WordOfDay(String year, String day, String month, String word, String reason, String image) {
        this.year = year;
        this.day = day;
        this.month = month;
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

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }
}
