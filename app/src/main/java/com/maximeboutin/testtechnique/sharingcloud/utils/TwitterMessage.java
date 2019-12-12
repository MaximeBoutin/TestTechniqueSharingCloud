package com.maximeboutin.testtechnique.sharingcloud.utils;

public class TwitterMessage {

    private String pseudo;
    private String text;
    private String ppUrl;

    public String getPseudo() {

        return pseudo;
    }

    public String getText() {

        return text;
    }

    public String getPpUrl() {
        return ppUrl;
    }

    public TwitterMessage(String pseudo, String text, String ppUrl) {
        this.pseudo = pseudo;
        this.text = text;
        this.ppUrl = ppUrl;
    }

}
