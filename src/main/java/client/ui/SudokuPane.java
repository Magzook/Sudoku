// Отрисовка и функционирование доски судоку

package client.ui;

import client.generator.SudokuGenerator;
import client.load_save.GameInfoObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import java.util.ArrayList;

public class SudokuPane {
    private static StackPane sudokuBoard;
    private static ArrayList<StackPane> tilesList; // Коллекция клеток (81 шт.)
    private static ArrayList<FlowPane> areasList; // Коллекция областей 3x3 (9 шт.)
    private static byte[][] sudokuSolved; // Массив - решённое судоку
    private static byte[][] sudokuStart; // Массив - нерешённое судоку
    public static byte[][] getSudokuSolved() {return sudokuSolved;}
    public static ArrayList<StackPane> getTilesList() {return tilesList;}
    private static void handleTileEnter(StackPane enteredTile) { // Наведение курсора на клетку
        // Определить позицию клетки
        int pos = -1;
        for (int i = 0; i < 81; i++) {
            if (tilesList.get(i) == enteredTile) pos = i;
        }
        // Подсветить столбец
        int column = pos % 9;
        for (int i = column; i < 81; i+= 9) {
            Rectangle tileBackground = (Rectangle) (tilesList.get(i)).getChildren().getFirst();
            tileBackground.setFill(Color.LIGHTGRAY);
        }
        // Подсветить строку
        int row = pos / 9;
        for (int i = row * 9; i < (row + 1) * 9; i++) {
            Rectangle tileBackground = (Rectangle) (tilesList.get(i)).getChildren().getFirst();
            tileBackground.setFill(Color.LIGHTGRAY);
        }
        // Подсветить область
        for (var area : areasList) {
            boolean quitFlag = false;
            for (int i = 0; i < 9; i++) {
                StackPane tile = (StackPane) area.getChildren().get(i);
                if (tile == enteredTile) {
                    for (int j = 0; j < 9; j++) {
                        StackPane _tile = (StackPane) area.getChildren().get(j);
                        Rectangle tileBackground = (Rectangle) _tile.getChildren().getFirst();
                        tileBackground.setFill(Color.LIGHTGRAY);
                    }
                    quitFlag = true;
                    break;
                }
            }
            if (quitFlag) break;
        }
        // Подсветить клетки с такими же цифрами (неверные цифры не учитываются)
        Label enteredLabel = (Label) enteredTile.getChildren().getLast();
        String enteredNumber = enteredLabel.getText();
        if (!enteredNumber.isEmpty() && !enteredLabel.getTextFill().equals(Color.RED)) {
            for (var tile : tilesList) {
                Label numberLabel = (Label) tile.getChildren().getLast();
                if (numberLabel.getTextFill().equals(Color.RED)) continue;
                String number = numberLabel.getText();
                if (number.equals(enteredNumber)) {
                    Rectangle tileBackground = (Rectangle) tile.getChildren().getFirst();
                    tileBackground.setFill(Color.DEEPSKYBLUE);
                }
            }
        }
        // Подсветить текущую клетку более тёмным цветом
        Rectangle background = (Rectangle) enteredTile.getChildren().getFirst();
        background.setFill(Color.GRAY);
        // Подсветить всех таких же кандидатов
        if (!enteredNumber.isEmpty() && !enteredLabel.getTextFill().equals(Color.RED)) {
            for (var tile : tilesList) {
                StackPane candidatesPane = (StackPane) tile.getChildren().get(1);
                FlowPane candidatesDigitsPane = (FlowPane) candidatesPane.getChildren().getLast();
                Label candidateDigit = (Label) candidatesDigitsPane.getChildren().get(Integer.parseInt(enteredNumber) - 1);
                if (candidateDigit.isVisible()) {
                    FlowPane candidatesBackgroundsPane = (FlowPane) candidatesPane.getChildren().getFirst();
                    Rectangle candidateBackground = (Rectangle) candidatesBackgroundsPane.getChildren().get(Integer.parseInt(enteredNumber) - 1);
                    candidateBackground.setFill(Color.DEEPSKYBLUE);
                }
            }
        }
    }

    private static void handleTileExit(StackPane exitedTile) { // Отведение курсора от клетки
        // Очистить всю подсветку
        for (var tile : tilesList) {
            Rectangle tileBackground = (Rectangle) tile.getChildren().getFirst();
            tileBackground.setFill(Color.WHITE);

            StackPane candidatesPane = (StackPane) tile.getChildren().get(1);
            FlowPane candidatesBackgroundsPane = (FlowPane) candidatesPane.getChildren().getFirst();
            for (int i = 0; i < 9; i++) {
                Rectangle candidateBackground = (Rectangle) candidatesBackgroundsPane.getChildren().get(i);
                candidateBackground.setFill(Color.TRANSPARENT);
            }
        }
    }

    private static void handleTileClick(StackPane clickedTile, int chosenDigit) {
        Label clickedNumberLabel = (Label) clickedTile.getChildren().getLast();
        String clickedNumber = clickedNumberLabel.getText();
        StackPane clickedCandidatesPane = (StackPane) clickedTile.getChildren().get(1);

        if (PlayPane.getPencilMode()) {
            // Вставить или удалить кандидата
            if (chosenDigit == 0 || !clickedNumber.isEmpty()) return;
            // Вставка кандидата допустима, только если соответствующая цифра
            // отсутствует в данном столбце, строке и области
            if (candidateIsValid(clickedTile, chosenDigit)) {
                FlowPane candidatesDigitsPane = (FlowPane) clickedCandidatesPane.getChildren().getLast();
                Label candidate = (Label) candidatesDigitsPane.getChildren().get(chosenDigit - 1);
                candidate.setVisible(!candidate.isVisible());
            }

        }
        else if (PlayPane.getEraserMode()) {
            // Стереть цифру (неверную) или всех кандидатов в клетке (выполняется и то, и то)
            if (clickedNumberLabel.getTextFill().equals(Color.RED))
                clickedNumberLabel.setText("");
            FlowPane candidatesDigitsPane = (FlowPane) clickedCandidatesPane.getChildren().getLast();
            for (int i = 0; i < 9; i++) {
                Label candidate = (Label) candidatesDigitsPane.getChildren().get(i);
                candidate.setVisible(false);
            }
        }
        else {
            // Вставить цифру
            if (chosenDigit == 0 || !clickedNumber.isEmpty()) return;
            clickedNumberLabel.setText(Integer.toString(chosenDigit));
            // При вставке цифры все кандидаты в этой клетке стираются
            FlowPane candidatesDigitsPane = (FlowPane) clickedCandidatesPane.getChildren().getLast();
            for (int i = 0; i < 9; i++) {
                Label candidate = (Label) candidatesDigitsPane.getChildren().get(i);
                candidate.setVisible(false);
            }
            // Вставленную цифру необходимо проверить на правильность и выставить ей нужный цвет
            // Для этого надо определить позицию клетки и сравнить вставленное число с конечным
            // решением из массива sudokuSolved (решение единственное!)
            int pos = -1;
            for (int i = 0; i < 81; i++) {
                if (tilesList.get(i) == clickedTile) pos = i;
            }
            int row = pos / 9; int col = pos % 9;
            if (sudokuSolved[row][col] != chosenDigit) {
                clickedNumberLabel.setTextFill(Color.RED);
                // Если цифра неверна, нужно увеличить счётчик ошибок
                PlayPane.incrementMistakesCounter();
            }
            else {
                clickedNumberLabel.setTextFill(Color.BLUE);
                // Если цифра верна, нужно стереть соответствующих кандидатов
                // из этого столбца, строки и области
                removeCandidates(clickedTile, row, col);
                // а также подсветить такие же цифры
                handleTileEnter(clickedTile);
                // а также изменить в Game счётчик пустых клеток
                PlayPane.decrementDigitsCounter();
            }
        }
    }

    private static boolean candidateIsValid(StackPane clickedTile, int chosenDigit) { // Проверка кандидата на возможность вставки его в клетку
        // Определить позицию клетки
        int pos = -1;
        for (int i = 0; i < 81; i++) {
            if (tilesList.get(i) == clickedTile) pos = i;
        }
        int row = pos / 9;
        int col = pos % 9;
        // Проверить столбец
        for (int i = col; i < 81; i += 9) {
            StackPane tile = tilesList.get(i);
            Label numberLabel = (Label) tile.getChildren().getLast();
            if (!numberLabel.getText().isEmpty() && !numberLabel.getTextFill().equals(Color.RED)) {
                int number = Integer.parseInt(numberLabel.getText());
                if (number == chosenDigit) return false;
            }
        }
        // Проверить строку
        for (int i = row * 9; i < (row + 1) * 9; i++) {
            StackPane tile = tilesList.get(i);
            Label numberLabel = (Label) tile.getChildren().getLast();
            if (!numberLabel.getText().isEmpty() && !numberLabel.getTextFill().equals(Color.RED)) {
                int number = Integer.parseInt(numberLabel.getText());
                if (number == chosenDigit) return false;
            }
        }
        // Проверить область
        for (var area : areasList) {
            boolean quitFlag = false;
            for (int i = 0; i < 9; i++) {
                StackPane tile = (StackPane) area.getChildren().get(i);
                if (tile == clickedTile) {
                    for (int j = 0; j < 9; j++) {
                        StackPane _tile = (StackPane) area.getChildren().get(j);
                        Label numberLabel = (Label) _tile.getChildren().getLast();
                        if (!numberLabel.getText().isEmpty() && !numberLabel.getTextFill().equals(Color.RED)) {
                            int number = Integer.parseInt(numberLabel.getText());
                            if (number == chosenDigit) return false;
                        }
                    }
                    quitFlag = true;
                    break;
                }
            }
            if (quitFlag) break;
        }

        return true;
    }

    private static void removeCandidates(StackPane clickedTile, int row, int col) { // Удалить лишних кандидатов
        // Определить вставленное число
        Label numberLabel = (Label) clickedTile.getChildren().getLast();
        int inputNumber = Integer.parseInt(numberLabel.getText());

        // Удалить кандидатов из столбца
        for (int i = col; i < 81; i += 9) {
            StackPane tile = tilesList.get(i);
            StackPane candidatesPane = (StackPane) tile.getChildren().get(1);
            FlowPane candidatesDigitsPane = (FlowPane) candidatesPane.getChildren().getLast();
            Label candidate = (Label) candidatesDigitsPane.getChildren().get(inputNumber - 1);
            candidate.setVisible(false);
        }
        // Удалить кандидатов из строки
        for (int i = row * 9; i < (row + 1) * 9; i++) {
            StackPane tile = tilesList.get(i);
            StackPane candidatesPane = (StackPane) tile.getChildren().get(1);
            FlowPane candidatesDigitsPane = (FlowPane) candidatesPane.getChildren().getLast();
            Label candidate = (Label) candidatesDigitsPane.getChildren().get(inputNumber - 1);
            candidate.setVisible(false);
        }
        // Удалить кандидатов из области
        for (var area : areasList) {
            boolean quitFlag = false;
            for (int i = 0; i < 9; i++) {
                StackPane tile = (StackPane) area.getChildren().get(i);
                if (tile == clickedTile) {
                    for (int j = 0; j < 9; j++) {
                        StackPane _tile = (StackPane) area.getChildren().get(j);
                        StackPane candidatesPane = (StackPane) _tile.getChildren().get(1);
                        FlowPane candidatesDigitsPane = (FlowPane) candidatesPane.getChildren().getLast();
                        Label candidate = (Label) candidatesDigitsPane.getChildren().get(inputNumber - 1);
                        candidate.setVisible(false);
                    }
                    quitFlag = true;
                    break;
                }
            }
            if (quitFlag) break;
        }
    }

    public static void superPencil() { // Автоматическая расстановка кандидатов
        for (var tile : tilesList) {
            Label numberLabel = (Label) tile.getChildren().getLast();
            if (numberLabel.getText().isEmpty()) {
                for (int number = 1; number <= 9; number++) {
                    if (candidateIsValid(tile, number)) {
                        StackPane candidatesPane = (StackPane) tile.getChildren().get(1);
                        FlowPane candidatesDigitsPane = (FlowPane) candidatesPane.getChildren().getLast();
                        Label candidate = (Label) candidatesDigitsPane.getChildren().get(number - 1);
                        candidate.setVisible(true);
                    }
                }
            }
        }
    }

    public static int getDigitsLeft() {
        int zeros = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuStart[i][j] == 0) zeros++;
            }
        }
        return zeros;
    }

    public static StackPane draw() {
        areasList = new ArrayList<>();

        Rectangle grid = new Rectangle(470, 470);
        grid.setFill(Color.BLACK);

        FlowPane areas = new FlowPane(3, -2);
        areas.setMaxSize(460, 460);
        int w = 0;
        for (int i = 0; i < 9; i++) {
            FlowPane area = new FlowPane(5, -5);
            area.setMaxWidth(150);

            for (int j = 0; j < 9; j++) {
                Rectangle tileBackground = new Rectangle(45, 45);
                tileBackground.setFill(Color.WHITE);

                FlowPane candidatesDigitsPane = new FlowPane(7, -3);
                candidatesDigitsPane.setMaxSize(45, 45);
                candidatesDigitsPane.setPrefSize(45, 45);
                candidatesDigitsPane.alignmentProperty().setValue(Pos.CENTER);
                Label[] candidatesDigits = new Label[9];
                for (int k = 0; k < 9; k++) {
                    candidatesDigits[k] = new Label(Integer.toString(k+1));
                    candidatesDigits[k].setVisible(false);
                    candidatesDigits[k].setFont(new Font("System Bold", 12));
                    candidatesDigitsPane.getChildren().add(candidatesDigits[k]);
                }
                FlowPane candidatesBackgroundPane = new FlowPane();
                candidatesBackgroundPane.setMaxSize(45, 45);
                candidatesBackgroundPane.setPrefSize(45, 45);
                Rectangle[] candidatesBackgrounds = new Rectangle[9];
                for (int k = 0; k < 9; k++) {
                    candidatesBackgrounds[k] = new Rectangle(15, 15);
                    candidatesBackgroundPane.getChildren().add(candidatesBackgrounds[k]);
                    candidatesBackgrounds[k].setFill(Color.TRANSPARENT);
                }
                StackPane candidatesPane = new StackPane(candidatesBackgroundPane, candidatesDigitsPane);
                candidatesPane.setMaxSize(45, 45);
                candidatesPane.setPrefSize(45, 45);

                Label number = new Label();
                number.fontProperty().setValue(new Font("System Bold", 38));
                number.setTextFill(Color.BLACK);

                StackPane tile = new StackPane();
                tile.setMaxWidth(35);
                tile.setMaxHeight(35);
                tile.getChildren().add(tileBackground);
                tile.getChildren().add(candidatesPane);
                tile.getChildren().add(number);
                tile.setOnMouseEntered(e -> {
                    handleTileEnter(tile);
                    tile.requestFocus();
                    tile.setOnKeyPressed(ee -> {
                        if (ee.getCode().isDigitKey()) {
                            handleTileClick(tile, Integer.parseInt(ee.getCode().getName()));
                        }
                    });
                });
                tile.setOnMouseExited(e -> {
                    handleTileExit(tile);
                    tile.setOnKeyPressed(null);
                });
                tile.setOnMouseClicked(e -> handleTileClick(tile, PlayPane.getChosenDigit()));
                area.getChildren().add(tile);
            }
            areas.getChildren().add(area);
            areasList.add(area);
        }
        // Сформировать массив клеток
        tilesList = new ArrayList<>();
        for (int i = 0; i < 9; i+=3) {
            for (int j = i; j < i + 3; j++) {
                FlowPane area = areasList.get(j);
                tilesList.add((StackPane)area.getChildren().get(0));
                tilesList.add((StackPane)area.getChildren().get(1));
                tilesList.add((StackPane)area.getChildren().get(2));
            }
            for (int j = i; j < i + 3; j++) {
                FlowPane area = areasList.get(j);
                tilesList.add((StackPane)area.getChildren().get(3));
                tilesList.add((StackPane)area.getChildren().get(4));
                tilesList.add((StackPane)area.getChildren().get(5));
            }
            for (int j = i; j < i + 3; j++) {
                FlowPane area = areasList.get(j);
                tilesList.add((StackPane)area.getChildren().get(6));
                tilesList.add((StackPane)area.getChildren().get(7));
                tilesList.add((StackPane)area.getChildren().get(8));
            }
        }

        sudokuBoard = new StackPane();
        sudokuBoard.setMaxSize(480, 480);
        sudokuBoard.getChildren().add(grid);
        StackPane.setAlignment(grid, Pos.TOP_LEFT);
        sudokuBoard.getChildren().add(areas);
        StackPane.setAlignment(areas, Pos.CENTER);

        return sudokuBoard;
    }

    public static void fill() {
        sudokuSolved = SudokuGenerator.generate();
        sudokuStart = SudokuGenerator.removeNumbers(sudokuSolved);

        int i = 0, j = 0;
        for (var tile : tilesList) {
            Label numberLabel = (Label) tile.getChildren().getLast();
            int number = (sudokuStart[i][j]);
            if (number != 0) numberLabel.setText(Integer.toString(number));
            j++;
            if (j == 9) {j = 0; i++;}
        }
    }

    public static void restore(GameInfoObject gameInfo) {
        byte[][] digits = gameInfo.getDigits();
        byte[][] colors = gameInfo.getColors();
        boolean[][][] candidates = gameInfo.getCandidates();

        int i = 0, j = 0;
        for (var tile : tilesList) {
            if (digits[i][j] != 0) {
                Label numberLabel = (Label) tile.getChildren().getLast();
                numberLabel.setText(Integer.toString(digits[i][j]));
                switch (colors[i][j]) {
                    case 1:
                        numberLabel.setTextFill(Color.BLACK);
                        break;
                    case 2:
                        numberLabel.setTextFill(Color.BLUE);
                        break;
                    case -1:
                        numberLabel.setTextFill(Color.RED);
                        break;
                }
            }
            else {
                StackPane candidatesPane = (StackPane) tile.getChildren().get(1);
                FlowPane candidatesLabelsPane = (FlowPane) candidatesPane.getChildren().getLast();
                for (int k = 0; k < 9; k++) {
                    Label candidateLabel = (Label) candidatesLabelsPane.getChildren().get(k);
                    candidateLabel.setVisible(candidates[i][j][k]);
                }
            }
            j++;
            if (j == 9) {j = 0; i++;}
        }

        sudokuStart = digits;
        // из sudokuStart нужно убрать все красные цифры, иначе не удастся получить его решение
        for (int k = 0; k < 9; k++) {
            for (int l = 0; l < 9; l++) {
                if (colors[k][l] == -1) sudokuStart[k][l] = 0;
            }
        }
        SudokuGenerator.setBoard(sudokuStart);
        SudokuGenerator.solveSudoku();
        sudokuSolved = SudokuGenerator.getBoard();
    }
}
