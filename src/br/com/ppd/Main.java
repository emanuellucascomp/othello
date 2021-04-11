package br.com.ppd;

import br.com.ppd.controller.LoginViewController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        LoginViewController loginViewController = new LoginViewController(primaryStage);
        loginViewController.createScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
