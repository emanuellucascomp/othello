package br.com.ppd.thread;

import br.com.ppd.controller.CountTurnHelper;
import br.com.ppd.model.*;
import br.com.ppd.utilitary.BufferUtilitary;
import br.com.ppd.view.GameView;
import javafx.application.Platform;

import java.util.List;

public class ThreadUpdateView implements Runnable {

    private GameView gameView;
    private Board board;
    private BufferUtilitary chat;
    private List<Command> receivedCommands;
    private Object lock;
    private boolean isRunning;
    private Runnable resetRunnable;

    public ThreadUpdateView(Board board, BufferUtilitary chat, List<Command> receivedCommands, Object lock, GameView gameView) {
        this.board = board;
        this.chat = chat;
        this.receivedCommands = receivedCommands;
        this.lock = lock;
        this.isRunning = true;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        while (isRunning) {
            if (!receivedCommands.isEmpty()) {
                handleCommand(this.receivedCommands.remove(0));
            } else {
                blockThread();
            }
        }
    }

    private void handleCommand(Command command) {
        if (command instanceof StartGameCommand) {
            startGame();
        } else if (command instanceof MoveCommand) {
            moveOponentPiece((MoveCommand) command);
        } else if (command instanceof MessageCommand) {
            addMessageToChat((MessageCommand) command);
        } else if (command instanceof EndTurnCommand) {
            changeTurn((EndTurnCommand) command);
        } else if (command instanceof VictoryCommand) {
            testVictoryOfOponent((VictoryCommand) command);
        } else if (command instanceof GiveUpCommand) {
            showVictory();
        } else if(command instanceof RestartCommand) {
            restartGame();
        }

    }

    private void restartGame() {
        Platform.runLater(() -> this.resetRunnable.run());
    }

    private void showVictory() {
        Platform.runLater(() -> {
            this.gameView.showVictoryPane();
            this.gameView.showResetButton();
        });
    }

    private void startGame() {
        this.gameView.removeWaitingPane();
    }

    private void testVictoryOfOponent(VictoryCommand command) {
        if (this.board.winCondition(command.getPlayer())) {
            this.chat.append("YOU LOST!!!");
            this.gameView.showDefeatPane();
            Platform.runLater(() -> this.gameView.showResetButton());
        }
    }

    private void changeTurn(EndTurnCommand command) {
        Platform.runLater(() -> {
            CountTurnHelper.turn++;
            this.gameView.removeClickPreventionPane();
            this.gameView.showPlayerTurn();
        });
    }

    private void addMessageToChat(MessageCommand messageCommand) {
        this.chat.append(messageCommand.getSender().getName() + ": "  + messageCommand.getText());
    }

    private void moveOponentPiece(MoveCommand command) {
        Square fromSquare = this.board.getBoardMatrix()[command.getFromRowIndex()][command.getFromColumnIndex()];
        fromSquare.reset();

        Square toSquare = this.board.getBoardMatrix()[command.getToRowIndex()][command.getToColumnIndex()];
        toSquare.setOwner(command.getPlayer());
    }

    private void blockThread() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.isRunning = false;
    }

    public void setResetFunction(Runnable resetRunnable) {
        this.resetRunnable = resetRunnable;
    }

}