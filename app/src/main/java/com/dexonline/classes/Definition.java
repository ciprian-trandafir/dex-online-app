package com.dexonline.classes;

public class Definition {
    private String id, htmlRep, userNick, sourceName, createDate, modDate;

    public Definition() {
        this.id = "";
        this.htmlRep = "";
        this.userNick = "";
        this.sourceName = "";
        this.createDate = "";
        this.modDate = "";
    }

    public Definition(String id, String htmlRep, String userNick, String sourceName, String createDate, String modDate) {
        this.id = id;
        this.htmlRep = htmlRep;
        this.userNick = userNick;
        this.sourceName = sourceName;
        this.createDate = createDate;
        this.modDate = modDate;
    }

    public String getId() {
        return id;
    }

    public String getHtmlRep() {
        return htmlRep;
    }

    public String getUserNick() {
        return userNick;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getModDate() {
        return modDate;
    }
}