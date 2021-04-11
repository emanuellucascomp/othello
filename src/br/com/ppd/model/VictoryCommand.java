package br.com.ppd.model;

import java.io.Serializable;

public class VictoryCommand implements Command, Serializable {

    private static final long serialVersionUID = 1L;
    private Player player;

    public VictoryCommand(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


}