import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SudokuSolverApp extends Application {

    private int[][] sudokuBoard = new int[9][9];
    private TextField[][] textFieldArray = new TextField[9][9];

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sudoku Solver");
        showWelcomeAnimation(primaryStage);
    }

    private void showWelcomeAnimation(Stage primaryStage) {
        GridPane welcomeGrid = new GridPane();
        welcomeGrid.setAlignment(Pos.CENTER);
        welcomeGrid.setHgap(10);
        welcomeGrid.setVgap(10);

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Welcome to Sudoku Solver");
        nameDialog.setHeaderText("Please enter your name:");
        nameDialog.setContentText("Name:");

        // Show the dialog and get the user's name
        String enteredName = null;
        Optional<String> result = nameDialog.showAndWait();
        if (result.isPresent()) {
            enteredName = result.get();
        }

        Label nameLabel = new Label("Hello, " + enteredName + "!");
        welcomeGrid.add(nameLabel, 0, 0);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), welcomeGrid);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        Scene welcomeScene = new Scene(welcomeGrid, 300, 200);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();

        // Set up a button to transition to the Sudoku app
        Button startButton = new Button("Start Sudoku");
        startButton.setOnAction(e -> {
            fadeTransition.setRate(-1);
            fadeTransition.play();
            fadeTransition.setOnFinished(event -> {
                // After the fade-out animation, switch to the Sudoku app
                setUpSudokuApp(primaryStage);
            });
        });
        welcomeGrid.add(startButton, 0, 1);
    }

    private void setUpSudokuApp(Stage primaryStage) {
        GridPane gridPane = createGrid();
        setUpTextFields(gridPane);

        Button solveButton = createSolveButton();
        gridPane.add(solveButton, 1, 10, 9, 1);

        solveButton.setOnAction(e -> solveSudoku());

        primaryStage.setScene(new Scene(gridPane, 300, 350));
        primaryStage.show();
    }

    private GridPane createGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        return gridPane;
    }

    private void setUpTextFields(GridPane gridPane) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField textField = new TextField();
                textField.setPrefWidth(30);
                textField.setPrefHeight(30);
                textField.setAlignment(Pos.CENTER);
                textFieldArray[i][j] = textField;
                gridPane.add(textField, j, i);
            }
        }
    }

    private Button createSolveButton() {
        Button solveButton = new Button("Solve Sudoku");
        solveButton.setPrefHeight(30);
        solveButton.setPrefWidth(200);
        return solveButton;
    }

    private void solveSudoku() {
        // Populate the sudokuBoard array with user input
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    String input = textFieldArray[i][j].getText();
                    sudokuBoard[i][j] = Integer.parseInt(input);
                } catch (NumberFormatException | NullPointerException e) {
                    // Handle invalid input (non-numeric or empty)
                    sudokuBoard[i][j] = 0;
                }
            }
        }

        // Call the Sudoku solver function
        if (solveSudoku(sudokuBoard, 0, 0)) {
            // Display the solved Sudoku in the text fields
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    textFieldArray[i][j].setText(String.valueOf(sudokuBoard[i][j]));
                }
            }
        } else {
            System.out.println("No solution exists.");
        }
    }

    private boolean solveSudoku(int[][] board, int row, int col) {
        // Find the next empty cell
        int[] emptyLocation = findEmptyLocation(board);
        if (emptyLocation == null) {
            // No empty cell is found; the puzzle is solved
            return true;
        }

        row = emptyLocation[0];
        col = emptyLocation[1];

        // Try placing numbers 1 through 9 in the empty cell
        for (int num = 1; num <= 9; num++) {
            if (isValid(board, row, col, num)) {
                // Make the choice
                board[row][col] = num;

                // Recursively try to solve the rest of the Sudoku
                if (solveSudoku(board, row, col)) {
                    return true;
                }

                // If the current choice does not lead to a solution, undo the choice
                board[row][col] = 0;
            }
        }

        // No number can be placed in the current cell
        return false;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        // Check if the number can be placed in the given row and column
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num
                    || board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3] == num) {
                return false;
            }
        }
        return true;
    }

    private int[] findEmptyLocation(int[][] board) {
        // Find an empty cell in the board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return new int[] { i, j };
                }
            }
        }
        return null;
    }
}