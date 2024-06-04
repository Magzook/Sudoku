package client.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class ChooseGamePane {
    private static int page = 1;
    private static int maxPageNumber = 1;
    private static final int PAGE_SIZE = 5;
    private static ArrayList<Button> btnGameNames;
    private static VBox deletePane;
    private static VBox namePane;
    public static void setPage(int page) {ChooseGamePane.page = page;}

    public static void show() {
        Label labelSavedGames = new Label("Saved games");
        labelSavedGames.setFont(new Font("Calibri", 100));
        Label description = new Label("Choose the game");
        description.setFont(new Font("Calibri", 30));

        btnGameNames = new ArrayList<>();
        File dir = new File("src/main/resources/saves");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile()) {
                String strGameName = file.getName().replace(".dat", "");
                Button btnGameName = new Button(strGameName);
                btnGameName.setFont(new Font("Consolas", 25));
                btnGameName.setPrefWidth(400);
                btnGameName.setOnAction(e -> {
                    try {
                        PlayPane.show(file.getName());
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                btnGameNames.add(btnGameName);
            }
        }

        if (btnGameNames.isEmpty()) maxPageNumber = 1;
        else maxPageNumber = (btnGameNames.size() - 1) / PAGE_SIZE + 1;

        deletePane = new VBox(10);
        namePane = new VBox(10);
        arrangePage();
        BorderPane savedGamesPane = new BorderPane();
        savedGamesPane.setLeft(deletePane);
        savedGamesPane.setRight(namePane);
        savedGamesPane.setPrefSize(Game.WIDTH, 300);
        savedGamesPane.setMaxSize(Game.WIDTH, 300);

        Button pageNext = new Button("Next page");
        pageNext.setOnAction(e -> {
            if (page != maxPageNumber) {
                page++;
                arrangePage();
            }
        });
        Button pagePrev = new Button("Previous page");
        pagePrev.setOnAction(e -> {
            if (page != 1) {
                page--;
                arrangePage();
            }
        });
        Button[] pageButtons = new Button[] {pagePrev, pageNext};
        String cssEnteredPageButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 5px; -fx-text-fill: #ffffff;";
        String cssExitedPageButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 5px;";
        for (var btn : pageButtons) {
            btn.setFont(new Font("Comic Sans MS", 25));
            btn.setStyle(cssExitedPageButton);
            btn.setOnMouseEntered(e -> btn.setStyle(cssEnteredPageButton));
            btn.setOnMouseExited(e -> btn.setStyle(cssExitedPageButton));
        }
        BorderPane pagesButtonsPane = new BorderPane();
        pagesButtonsPane.setLeft(pagePrev);
        pagesButtonsPane.setRight(pageNext);

        Button backToMainMenu = new Button("Back to Main Menu");
        Font buttonFont = new Font("Comic Sans MS", 50);
        backToMainMenu.setFont(buttonFont);
        String cssEnteredButton = "-fx-background-color: #6666ff; -fx-border-color: #4d4dff; -fx-border-width: 10px; -fx-text-fill: #ffffff;" ;
        String cssExitedButton = "-fx-background-color: #8080ff; -fx-border-color: #6666ff; -fx-border-width: 10px;";
        backToMainMenu.setStyle(cssExitedButton);
        backToMainMenu.setOnMouseEntered(e -> backToMainMenu.setStyle(cssEnteredButton));
        backToMainMenu.setOnMouseExited(e -> backToMainMenu.setStyle(cssExitedButton));
        backToMainMenu.setOnAction(e -> {
            Game.mainMenu();
        });

        VBox loadPane = new VBox(40, labelSavedGames, description, savedGamesPane, pagesButtonsPane, backToMainMenu);
        loadPane.setPrefSize(Game.WIDTH, Game.HEIGHT);
        loadPane.setMaxSize(Game.WIDTH, Game.HEIGHT);
        loadPane.setAlignment(Pos.CENTER);

        PaneMover.moveTo(loadPane, "right");
    }

    private static void arrangePage() {
        // Если у тебя миллиард сохранённых игр, то они все на экран не поместятся
        // Надо отобразить только 5 из них
        // (также в этом методе описан функционал кнопки удаления)
        // Сначала надо очистить панели
        deletePane.getChildren().clear();
        namePane.getChildren().clear();
        // Теперь надо в них поместить 5 сохранённых игр согласно текущему номеру страницы
        // (на последней странице их может быть меньше
        for (int i = PAGE_SIZE * (page - 1); i < PAGE_SIZE * page && i < btnGameNames.size(); i++) {
            namePane.getChildren().add(btnGameNames.get(i));
            Button btnDelete = new Button("X");
            btnDelete.setFont(new Font("Consolas", 25));
            btnDelete.setOnAction(e -> {
                // Определить позицию кнопки
                int pos = -1;
                for (int j = 0; j < 5; j++) {
                    Button btn = (Button) deletePane.getChildren().get(j);
                    if (btn == btnDelete) {
                        pos = j;
                        break;
                    }
                }
                // Извлечь название игры
                Button btnGameName = (Button) namePane.getChildren().get(pos);
                String gameName = btnGameName.getText();
                // Удалить кнопку из коллекции
                btnGameNames.remove(btnGameName);
                // Удалить файл с игрой
                new File("src/main/resources/saves/" + gameName + ".dat").delete();
                // Удалить кнопку удаления и кнопку названия игры
                deletePane.getChildren().remove(pos);
                namePane.getChildren().remove(pos);
                // Если на странице не осталось кнопок, надо перейти на предыдущую страницу
                if (deletePane.getChildren().isEmpty() && page != 1) {
                    page--;
                    maxPageNumber--;
                }
                // Заново отобразить страницу
                arrangePage();
            });
            deletePane.getChildren().add(btnDelete);
        }
    }
}
