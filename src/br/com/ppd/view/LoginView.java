package br.com.ppd.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {
    private Scene scene;
    private StackPane pane;

    private TextArea playerName;
    private TextArea ip;
    private TextArea port;

    private Button createPlayer1Button;
    private Button createPlayer2Button;
    private Button startGameButton;

    public LoginView() {
        this.pane = new StackPane();
    }

    public void createFirstScreen() {
        VBox firstScreen = new VBox();
        firstScreen.getStyleClass().add("box");

        Label label = new Label("Othello");
        label.getStyleClass().add("title-label");
        createPlayer1Button = new Button("Player 1");
        createPlayer1Button.getStyleClass().add("custom-button");
        createPlayer2Button = new Button("Player 2");
        createPlayer2Button.getStyleClass().add("custom-button");

        firstScreen.getChildren().addAll(label, createPlayer1Button, createPlayer2Button);
        this.pane.getChildren().add(firstScreen);
    }

    public void createLoginScene() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid");

        Label label = new Label("Othello");
        label.getStyleClass().add("title-label");

        grid.add(label, 0, 0, 2, 1);
        grid.add(createPlayerTextField(), 0, 1, 2, 1);
        grid.add(createIPTextField(), 0, 2);
        grid.add(createPortTextField(), 1, 2);
        grid.add(createStartButton(), 0, 3, 2, 1);

        this.pane.getChildren().add(grid);
    }

    public void createScene() {
        this.scene = new Scene(pane, 400, 300);
        this.scene.getStylesheets().add("/br/com/ppd/styles/styles.css");
    }

    public Scene getScene() {
        return this.scene;
    }

    private TextArea createPlayerTextField() {
        playerName = new TextArea();
        playerName.setPromptText("Nome");
        playerName.getStyleClass().add("input-text-area");
        return playerName;
    }

    private TextArea createIPTextField() {
        ip = new TextArea();
        ip.setPromptText("IP");
        ip.getStyleClass().add("input-text-area");
        return ip;
    }

    private TextArea createPortTextField() {
        port = new TextArea();
        port.setPromptText("Porta");
        port.getStyleClass().add("input-text-area");
        return port;
    }

    private Button createStartButton() {
        startGameButton = new Button("Jogar");
        startGameButton.getStyleClass().add("custom-button");
        return startGameButton;
    }

    public TextArea getPlayerName() {
        return playerName;
    }

    public TextArea getIp() {
        return ip;
    }

    public TextArea getPort() {
        return port;
    }

    public Button getCreatePlayer1Button() {
        return createPlayer1Button;
    }

    public Button getCreatePlayer2Button() {
        return createPlayer2Button;
    }

    public Button getStartGameButton() {
        return startGameButton;
    }
}
