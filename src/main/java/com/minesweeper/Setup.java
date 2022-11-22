package com.minesweeper;

import javafx.application.Application;
import javafx.stage.Stage;

public class Setup extends Application{
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        String revealed = "revealed.jpg", unrevealed = "unrevealed.jpg", flag = "flag.jpg", mine = "mine.jpg";

        Controller controller = new Controller(revealed, unrevealed, mine, flag, stage);
        controller.build();
    }
}
