// Отрисовка и функционирование почти всех игровых панелей

package client.ui;

import client.load_save.GameInfoObject;
import client.main.Main;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.*;
import java.util.Random;

public class Game {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 900;

    public static void mainMenu() {
        Label labelGameName = new Label("Sudoku");
        labelGameName.fontProperty().setValue(new Font("Calibri Bold", 160));
        labelGameName.alignmentProperty().setValue(Pos.BOTTOM_CENTER);
        Label labelCreator = new Label("by Magzook");
        labelCreator.fontProperty().setValue(new Font("Calibri", 20));
        labelCreator.setPrefHeight(200);
        labelCreator.alignmentProperty().setValue(Pos.TOP_CENTER);
        VBox titlePane = new VBox(labelGameName, labelCreator);
        titlePane.setMaxWidth(500);
        titlePane.setAlignment(Pos.CENTER_RIGHT);

        Button btnContinue = new Button("Continue");
        Button btnStart = new Button("New game");
        Button btnExit = new Button("Quit");
        Font buttonFont = new Font("Comic Sans MS", 50);
        Button[] mainMenuButtons = new Button[]{btnContinue, btnStart, btnExit};
        String cssEnteredButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 10px; -fx-text-fill: #ffffff;" ;
        String cssExitedButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 10px;";
        for (var btn : mainMenuButtons) {
            btn.setFont(buttonFont);
            btn.setStyle(cssExitedButton);
            btn.setOnMouseEntered(e -> btn.setStyle(cssEnteredButton));
            btn.setOnMouseExited(e -> btn.setStyle(cssExitedButton));
        }
        btnContinue.setPrefWidth(400);
        btnStart.setPrefWidth(400);
        btnExit.setPrefWidth(250);
        btnContinue.setOnAction(e -> {
            ChooseGamePane.setPage(1);
            ChooseGamePane.show();
        });
        btnStart.setOnAction(e -> {
            try {
                chooseDifficulty();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnExit.setOnAction(e -> System.exit(0));
        VBox buttonPane = new VBox(50, mainMenuButtons);
        buttonPane.setAlignment(Pos.CENTER);

        VBox mainMenuPane = new VBox(titlePane, buttonPane);
        mainMenuPane.setMaxSize(WIDTH, HEIGHT);
        mainMenuPane.setPrefSize(WIDTH, HEIGHT);
        mainMenuPane.setAlignment(Pos.CENTER);

        if (Main.getCt().getPane().getChildren().isEmpty()) {
            Main.getCt().getPane().getChildren().add(mainMenuPane);
        }
        else PaneMover.moveTo(mainMenuPane, "left");
    }

    private static void chooseDifficulty() throws InterruptedException {
        Label labelDifficulty = new Label("Difficulty");
        labelDifficulty.fontProperty().setValue(new Font("Calibri", 100));
        labelDifficulty.setPrefHeight(300);
        Button btnEasy = new Button("Easy");
        Button btnMedium = new Button("Medium");
        Button btnHard = new Button("Hard");
        Font buttonFont = new Font("Comic Sans MS", 50);
        String cssEnteredButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 10px; -fx-text-fill: #ffffff;" ;
        String cssExitedButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 10px;";
        Button[] btnDifficulty = new Button[]{btnEasy, btnMedium, btnHard};
        for (var btn : btnDifficulty) {
            btn.setFont(buttonFont);
            btn.setStyle(cssExitedButton);
            btn.setOnMouseEntered(e -> btn.setStyle(cssEnteredButton));
            btn.setOnMouseExited((e -> btn.setStyle(cssExitedButton)));
            btn.setPrefWidth(300);
        }
        VBox chooseDifficultyPane = new VBox(50, labelDifficulty, btnEasy, btnMedium, btnHard);
        chooseDifficultyPane.setPrefSize(WIDTH, HEIGHT);
        chooseDifficultyPane.setMaxSize(WIDTH, HEIGHT);
        chooseDifficultyPane.setAlignment(Pos.CENTER);;

        PaneMover.moveTo(chooseDifficultyPane, "right");

        btnEasy.setOnAction(e -> {PlayPane.setDifficulty("Easy");
            try {
                PlayPane.show("");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnMedium.setOnAction(e -> {PlayPane.setDifficulty("Medium");
            try {
                PlayPane.show("");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnHard.setOnAction(e -> {PlayPane.setDifficulty("Hard");
            try {
                PlayPane.show("");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static void pause() {
        Label gameIsPaused = new Label("Game is paused");
        gameIsPaused.setFont(new Font("Calibri", 80));
        Button btnResume = new Button("Resume");
        Font buttonFont = new Font("Comic Sans MS", 50);
        btnResume.setFont(buttonFont);
        String cssEnteredButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 10px; -fx-text-fill: #ffffff;" ;
        String cssExitedButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 10px;";
        btnResume.setStyle(cssExitedButton);
        btnResume.setOnMouseEntered(e -> btnResume.setStyle(cssEnteredButton));
        btnResume.setOnMouseExited(e -> btnResume.setStyle(cssExitedButton));
        btnResume.setOnAction(e -> {
            PaneMover.moveTo(PlayPane.getPlayPane(), "left");
            MyTimer.start();
        });
        // Игра на паузе
        VBox pausePane = new VBox(100, gameIsPaused, btnResume);
        pausePane.setPrefSize(WIDTH, HEIGHT);
        pausePane.setMaxSize(WIDTH, HEIGHT);
        pausePane.setAlignment(Pos.CENTER);

        PaneMover.moveTo(pausePane, "right");

    }
    public static void defeat() {
        MyTimer.reset();
        Label youLost = new Label("Game Over");
        youLost.setFont(new Font("Calibri", 100));
        Label description = new Label("You've made too many mistakes");
        description.setFont(new Font("Calibri", 35));
        youLost.setPrefHeight(100);
        description.setPrefHeight(100);

        Button backToMainMenu = new Button("Back to Main Menu");
        Font buttonFont = new Font("Comic Sans MS", 50);
        backToMainMenu.setFont(buttonFont);
        String cssEnteredButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 10px; -fx-text-fill: #ffffff;" ;
        String cssExitedButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 10px;";
        backToMainMenu.setStyle(cssExitedButton);
        backToMainMenu.setOnMouseEntered(e -> backToMainMenu.setStyle(cssEnteredButton));
        backToMainMenu.setOnMouseExited(e -> backToMainMenu.setStyle(cssExitedButton));
        backToMainMenu.setOnAction(e -> {
            mainMenu();
        });

        VBox gameOverPane = new VBox(100, youLost, description, backToMainMenu);
        gameOverPane.setPrefSize(WIDTH, HEIGHT);
        gameOverPane.setMaxSize(WIDTH, HEIGHT);
        gameOverPane.setAlignment(Pos.CENTER);

        PaneMover.moveTo(gameOverPane, "right");
    }
    public static void victory() {
        Label youWon = new Label("You Won!");
        youWon.setFont(new Font("Calibri", 100));
        Label description = new Label("Your time is " + MyTimer.getTime());
        MyTimer.reset();
        description.setFont(new Font("Calibri", 35));
        youWon.setPrefHeight(100);
        description.setPrefHeight(100);

        Button backToMainMenu = new Button("Back to Main Menu");
        Font buttonFont = new Font("Comic Sans MS", 50);
        backToMainMenu.setFont(buttonFont);
        String cssEnteredButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 10px; -fx-text-fill: #ffffff;" ;
        String cssExitedButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 10px;";
        backToMainMenu.setStyle(cssExitedButton);
        backToMainMenu.setOnMouseEntered(e -> backToMainMenu.setStyle(cssEnteredButton));
        backToMainMenu.setOnMouseExited(e -> backToMainMenu.setStyle(cssExitedButton));
        backToMainMenu.setOnAction(e -> {
            mainMenu();
        });
        VBox victoryPane = new VBox(100, youWon, description, backToMainMenu);
        victoryPane.setPrefSize(WIDTH, HEIGHT);
        victoryPane.setMaxSize(WIDTH, HEIGHT);
        victoryPane.setAlignment(Pos.CENTER);

        PaneMover.moveTo(victoryPane, "right");
    }

    public static void save() {
        String time = MyTimer.getTime();
        MyTimer.reset();
        Label labelSave = new Label("Save your game");
        labelSave.setFont(new Font("Calibri", 80));
        Label labelSave2 = new Label("under the name:");
        labelSave2.setFont(new Font("Calibri", 40));
        TextField fieldGameName = new TextField(PlayPane.getDifficulty() + "_" + time.replace(":", "-") + "_" + new Random().nextInt(0, 1000000000));
        fieldGameName.setFont(new Font("Consolas", 25));
        fieldGameName.setMaxWidth(400);
        fieldGameName.positionCaret(fieldGameName.getText().length());
        fieldGameName.setOnKeyTyped(e -> {
            if (fieldGameName.getText().length() > 25) {
                fieldGameName.setText(fieldGameName.getText().substring(0, 25));
                fieldGameName.positionCaret(25);
            }
        });
        fieldGameName.setStyle("-fx-border-color: grey");
        Button btnSaveAndQuit = new Button("Save and quit");
        Button btnQuitNoSave = new Button("Quit without saving");
        String cssEnteredButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 10px; -fx-text-fill: #ffffff;" ;
        String cssExitedButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 10px;";
        Button[] btnSave = new Button[]{btnSaveAndQuit, btnQuitNoSave};
        for (var btn : btnSave) {
            btn.setFont(new Font("Comic Sans MS", 50));
            btn.setStyle(cssExitedButton);
            btn.setOnMouseEntered(e -> btn.setStyle(cssEnteredButton));
            btn.setOnMouseExited(e -> btn.setStyle(cssExitedButton));
        }
        btnSaveAndQuit.setOnAction(e -> {
            String fileName = fieldGameName.getText();
            if (fileName.isEmpty()) return;

            GameInfoObject gameInfo = new GameInfoObject(SudokuPane.getTilesList(), time, PlayPane.getDifficulty(), PlayPane.getMistakes());

            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/saves/" + fileName + ".dat"))) {
                oos.writeObject(gameInfo);
            } catch (IOException ex) {
                System.out.println("Something went wrong while saving the game");
            } finally {
                mainMenu();
            }
        });
        btnQuitNoSave.setOnAction(e -> {
            mainMenu();
        });
        VBox savePane = new VBox(50, labelSave, labelSave2, fieldGameName, btnSaveAndQuit, btnQuitNoSave);
        savePane.setPrefSize(WIDTH, HEIGHT);
        savePane.setMaxSize(WIDTH, HEIGHT);
        savePane.setAlignment(Pos.CENTER);

        PaneMover.moveTo(savePane, "left");

    }
}