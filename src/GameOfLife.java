import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GameOfLife extends Application {

    private static int width = 500;
    private static int height = 500;
    private static int cellSize = 10;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        Scene scene = new Scene(root, width, height + 100);
        final Canvas canvas = new Canvas(width, height);

        Button resetBtn = new Button("Reset");
        Button nextGenBtn = new Button("Next Generation");
        Button startBtn = new Button("Start");
        Button stopBtn = new Button("Stop");

        root.getChildren().addAll(canvas, new HBox(10, resetBtn, nextGenBtn, startBtn, stopBtn));
        primaryStage.setScene(scene);
        primaryStage.show();

        int rows = (int) Math.floor(height / cellSize);
        int cols = (int) Math.floor(width / cellSize);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        Board board = new Board(rows, cols, graphicsContext);
        board.createBoard();

        AnimationTimer runAnimation = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if ((now - lastUpdate) >= TimeUnit.MILLISECONDS.toNanos(100)) {
                    board.generateNextGeneration();
                    lastUpdate = now;
                }
            }
        };
        resetBtn.setOnAction(event -> board.createBoard());
        startBtn.setOnAction(event -> runAnimation.start());
        nextGenBtn.setOnAction(event -> board.generateNextGeneration());
        stopBtn.setOnAction(event -> runAnimation.stop());
    }

    private static class Board {
        private final int rows;
        private final int cols;
        private int[][] grid;
        private Random random = new Random();
        private final GraphicsContext graphics;

        public Board(int rows, int cols, GraphicsContext graphics) {
            this.rows = rows;
            this.cols = cols;
            this.graphics = graphics;
            grid = new int[rows][cols];
        }

        public void createBoard() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    grid[i][j] = random.nextInt(2);
                }
            }
            drawBoard();
        }

        private void drawBoard() {
            graphics.setFill(Color.GREEN);
            graphics.fillRect(0, 0, width, height);

            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j] == 1) {
                        graphics.setFill(Color.BLACK);
                        graphics.fillRect((i * cellSize) , (j * cellSize) , cellSize , cellSize );
                    }else {
                        graphics.setFill(Color.WHITE);
                        graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                    }
                }
            }
        }

        public void generateNextGeneration() {
            int[][] next = new int[rows][cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    int neighbors = countAliveNeighbors(i, j);

                    if (neighbors == 3) {
                        next[i][j] = 1;
                    }else if (neighbors < 2 || neighbors > 3) {
                        next[i][j] = 0;
                    }else {
                        next[i][j] = grid[i][j];
                    }
                }
            }
            grid = next;
            drawBoard();
        }

        private int countAliveNeighbors(int i, int j) {
            int count = 0;
            int iStart = i == 0 ? 0 : -1;
            int iEnd = i == grid.length - 1 ? 0 : 1;
            int jStart = j == 0 ? 0 : -1;
            int jEnd = j == grid.length - 1 ? 0 : 1;

            for (int k = iStart; k <= iEnd; k++) {
                for (int l = jStart; l <= jEnd; l++) {
                    count += grid[i + k][l + j];
                }
            }

            count -= grid[i][j];

            return count;
        }
    }

}