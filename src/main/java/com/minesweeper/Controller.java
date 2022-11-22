package com.minesweeper;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//Controls bulk of program
public class Controller {
    private int width;
    private int height;
    private int clicks;
    private int flagsPlaced;
    private int timeElapsed;
    private Timer timer;
    private boolean running;
    private PauseTransition pause;
    private Image revealed;
    private Image unrevealed;
    private Image mine;
    private Image flag;
    private ImageView[] unrevealedImage;
    private ImageView[] mineImage;
    private ImageView[] flagImage;
    private Label[] numberMines;
    private Label winOrLose;
    private Group boardGroup;
    private Scene scene;
    private Stage stage;
    private final int UNITS = 30;
    private final int MINES = 42;

    public Controller(String r, String u, String m, String f, Stage s) {
        width = 18;
        height = 14;
        clicks = 0;
        flagsPlaced = 0;
        timeElapsed = 0;

        pause = new PauseTransition();
        pause.setDuration(new Duration(750));

        revealed = new Image(r);
        unrevealed = new Image(u);
        mine = new Image(m);
        flag = new Image(f);

        boardGroup = new Group();
        scene = new Scene(boardGroup);

        stage = s;
        stage.setResizable(false);
        stage.setTitle("MINESWEEPER  |  " + MINES + " FLAGS  |  0");
        stage.getIcons().add(flag);
        stage.setScene(scene);

        numberMines = new Label[height * width];
        for (int i = 0; i < numberMines.length; i++) {
            numberMines[i] = new Label();
            numberMines[i].setFont(Font.font("", FontWeight.BOLD, 18));
            numberMines[i].setPrefSize(UNITS, UNITS);
            numberMines[i].setAlignment(Pos.CENTER);
        }

        winOrLose = new Label();
        winOrLose.setVisible(false);

        mineImage = new ImageView[MINES];
        for (int i = 0; i < mineImage.length; i++) {
            mineImage[i] = new ImageView(mine);
            mineImage[i].setFitWidth(UNITS);
            mineImage[i].setFitHeight(UNITS);
        }

        unrevealedImage = new ImageView[height * width];
        for (int i = 0; i < unrevealedImage.length; i++) {
            unrevealedImage[i] = new ImageView(unrevealed);
            unrevealedImage[i].setFitWidth(UNITS);
            unrevealedImage[i].setFitHeight(UNITS);
        }

        flagImage = new ImageView[height * width];
        for (int i = 0; i < flagImage.length; i++) {
            flagImage[i] = new ImageView(flag);
            flagImage[i].setFitWidth(UNITS);
            flagImage[i].setFitHeight(UNITS);
            flagImage[i].setVisible(false);
            int x = i;

        }

    }

    //Board Setup
    private void drawBoard() {
        Random rand = new Random();
        int counter = 0;

        //Revealed Image
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ImageView revealedImage = new ImageView(revealed);
                revealedImage.setFitWidth(UNITS);
                revealedImage.setFitHeight(UNITS);
                revealedImage.setX(j * UNITS);
                revealedImage.setY(i * UNITS);
                boardGroup.getChildren().add(revealedImage);
            }
        }

        mineGeneration();

        numberGeneration();

        for (int i = 0; i < mineImage.length; i++)
            boardGroup.getChildren().add(mineImage[i]);

        counter = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                //Unrevealed Image
                unrevealedImage[counter].setX(j * UNITS);
                unrevealedImage[counter].setY((i * UNITS));
                boardGroup.getChildren().add(unrevealedImage[counter]);

                //Flag Image
                flagImage[counter].setX(j * UNITS);
                flagImage[counter].setY((i * UNITS));
                boardGroup.getChildren().add(flagImage[counter]);

                counter++;
            }
        }

    }

    private void mineGeneration() {
        Random rand = new Random();
        int counter = 0;
        for (int i = 0; i < height; i++) {
            int x = rand.nextInt(width - 4) + 2;
            int y = i;
            for (int j = 0; j < 3; j++) {
                if (counter < mineImage.length) {
                    if (j == 0) {
                        mineImage[counter].setX(x * UNITS);
                        mineImage[counter].setY(y * UNITS);
                        System.out.println("Mine " + (counter + 1) + ": (" + mineImage[counter].getX()/UNITS + ", " + mineImage[counter].getY()/UNITS + ")");
                    } else if (j == 1) {
                        int xTemp = rand.nextInt(x);
                        mineImage[counter].setX(xTemp * UNITS);
                        mineImage[counter].setY(y * UNITS);
                        System.out.println("Mine " + (counter + 1) + ": (" + mineImage[counter].getX()/UNITS + ", " + mineImage[counter].getY()/UNITS + ")");
                    } else {
                        int xTemp = rand.nextInt((width-1) - x) + (x + 1);
                        mineImage[counter].setX(xTemp * UNITS);
                        mineImage[counter].setY(y * UNITS);
                        System.out.println("Mine " + (counter + 1) + ": (" + mineImage[counter].getX()/UNITS + ", " + mineImage[counter].getY()/UNITS + ")");
                    }
                }
                counter++;
            }
        }
    }

    private void numberGeneration() {
        int counter = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int x = j * UNITS, y = i * UNITS;
                int minesNear = 0;
                for (int k = 0; k < mineImage.length; k++) {
                    //Surface
                    if (x + UNITS == mineImage[k].getX() && y == mineImage[k].getY())
                        minesNear++;
                    if (x - UNITS == mineImage[k].getX() && y == mineImage[k].getY())
                        minesNear++;
                    if (y + UNITS == mineImage[k].getY() && x == mineImage[k].getX())
                        minesNear++;
                    if (y - UNITS == mineImage[k].getY() && x == mineImage[k].getX())
                        minesNear++;
                    //Corners
                    if (x + UNITS == mineImage[k].getX() && y - UNITS == mineImage[k].getY())
                        minesNear++;
                    if (x + UNITS == mineImage[k].getX() && y + UNITS == mineImage[k].getY())
                        minesNear++;
                    if (x - UNITS == mineImage[k].getX() && y - UNITS == mineImage[k].getY())
                        minesNear++;
                    if (x - UNITS == mineImage[k].getX() && y + UNITS == mineImage[k].getY())
                        minesNear++;
                }
                switch (minesNear) {
                    case -1:
                        numberMines[counter].setVisible(false);
                    case 0:
                        numberMines[counter].setVisible(false);
                        break;
                    case 1:
                        numberMines[counter].setTextFill(Color.color(0, 0, 1));
                        break;
                    case 2:
                        numberMines[counter].setTextFill(Color.color(0,.67,0));
                        break;
                    case 3:
                        numberMines[counter].setTextFill(Color.color(1,0,0));
                        break;
                    case 4:
                        numberMines[counter].setTextFill(Color.color(0,0,.53));
                        break;
                    case 5:
                        numberMines[counter].setTextFill(Color.color(.53,0,0));
                        break;
                    case 6:
                        numberMines[counter].setTextFill(Color.color(0,.67,.67));
                        break;
                    case 7:
                        numberMines[counter].setTextFill(Color.color(.53,.53,.53));
                        break;
                    case 8:
                        numberMines[counter].setTextFill(Color.color(0,0,0));
                        break;
                }
                numberMines[counter].setText(String.valueOf(minesNear));
                numberMines[counter].setLayoutX(x);
                numberMines[counter].setLayoutY(y);
                boardGroup.getChildren().add(numberMines[counter]);
                counter++;
            }
        }
    }

    //Click Logic
    private void setClick() {
        for (int i = 0; i < (height*width); i++) {
            int x = i;
            flagImage[i].setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    flagImage[x].setVisible(false);
                    flagsPlaced--;
                    stage.setTitle("MINESWEEPER  |  " + (MINES - flagsPlaced) + " FLAGS  |  " + timeElapsed);
                }
            });
            unrevealedImage[i].setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if (clicks <= 0) {
                        firstClick(x);
                    }
                    else {
                        unrevealedImage[x].setVisible(false);
                        checkMines();
                    }
                    clicks++;
                }
                else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    if (flagsPlaced < MINES) {
                        flagImage[x].setVisible(true);
                        flagsPlaced++;
                        stage.setTitle("MINESWEEPER  |  " + (MINES - flagsPlaced) + " FLAGS  |  " + timeElapsed);
                        if (flagsPlaced == MINES) {
                            checkFlags();
                        }
                    }
                }
            });
        }
    }

    private void firstClick (int x) {
        try {
            for (int k = 0; k < 2; k++) {
                int xp1 = 0, xm1 = 0, yp1 = 0, ym1 = 0, tl = 0, tr = 0, bl = 0, br = 0;
                for (int j = 0; j < mineImage.length; j++) {
                    if (unrevealedImage[x + k].getX() == mineImage[j].getX() &&
                            unrevealedImage[x + k].getY() == mineImage[j].getY()) {
                        xp1++;
                        if (k == 0)
                            throw new ArrayIndexOutOfBoundsException();
                    }
                    if (unrevealedImage[x - k].getX() == mineImage[j].getX() &&
                            unrevealedImage[x - k].getY() == mineImage[j].getY())
                        xm1++;
                    if (unrevealedImage[x + k * width].getX() == mineImage[j].getX() &&
                            unrevealedImage[x + k * width].getY() == mineImage[j].getY())
                        yp1++;
                    if (unrevealedImage[x - k * width].getX() == mineImage[j].getX() &&
                            unrevealedImage[x - k * width].getY() == mineImage[j].getY())
                        ym1++;
                    if (unrevealedImage[x - k * (width + 1)].getX() == mineImage[j].getX() &&
                            unrevealedImage[x - k * (width + 1)].getY() == mineImage[j].getY())
                        tl++;
                    if (unrevealedImage[x - k * (width - 1)].getX() == mineImage[j].getX() &&
                            unrevealedImage[x - k * (width - 1)].getY() == mineImage[j].getY())
                        tr++;
                    if (unrevealedImage[x + k * (width - 1)].getX() == mineImage[j].getX() &&
                            unrevealedImage[x + k * (width - 1)].getY() == mineImage[j].getY())
                        bl++;
                    if (unrevealedImage[x + k * (width + 1)].getX() == mineImage[j].getX() &&
                            unrevealedImage[x + k * (width + 1)].getY() == mineImage[j].getY())
                        br++;
                }
                if (xp1 == 0)
                    unrevealedImage[x + k].setVisible(false);
                if (xm1 == 0)
                    unrevealedImage[x - k].setVisible(false);
                if (yp1 == 0)
                    unrevealedImage[x + k * width].setVisible(false);
                if (ym1 == 0)
                    unrevealedImage[x - k * width].setVisible(false);
                if (tl == 0)
                    unrevealedImage[x - k * (width + 1)].setVisible(false);
                if (tr == 0)
                    unrevealedImage[x - k * (width - 1)].setVisible(false);
                if (bl == 0)
                    unrevealedImage[x + k * (width - 1)].setVisible(false);
                if (br == 0)
                    unrevealedImage[x + k * (width + 1)].setVisible(false);
            }
            startTimer();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            unrevealedImage[x].setVisible(true);
            clicks--;
        }
    }

    private void checkFlags() {
        int correctlyPlaced = 0;

        int counter = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int k = 0; k < mineImage.length; k++) {
                    if (flagImage[counter].isVisible() &&
                            flagImage[counter].getX() == mineImage[k].getX() &&
                            flagImage[counter].getY() == mineImage[k].getY()){
                        correctlyPlaced++;
                    }
                }
                counter++;
            }
        }

        if (correctlyPlaced == MINES) {
            endGameLogic("You Win");
        }
    }

    private void checkMines () {
        boolean mineClick = false;

        int counter = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int k = 0; k < mineImage.length; k++) {
                    if (!flagImage[counter].isVisible() &&
                            !unrevealedImage[counter].isVisible() &&
                            unrevealedImage[counter].getX() == mineImage[k].getX() &&
                            unrevealedImage[counter].getY() == mineImage[k].getY()) {
                        mineClick = true;
                    }
                }
                counter++;
            }
        }

        if (mineClick) {
            endGameLogic("Game Over");
        }
    }

    //Timer Logic
    private void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    running = true;
                    timeElapsed++;
                    stage.setTitle("MINESWEEPER  |  " + (MINES - flagsPlaced) + " FLAGS  |  " + timeElapsed);
                });
            }
        };

        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private void cancelTimer() {
        timer.cancel();
        running = false;
    }

    //Exit
    private void endGameLogic (String s) {
        for (int i = 0; i < height*width; i++) {
            unrevealedImage[i].setOnMouseClicked(mouseEvent -> {});
        }
        if (running)
            cancelTimer();
        pause.play();
        pause.setOnFinished(actionEvent -> {
            gameOverScreen(s);
        });
    }

    private void gameOverScreen(String s) {
        winOrLose.setText(s);
        winOrLose.setVisible(true);
        winOrLose.setAlignment(Pos.CENTER);
        winOrLose.setFont(Font.font(50));
        winOrLose.setTextFill(Color.WHITE);
        winOrLose.setBackground(new Background(new BackgroundFill(Color.color(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        winOrLose.setPrefSize(width*UNITS, height*UNITS);
        winOrLose.setOpacity(.75);
        boardGroup.getChildren().add(winOrLose);

        stage.setScene(scene);

        if (running)
            cancelTimer();
    }

    public void exit() {
        if (running)
            cancelTimer();
        stage.close();
    }
    private void setManualExit() {
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getText().equals("x")) {
                exit();
            }
        });
        stage.setOnCloseRequest(windowEvent -> {
            exit();
        });
    }

    public void build() {
        setClick();
        drawBoard();
        setManualExit();
        System.gc();

        stage.show();
    }
}
