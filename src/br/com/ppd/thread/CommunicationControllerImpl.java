package br.com.ppd.thread;

import br.com.ppd.controller.CommunicationController;
import br.com.ppd.controller.CountTurnHelper;
import br.com.ppd.model.Board;
import br.com.ppd.model.Player;
import br.com.ppd.model.Square;
import br.com.ppd.utilitary.BufferUtilitary;
import br.com.ppd.view.GameView;
import javafx.application.Platform;

import java.rmi.RemoteException;

public class CommunicationControllerImpl implements CommunicationController {
    private GameView view;
    private Board board;
    private BufferUtilitary chatTextAreaBinBiding;
    private Runnable resetFunction;
    private Runnable clientInitFunction;

    public CommunicationControllerImpl(Board board, BufferUtilitary chatTextAreaBinBiding, GameView view, Runnable clientInitFunction) throws RemoteException {
        this.board = board;
        this.chatTextAreaBinBiding = chatTextAreaBinBiding;
        this.view = view;
        this.clientInitFunction = clientInitFunction;
    }

    public void setResetFunction(Runnable resetFunction) {
        this.resetFunction = resetFunction;
    }


    @Override
    public void sendMessage(Player player, String message) {
        this.chatTextAreaBinBiding.append(player.getName() + ": "  + message);
    }


    @Override
    public void move(int initX, int initY, int destinationX, int destinationY, Player player) {
        Square fromCell = this.board.getBoardMatrix()[initX][initY];
        fromCell.reset();

        Square toCell = this.board.getBoardMatrix()[destinationX][destinationY];
        toCell.setOwner(player);
    }


    @Override
    public void startGame() {
        this.view.removeWaitingPane();
        clientInitFunction.run();
    }


    @Override
    public void victory(Player player) {
        this.chatTextAreaBinBiding.append("YOU LOST!");
        this.view.showDefeatPane();
        Platform.runLater(() -> this.view.showResetButton());
    }


    @Override
    public void restartGame() {
        Platform.runLater(() -> this.resetFunction.run());
    }


    @Override
    public void endTurn(int turn) {
        Platform.runLater(() -> {
            CountTurnHelper.turn++;
            this.view.removeClickPreventionPane();
            this.view.showPlayerTurn();
        });
    }


    @Override
    public void giveup() {
        Platform.runLater(() -> {
            this.view.showVictoryPane();
            this.view.showResetButton();
        });
    }
}
