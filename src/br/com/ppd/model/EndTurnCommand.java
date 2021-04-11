package br.com.ppd.model;

import java.io.Serializable;

public class EndTurnCommand implements Command, Serializable {

    private static final long serialVersionUID = 1L;
    private int currentTurn;

    public EndTurnCommand(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }


}
