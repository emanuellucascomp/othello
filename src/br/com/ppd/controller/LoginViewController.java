package br.com.ppd.controller;

import br.com.ppd.model.Address;
import br.com.ppd.model.Player;
import br.com.ppd.view.LoginView;
import javafx.stage.Stage;

public class LoginViewController {
    private Stage stage;
    private LoginView view;

    public LoginViewController(Stage stage) {
        this.view = new LoginView();
        this.stage = stage;
    }

    public void createScene() {
        this.view.createScene();
        this.view.createFirstScreen();
        this.view.getCreatePlayer1Button().setOnMouseClicked(e -> openCreateServerPane());
        this.view.getCreatePlayer2Button().setOnMouseClicked(e -> openConnectToServerPane());

        this.stage.setTitle("Othello");
        this.stage.setScene(this.view.getScene());
        this.stage.show();
    }

    private void openCreateServerPane() {
        this.view.createLoginScene();
        this.view.getIp().setText("127.0.0.1");
        this.view.getIp().setEditable(false);
        this.view.getStartGameButton().setOnMouseClicked(e -> startGame());
    }

    private void openConnectToServerPane() {
        this.view.createLoginScene();
        this.view.getStartGameButton().setOnMouseClicked(e -> connectToGame());
    }

    private void connectToGame() {
        Player oponent = new Player("black", 4, 1, "");
        Player player = new Player("white", 1, 2, this.view.getPlayerName().getText());
        createGameController(player, oponent);
    }

    private void startGame() {
        Player oponent = new Player("white", 1, 2, "");
        Player player = new Player("black", 4, 1, this.view.getPlayerName().getText());
        createGameController(player, oponent);
    }

    private void createGameController(Player player, Player oponent) {
        String ipAddress = this.view.getIp().getText();
        int port = Integer.parseInt(this.view.getPort().getText());
        GameViewController gameController = new GameViewController(this.stage, player, oponent);
        gameController.startGame();
    }
}
