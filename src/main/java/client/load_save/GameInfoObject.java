package client.load_save;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.io.Serializable;
import java.util.ArrayList;

public class GameInfoObject implements Serializable {
    private transient ArrayList<StackPane> tilesList;
    private byte[][] digits;
    private byte[][] colors;
    private boolean[][][] candidates;
    private String time;
    private String difficulty;
    private int mistakes;
    public GameInfoObject(ArrayList<StackPane> tilesList, String time, String difficulty, int mistakes) {
        this.tilesList = tilesList;
        digits = new byte[9][9];
        colors = new byte[9][9];
        candidates = new boolean[9][9][9];
        writeBoard();
        this.time = time;
        this.difficulty = difficulty;
        this.mistakes = mistakes;
    }
    public String getTime() {return time;}
    public String getDifficulty() {return difficulty;}
    public int getMistakes() {return mistakes;}
    public byte[][] getDigits() {return digits;}
    public byte[][] getColors() {return colors;}
    public boolean[][][] getCandidates() {return candidates;}

    private void writeBoard() {
        int i = 0, j = 0;
        for (var tile : tilesList) {
            Label numberLabel = (Label) tile.getChildren().getLast();
            String strNumber = numberLabel.getText();
            if (!strNumber.isEmpty()) {
                // Цифра и число
                digits[i][j] = (byte) Integer.parseInt(strNumber);
                Paint color = numberLabel.getTextFill();
                if (color.equals(Color.BLACK)) colors[i][j] = 1;
                else if (color.equals(Color.BLUE)) colors[i][j] = 2;
                else if (color.equals(Color.RED)) colors[i][j] = -1;
            }
            else {
                // Кандидаты при наличии
                StackPane candidatesPane = (StackPane) tile.getChildren().get(1);
                FlowPane candidatesLabelsPane = (FlowPane) candidatesPane.getChildren().getLast();
                for (int k = 0; k < 9; k++) {
                    Label candidateLabel = (Label) candidatesLabelsPane.getChildren().get(k);
                    if (candidateLabel.isVisible()) candidates[i][j][k] = true;
                }
            }
            j++;
            if (j == 9) {j = 0; i++;}
        }
    }
}
