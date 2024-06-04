package client.ui;

import client.load_save.GameInfoObject;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class PlayPane {
    private static VBox playPane; // Собственно главная панель игры
    private static String difficulty;
    private static boolean eraserMode = false;
    private static boolean pencilMode = false;
    private static int chosenDigit = 0;
    private static int mistakes = 0;
    private static int digitsLeft;
    public static VBox getPlayPane() {return playPane;}
    public static String getDifficulty() {return difficulty;}
    public static void setDifficulty(String difficulty) {PlayPane.difficulty = difficulty;}
    public static boolean getEraserMode() {return eraserMode;}
    public static boolean getPencilMode() {return pencilMode;}
    public static int getChosenDigit() {return chosenDigit;}
    public static int getMistakes() {return mistakes;}

    public static void decrementDigitsCounter() {
        digitsLeft--;
        if (digitsLeft == 0) Game.victory();
    }

    public static void incrementMistakesCounter() {
        mistakes++;
        StackPane statsPane = (StackPane) playPane.getChildren().get(1);
        HBox mistakesPane = (HBox) statsPane.getChildren().getFirst();
        Label labelMistakes = (Label) mistakesPane.getChildren().getFirst();
        labelMistakes.setText("Mistakes: " + mistakes + " / 3");
        if (mistakes == 3) Game.defeat();
    }

    public static void show(String fileName) throws FileNotFoundException {
        eraserMode = false;
        pencilMode = false;
        chosenDigit = 0;
        mistakes = 0;

        Button btnPauseResume = new Button("|| Pause");
        Button btnQuit = new Button("<< Quit");
        Font buttonFont = new Font("Comic Sans MS", 18);
        String cssEnteredButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 6px; -fx-text-fill: #ffffff;" ;
        String cssExitedButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 6px;";
        Button[] btnTopPane = new Button[]{btnQuit, btnPauseResume};
        for (var btn : btnTopPane) {
            btn.setFont(buttonFont);
            btn.setStyle(cssExitedButton);
            btn.setOnMouseEntered(e -> btn.setStyle(cssEnteredButton));
            btn.setOnMouseExited(e -> btn.setStyle(cssExitedButton));
        }
        btnQuit.setOnAction(e -> {
            Game.save();
        });
        btnPauseResume.setOnAction(e -> {
            MyTimer.pause();
            Game.pause();
        });
        BorderPane topPane = new BorderPane();
        topPane.setLeft(btnQuit);
        topPane.setRight(btnPauseResume);
        topPane.setPrefSize(Game.WIDTH, 100);
        topPane.setMaxSize(Game.WIDTH, 100);

        Label labelMistakes = new Label("Mistakes: 0 / 3");
        Label labelDifficulty = new Label(difficulty);
        Label labelTimer = new Label("00:00");
        MyTimer.setLabelTimer(labelTimer);

        Label[] labelsStats = new Label[]{labelMistakes, labelDifficulty, labelTimer};
        Font labelFont = new Font("System", 25);
        for (var label : labelsStats) {
            label.setFont(labelFont);
        }
        HBox mistakesPane = new HBox(labelMistakes);
        mistakesPane.setAlignment(Pos.BOTTOM_LEFT);
        mistakesPane.setMaxSize(480, 30);
        HBox difficultyPane = new HBox(labelDifficulty);
        difficultyPane.setAlignment(Pos.BOTTOM_CENTER);
        difficultyPane.setMaxSize(480, 30);
        HBox timerPane = new HBox(labelTimer);
        timerPane.setAlignment(Pos.BOTTOM_RIGHT);
        timerPane.setMaxSize(460, 30);
        StackPane statsPane = new StackPane(mistakesPane, difficultyPane, timerPane);
        statsPane.setPrefSize(480, 30);
        statsPane.setMaxSize(480, 30);

        StackPane sudokuPane = SudokuPane.draw();

        if (fileName.isEmpty()) {
            // Если это новая игра
            // Выставляем значения по умолчанию
            labelTimer.setText("00:00");
            //difficulty был выставлен в предыдущем методе в лямбда-выражении
            mistakes = 0;
            // Генерируем судоку
            SudokuPane.fill();
        }
        else {
            // Если это загруженная игра, то все значения выгружаются из файла
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("src/main/resources/saves/" + fileName))) {
                GameInfoObject gameInfo = (GameInfoObject) ois.readObject();
                // Выставляем время, сложность, счётчик ошибок
                labelTimer.setText(gameInfo.getTime());
                MyTimer.setMinutes(Integer.parseInt(gameInfo.getTime().substring(0, 2)));
                MyTimer.setSeconds(Integer.parseInt(gameInfo.getTime().substring(3)));
                difficulty = gameInfo.getDifficulty();
                labelDifficulty.setText(difficulty);
                mistakes = gameInfo.getMistakes();
                // Судоку надо восстановить
                SudokuPane.restore(gameInfo);

            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        labelMistakes.setText("Mistakes: " + mistakes + " / 3");
        digitsLeft = SudokuPane.getDigitsLeft();

        Button btnEraser = new Button("Eraser");
        Button btnPencil = new Button("Pencil");
        Button btnSuperPencil = new Button("Super Pencil");
        Button[] btnControlPane = new Button[]{btnEraser, btnPencil, btnSuperPencil};
        String cssTransparentBackground = "-fx-background-color: transparent;";
        String cssGrayBackground = "-fx-background-color: #dddddd;";
        for (var btn : btnControlPane) {
            btn.setFont(new Font("System", 20));
            btn.setPrefWidth(150);
            btn.setMaxWidth(150);
            btn.setContentDisplay(ContentDisplay.TOP);
            btn.setStyle(cssTransparentBackground);
            btn.setOnMouseEntered(e -> btn.setStyle(cssGrayBackground));
            btn.setOnMouseExited(e -> btn.setStyle(cssTransparentBackground));
        }
        // Modified icon made by Freepik from www.flaticon.com
        ImageView imgEraserOFF = new ImageView(new Image(new FileInputStream("src/main/resources/button_icons/eraser_off.png")));
        // Modified icon made by Freepik from www.flaticon.com
        ImageView imgEraserON = new ImageView(new Image(new FileInputStream("src/main/resources/button_icons/eraser_on.png")));
        // Modified icon made by Icon home from www.flaticon.com
        ImageView imgPencilOFF = new ImageView(new Image(new FileInputStream("src/main/resources/button_icons/pencil_off.png")));
        // Modified icon made by Icon home from www.flaticon.com
        ImageView imgPencilON = new ImageView(new Image(new FileInputStream("src/main/resources/button_icons/pencil_on.png")));
        // Icon made by lalawidi from www.flaticon.com
        ImageView imgSuperPencil = new ImageView(new Image(new FileInputStream("src/main/resources/button_icons/super_pencil.png")));
        btnEraser.setGraphic(imgEraserOFF);
        btnPencil.setGraphic(imgPencilOFF);
        btnSuperPencil.setGraphic(imgSuperPencil);
        ImageView[] imgs = new ImageView[]{imgEraserOFF, imgEraserON, imgPencilOFF, imgPencilON, imgSuperPencil};
        for (var img : imgs) {
            img.setFitWidth(80);
            img.setFitHeight(80);
        }
        btnEraser.setOnAction(e -> {
            eraserMode = !eraserMode;
            if (eraserMode) {
                btnEraser.setGraphic(imgEraserON);
                pencilMode = false;
                btnPencil.setGraphic(imgPencilOFF);
            }
            else {btnEraser.setGraphic(imgEraserOFF);}
        });
        btnPencil.setOnAction(e -> {
            pencilMode = !pencilMode;
            if (pencilMode) {
                btnPencil.setGraphic(imgPencilON);
                eraserMode = false;
                btnEraser.setGraphic(imgEraserOFF);
            }
            else {btnPencil.setGraphic(imgPencilOFF);}
        });
        btnSuperPencil.setOnAction(e -> SudokuPane.superPencil());
        HBox controlPane = new HBox(50, btnControlPane);
        controlPane.setAlignment(Pos.BOTTOM_CENTER);
        controlPane.setPrefSize(Game.WIDTH, 150);
        controlPane.setMaxSize(Game.WIDTH, 150);

        Button[] btnDigit = new Button[9];
        for (int i = 1; i <= 9; i++) {
            btnDigit[i-1] = new Button(Integer.toString(i));
        }
        String cssBtnDigitMouseExited = "-fx-background-color: #eeeeee; -fx-border-color: #dddddd; -fx-border-width: 2px;";
        String cssBtnDigitMouseEntered = "-fx-background-color: #dddddd; -fx-border-color: #cccccc; -fx-border-width: 2px;";
        Font fontBtnDigitOff = new Font("System Bold", 20);
        Font fontBtnDigitOn = new Font("System Bold", 30);
        for (var btn : btnDigit) {
            btn.setFont(fontBtnDigitOff);
            btn.setStyle(cssBtnDigitMouseExited);
            btn.setOnMouseEntered(e -> btn.setStyle(cssBtnDigitMouseEntered));
            btn.setOnMouseExited(e -> btn.setStyle(cssBtnDigitMouseExited));
            btn.setOnAction(e -> {
                int digit = Integer.parseInt(btn.getText());
                if (digit == chosenDigit) {
                    chosenDigit = 0;
                    btn.setFont(fontBtnDigitOff);
                }
                else {
                    if (chosenDigit != 0) btnDigit[chosenDigit - 1].setFont(fontBtnDigitOff);
                    chosenDigit = digit;
                    btn.setFont(fontBtnDigitOn);
                }
            });
        }
        HBox digitPane = new HBox(10, btnDigit);
        digitPane.setPrefSize(Game.WIDTH, 150);
        digitPane.setMaxSize(Game.WIDTH, 150);
        digitPane.setAlignment(Pos.CENTER);

        playPane = new VBox(topPane, statsPane, sudokuPane, controlPane, digitPane);
        playPane.setAlignment(Pos.CENTER);

        PaneMover.moveTo(playPane, "right");

        MyTimer.start();
    }
}
