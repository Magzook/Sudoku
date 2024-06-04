package client.main;

import client.controller.Controller;
import client.ui.Game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    private static Controller ct;
    public static Controller getCt() {return ct;}
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/game.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Game.WIDTH, Game.HEIGHT);
        ct = fxmlLoader.getController();
        stage.setTitle("Sudoku");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0));

        Game.mainMenu();
    }

    public static void main(String[] args) {
        launch();
    }
}