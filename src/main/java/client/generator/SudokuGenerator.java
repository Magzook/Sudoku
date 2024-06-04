package client.generator;

import client.ui.PlayPane;
import client.ui.SudokuPane;
import java.util.Random;

public class SudokuGenerator {
    private static final int SIZE = 9;
    private static final int AREA_SIZE = 3;
    private static final int EMPTY = 0;
    private static byte[][] board;
    private static Random random;

    public static void setBoard(byte[][] inputBoard) {
        board = new byte[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = inputBoard[i][j];
            }
        }
    }
    public static byte[][] getBoard() {
        return board;
    }

    public static byte[][] generate() {
        board = new byte[SIZE][SIZE];
        random = new Random();
        fillDiagonalBlocks();
        solveSudoku();
        return board;
    }
    private static void fillDiagonalBlocks() {
        for (int i = 0; i < SIZE; i += AREA_SIZE) {
            int[] numList = random.ints(1, SIZE + 1).distinct().limit(SIZE).toArray();
            int idx = 0;
            for (int j = 0; j < AREA_SIZE; j++) {
                for (int k = 0; k < AREA_SIZE; k++) {
                    board[i + j][i + k] = (byte) numList[idx++];
                }
            }
        }
    }
    public static boolean solveSudoku() {
        int[] empty = findEmptyLocation();
        if (empty == null) {
            return true;
        }
        int row = empty[0];
        int col = empty[1];

        for (int num = 1; num <= SIZE; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = (byte) num;
                if (solveSudoku()) {
                    return true;
                }
                board[row][col] = EMPTY;
            }
        }
        return false;
    }

    public static byte[][] removeNumbers(byte[][] _board) {
        // Копирование
        board = new byte[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = _board[i][j];
            }
        }
        int attempts = 100;
        while (attempts > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            while (board[row][col] == EMPTY) {
                row = random.nextInt(SIZE);
                col = random.nextInt(SIZE);
            }
            byte backup = board[row][col];
            board[row][col] = EMPTY;

            byte[][] boardCopy = copyBoard();
            if (!hasUniqueSolution(boardCopy)) {
                board[row][col] = backup;
                attempts--;
            }
        }
        // В зависимости от сложности надо добавить несколько дополнительных цифр на поле
        int extraNumbers = 0;
        if (PlayPane.getDifficulty().equals("Easy")) extraNumbers = 20;
        else if (PlayPane.getDifficulty().equals("Medium")) extraNumbers = 10;
        for (int n = 0; n < extraNumbers; ) {
            int randomRow = random.nextInt(0, 9);
            int randomCol = random.nextInt(0, 9);
            if (board[randomRow][randomCol] == EMPTY) {
                board[randomRow][randomCol] = (byte) SudokuPane.getSudokuSolved()[randomRow][randomCol];
                n++;
            }
        }
        return board;
    }
    private static boolean isValid(int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        int startRow = row / AREA_SIZE * AREA_SIZE;
        int startCol = col / AREA_SIZE * AREA_SIZE;
        for (int i = 0; i < AREA_SIZE; i++) {
            for (int j = 0; j < AREA_SIZE; j++) {
                if (board[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isValid(byte[][] board, int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        int startRow = row / AREA_SIZE * AREA_SIZE;
        int startCol = col / AREA_SIZE * AREA_SIZE;
        for (int i = 0; i < AREA_SIZE; i++) {
            for (int j = 0; j < AREA_SIZE; j++) {
                if (board[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[] findEmptyLocation() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
    private static int[] findEmptyLocation(byte[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private static boolean hasUniqueSolution(byte[][] boardCopy) {
        int[] count = {0};
        solveAndCount(boardCopy, count);
        return count[0] == 1;
    }
    private static void solveAndCount(byte[][] boardCopy, int[] count) {
        int[] empty = findEmptyLocation(boardCopy);
        if (empty == null) {
            count[0]++;
            return;
        }
        int row = empty[0];
        int col = empty[1];

        for (int num = 1; num <= SIZE; num++) {
            if (isValid(boardCopy, row, col, num)) {
                boardCopy[row][col] = (byte) num;
                solveAndCount(boardCopy, count);
                boardCopy[row][col] = EMPTY;
                if (count[0] > 1) {
                    return;
                }
            }
        }
    }

    private static byte[][] copyBoard() {
        byte[][] boardCopy = new byte[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, boardCopy[i], 0, SIZE);
        }
        return boardCopy;
    }
}

