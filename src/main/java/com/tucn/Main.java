package com.tucn;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("application-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 670, 584);
        stage.setTitle("Queue management simulator");
        stage.getIcons().add(new Image("file:" + Paths.get("task.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}