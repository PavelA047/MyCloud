package org.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class ClientApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(Paths.get("cli/src/main/resources/org.example.client/client.fxml").toUri().toURL());
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}