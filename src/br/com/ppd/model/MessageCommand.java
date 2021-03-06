package br.com.ppd.model;

import java.io.Serializable;

public class MessageCommand implements Command, Serializable {

    private static final long serialVersionUID = 1L;
    private String text;
    private Player sender;

    public MessageCommand(String text, Player sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }


}
