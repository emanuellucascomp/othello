package br.com.ppd.controller;

import br.com.ppd.model.*;
import br.com.ppd.thread.CommunicationControllerImpl;
import br.com.ppd.thread.ThreadUpdateView;
import br.com.ppd.utilitary.BufferUtilitary;
import br.com.ppd.utilitary.MatrizUtilitary;
import br.com.ppd.view.GameView;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GameViewController {

    private GameView view;
    private Board board;
    private Player player;
    private Player opponent;
    private List<Square> possibleMoves;
    private Square movingPiece;
    private boolean canMove;
    private boolean hasMoved;
    private boolean hasJumped;
    private BufferUtilitary buffer;
    private CommunicationController communicator;
    private CommunicationControllerImpl communicationControllerImpl;
    private Registry registry;


    public GameViewController(Stage stage, Player player, Player opponent) {
        view = new GameView(stage);
        view.setPlayerColor(player.getColor());
        view.setOpponentColor(opponent.getColor());
        stage.setOnCloseRequest(e -> closeGame());
        stage.setTitle("Othello - " + player.getId());
        board = new Board();
        board.createBoard();
        this.opponent = opponent;
        this.player = player;
        this.possibleMoves = new ArrayList<Square>();
        this.buffer = new BufferUtilitary();
        this.canMove = true;
        this.hasJumped = false;
        this.hasMoved = false;

        firstCommunication();
        if (this.player.getId() == 2) {
            secondCommunication();
        }

    }

    private void firstCommunication() {
        try {
            this.communicationControllerImpl = new CommunicationControllerImpl(this.board, this.buffer, this.view, () -> { secondCommunication();});
            this.communicationControllerImpl.setResetFunction(() -> resetGame());
            CommunicationController stub = (CommunicationController) UnicastRemoteObject.exportObject(communicationControllerImpl, 0);
            if (this.player.getId() == 1)
                registry = LocateRegistry.createRegistry(3000);
            else
                registry = LocateRegistry.getRegistry(3000);
            registry.rebind("Communication" + player.getId(), stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void secondCommunication() {
        try {
            this.communicator = (CommunicationController) LocateRegistry.getRegistry(3000).lookup("Communication" + opponent.getId());
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void selectPieceToMove(Square hexagon) {
        if (!this.possibleMoves.isEmpty())
            this.clearHighlightedCells();
        if (canMove) {
            if (this.hasJumped && !hexagon.equals(this.movingPiece))
                return;

            if (this.movingPiece != null) this.movingPiece.deselectCell();
            this.movingPiece = hexagon;
            hexagon.selectCell();
            this.highlightPossibleMoves(hexagon);
        }

    }

    private void clearHighlightedCells() {
        for (Square hexagon : this.possibleMoves) {
            if (hexagon != null && hexagon.isEmpty()) {
                hexagon.reset();
            }
        }
        this.possibleMoves.clear();
    }

    private void moveToNeighbor(Square from, Square to) {
        this.movingPiece = to;
        this.clearHighlightedCells();
        to.setOwner(player);
        to.getHex().setOnMouseClicked(e -> selectPieceToMove(to));
        from.reset();
        this.canMove = false;
        this.hasMoved = true;
        this.sendMoveCommand(from, to);

        if (this.board.winCondition(player)) {
            try {
                this.communicator.victory(player);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            this.view.showVictoryPane();
            this.view.showResetButton();
        }
    }

    private void jumpToCell(Square from, Square to) {
        this.clearHighlightedCells();
        this.movingPiece = to;
        to.setOwner(player);
        to.getHex().setOnMouseClicked(e -> selectPieceToMove(to));
        from.reset();
        this.canMove = true;
        this.hasJumped = true;
        this.hasMoved = true;
        this.sendMoveCommand(from, to);

        if (this.board.winCondition(player)) {
            try {
                this.communicator.victory(player);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            this.view.showVictoryPane();
            this.view.showResetButton();
        }
    }

    private void highlightNeighborMoves(Square hexagon) {
        for (Square possibleMove : this.possibleMoves) {
            if (possibleMove.isEmpty()) {
                possibleMove.setMovable(true);
                possibleMove.getHex().getStyleClass().add("hex-highlight");
                possibleMove.getHex().setOnMouseClicked(e -> moveToNeighbor(hexagon, possibleMove));
            }
        }
    }


    private void highlightJumpMoves(Square hexagonToJump, Square originHexagon) {
        for (Square possibleMove : this.board.getAdjacentTo(hexagonToJump)) {
            if (possibleMove.isEmpty() && !possibleMove.isMovable() ) {
                possibleMove.getHex().getStyleClass().add("hex-highlight");
                possibleMove.getHex().setOnMouseClicked(e -> jumpToCell(originHexagon, possibleMove));
                this.possibleMoves.add(possibleMove);
            }
        }
    }

    public void highlightPossibleMoves(Square hexagon) {
        List<Square> neighborHexagons = this.board.getAdjacentTo(hexagon);
        if (!hasJumped) {
            this.possibleMoves.addAll(neighborHexagons);
            highlightNeighborMoves(hexagon);
        }

        for (Square neighborHexagon : neighborHexagons) {
            if (!neighborHexagon.isEmpty()) {
                highlightJumpMoves(neighborHexagon, hexagon);
            }
        }
    }

    private void initRestartButton( ) {
        Button restartButton = new Button("Reiniciar Jogo");
        restartButton.getStyleClass().addAll("custom-button", "full-button");
        restartButton.setOnMouseClicked(e -> restartGame());
        this.view.setRestartButton(restartButton);
    }

    public void createGameScene() {
        initRestartButton();
        this.view.createChatArea();
        this.view.getChatTextArea().textProperty().bind(buffer);
        this.buffer.addListener(listener -> {
            this.view.getChatTextArea().selectPositionCaret(this.view.getChatTextArea().getLength());
            this.view.getChatTextArea().deselect();
        });

        this.view.getEndTurnButton().setOnMouseClicked(e -> endTurn());
        this.view.getGiveUpButton().setOnMouseClicked(e -> giveUp());
        this.view.getSendMessageButton().setOnMouseClicked(e -> sendMessage());
        this.view.addBoard(createBoard(board));
        if (this.player.getId() == 2)
            this.view.addClickPreventionPane();
        this.initPlayerArea();
        this.initOpponentArea(opponent);
        this.view.createGameScene();
        CountTurnHelper.turn++;
    }

    private void sendMessage() {
        String text = this.view.getMessageTextArea().getText();
        if (text != null && !text.isEmpty()) {
            this.buffer.append(text);
            this.view.getMessageTextArea().clear();
            try {
                this.communicator.sendMessage(player, text);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void endTurn() {
        if (hasMoved) {
            this.clearHighlightedCells();
            this.movingPiece = null;
            this.canMove = true;
            this.hasJumped = false;
            this.hasMoved = false;
            this.view.addClickPreventionPane();
            this.view.showOponentTurn();
            CountTurnHelper.turn++;
            try {
                this.communicator.endTurn(CountTurnHelper.turn);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void giveUp() {
        this.clearHighlightedCells();
        try {
            this.communicator.giveup();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.view.showGivenUpPane();
        this.view.showResetButton();
    }

    private Pane createBoard(Board board) {
        Pane boardPane = new Pane();

        for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
            for (int j = 0; j < Board.BOARD_WIDTH; j++) {
                Square hexagon = board.getBoardMatrix()[i][j];
                if (hexagon != null) {
                    boardPane.getChildren().add(hexagon.getHex());
                }
            }
        }
        return boardPane;
    }

    private void initOpponentArea(Player oponent) {
        for (int[] position : MatrizUtilitary.getArea(oponent.getArea())) {
            Square hexagon = this.board.getBoardMatrix()[position[0]][position[1]];
            hexagon.setOwner(oponent);
        }
    }

    private void initPlayerArea() {
        for (int[] position : MatrizUtilitary.getArea(player.getArea())) {
            Square hexagon = this.board.getBoardMatrix()[position[0]][position[1]];
            hexagon.setOwner(player);
            hexagon.getHex().setOnMouseClicked(e -> selectPieceToMove(hexagon));
        }
    }

    private void sendMoveCommand(Square from, Square to) {
        try{
            this.communicator.move(from.getMatrixIndexRow(), from.getMatrixIndexColumn(), to.getMatrixIndexRow(), to.getMatrixIndexColumn(), player);
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    public void closeGame() {
        try {
            this.registry.unbind("Communication" + player.getId());
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void startGame() {
        createGameScene();
        if (this.player.getId() == 1) {
            this.view.showWaitingScene();
            this.view.showPlayerTurn();
        }
        else {
            try {
                this.communicator.startGame();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            this.view.showOponentTurn();
        }
    }

    private void restartGame() {
        try {
            this.communicator.restartGame();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        resetGame();
    }

    private void resetGame() {
        resetBoard();
        resetStatesFlag();
        initPlayerArea();
        initOpponentArea(opponent);

        this.view.showControlButtons();
        this.view.resetBoard();
        if (player.getId() == 2) {
            this.view.addClickPreventionPane();
            this.view.showOponentTurn();
        } else {
            this.view.showPlayerTurn();
        }
        CountTurnHelper.turn = 1;
    }

    private void resetBoard() {
        for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
            for (int j = 0; j < Board.BOARD_WIDTH; j++) {
                Square hexagon = board.getBoardMatrix()[i][j];
                if (hexagon != null) {
                    hexagon.reset();
                }
            }
        }
    }

    private void resetStatesFlag() {
        this.canMove = true;
        this.hasJumped = false;
        this.hasMoved = false;
    }
}
