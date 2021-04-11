package br.com.ppd.controller;

import br.com.ppd.model.*;
import br.com.ppd.thread.ThreadUpdateView;
import br.com.ppd.utilitary.BufferUtilitary;
import br.com.ppd.utilitary.MatrizUtilitary;
import br.com.ppd.view.GameView;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameViewController {

    private GameView view;
    private Board board;
    private Player player;
    private Player oponent;
    private List<Square> possibleMoves;
    private Square movingPiece;
    private boolean canMove;
    private boolean hasMoved;
    private boolean hasJumped;
    private BufferUtilitary buffer;
    private CommandController communicationController;
    private ThreadUpdateView threadUpdateView;



    public GameViewController(Stage stage, Player player, Player oponent, Address address) {
        view = new GameView(stage);
        view.setPlayerColor(player.getColor());
        view.setOponentColor(oponent.getColor());
        stage.setOnCloseRequest(e -> closeGame());
        stage.setTitle("Othello - " + player.getId());
        board = new Board();
        board.createBoard();
        this.oponent = oponent;
        this.player = player;
        this.possibleMoves = new ArrayList<>();
        this.buffer = new BufferUtilitary();
        this.canMove = true;
        this.hasJumped = false;
        this.hasMoved = false;

        this.communicationController = new CommandController(address.getIpAddress(), address.getPort());
    }

    private void initUpdaterThread() {
        this.threadUpdateView = new ThreadUpdateView(this.board, this.buffer, this.communicationController.getReceivedCommands(), this.communicationController.getUpdateViewLock(), this.view);
        this.threadUpdateView.setResetFunction(() -> resetState());
        Thread updater = new Thread(this.threadUpdateView);
        updater.start();

    }

    private void selectPieceToMove(Square square) {
        if (!this.possibleMoves.isEmpty())
            this.clearHighlightedCells();
        if (canMove) {
            if (this.hasJumped && !square.equals(this.movingPiece))
                return;

            if (this.movingPiece != null) this.movingPiece.deselectCell();
            this.movingPiece = square;
            square.selectCell();
            this.highlightPossibleMoves(square);
        }

    }

    private void clearHighlightedCells() {
        for (Square square : this.possibleMoves) {
            if (square != null && square.isEmpty()) {
                square.reset();
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
            this.communicationController.addCommand(new VictoryCommand(this.player));
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
            this.communicationController.addCommand(new VictoryCommand(this.player));
            this.view.showVictoryPane();
            this.view.showResetButton();
        }
    }

    private void highlightNeighborMoves(Square square) {
        for (Square possibleMove : this.possibleMoves) {
            if (possibleMove.isEmpty()) {
                possibleMove.setMovable(true);
                possibleMove.getHex().getStyleClass().add("hex-highlight");
                possibleMove.getHex().setOnMouseClicked(e -> moveToNeighbor(square, possibleMove));
            }
        }
    }


    private void highlightJumpMoves(Square squareToJump, Square originSquare) {
        for (Square possibleMove : this.board.getAdjacentTo(squareToJump)) {
            if (possibleMove.isEmpty() && !possibleMove.isMovable() ) {
                possibleMove.getHex().getStyleClass().add("hex-highlight");
                possibleMove.getHex().setOnMouseClicked(e -> jumpToCell(originSquare, possibleMove));
                this.possibleMoves.add(possibleMove);
            }
        }
    }

    public void highlightPossibleMoves(Square square) {
        List<Square> neighborSquares = this.board.getAdjacentTo(square);
        if (!hasJumped) {
            this.possibleMoves.addAll(neighborSquares);
            highlightNeighborMoves(square);
        }

        for (Square neighborSquare : neighborSquares) {
            if (!neighborSquare.isEmpty()) {
                highlightJumpMoves(neighborSquare, square);
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
        this.initOponentsArea(oponent);
        this.view.createGameScene();
        initUpdaterThread();
        CountTurnHelper.turn++;
    }

    private void sendMessage() {
        String text = this.view.getMessageTextArea().getText();
        if (text != null && !text.isEmpty()) {
            this.buffer.append(text);
            this.view.getMessageTextArea().clear();
            this.communicationController.addCommand(new MessageCommand(text, this.player));
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
            this.communicationController.addCommand(new EndTurnCommand(CountTurnHelper.turn));
        }
    }

    private void giveUp() {
        this.clearHighlightedCells();
        this.communicationController.addCommand(new GiveUpCommand());
        this.view.showGivenUpPane();
        this.view.showResetButton();
    }

    private Pane createBoard(Board board) {
        Pane boardPane = new Pane();

        for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
            for (int j = 0; j < Board.BOARD_WIDTH; j++) {
                Square square = board.getBoardMatrix()[i][j];
                if (square != null) {
                    boardPane.getChildren().add(square.getHex());
                }
            }
        }
        return boardPane;
    }

    private void initOponentsArea(Player oponent) {
        for (int[] position : MatrizUtilitary.getArea(oponent.getArea())) {
            Square square = this.board.getBoardMatrix()[position[0]][position[1]];
            square.setOwner(oponent);
        }
    }

    private void initPlayerArea() {
        for (int[] position : MatrizUtilitary.getArea(player.getArea())) {
            Square square = this.board.getBoardMatrix()[position[0]][position[1]];
            square.setOwner(player);
            square.getHex().setOnMouseClicked(e -> selectPieceToMove(square));
        }
    }

    private void sendMoveCommand(Square from, Square to) {
        this.communicationController.addCommand(new MoveCommand(from, to, this.player));
    }

    public void createServer() {
        try {
            this.communicationController.createServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            this.communicationController.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeGame() {
        if (this.threadUpdateView != null)
            this.threadUpdateView.stop();
        this.communicationController.stopCommunication();
    }

    public void startGame() {
        createGameScene();
        if (this.player.getId() == 1) {
            this.view.showWaitingScene();
            this.view.showPlayerTurn();
            new Thread(() -> createServer()).start();
        }
        else {
            connect();
            this.view.showOponentTurn();
        }
    }

    private void restartGame() {
        this.communicationController.addCommand(new RestartCommand());
        resetState();
    }

    private void resetState() {
        resetBoard();
        resetStatesFlag();
        initPlayerArea();
        initOponentsArea(oponent);

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
                Square square = board.getBoardMatrix()[i][j];
                if (square != null) {
                    square.reset();
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
