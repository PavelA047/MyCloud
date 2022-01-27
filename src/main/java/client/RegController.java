package client;

import common.StringCommand;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegController implements Initializable {

    public TextField name;
    public TextField surname;
    public TextField login;
    public TextField password;
    public TextField location;
    public TextField gender;
    private Network network;
    private Stage stage;

    //кнопка регистрации
    @FXML
    public void reg(ActionEvent actionEvent) throws IOException {
        String log = login.getText().trim();
        String pass = password.getText().trim();
        String firstName = name.getText().trim();
        String secondName = surname.getText().trim();
        String loc = location.getText().trim();
        String gen = gender.getText().trim();
        StringCommand command = new StringCommand(String.format("/reg %s %s %s %s %s %s",
                firstName, secondName, log, pass, loc, gen));
        network.write(command);
    }

    //кнопка авторизации
    @FXML
    public void auth(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("authClient.fxml"));
            Parent root = fxmlLoader.load();
            stage = new Stage();
            stage.setScene(new Scene(root, 280, 150));
            stage.setTitle("Try to auth");
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            network = Network.getInstance(message -> {
                switch (message.getType()) {
                    case STRING:
                        StringCommand stringCommand = (StringCommand) message;
                        if (stringCommand.getCommand().equals("/regOk")) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("authClient.fxml"));
                            Parent root = fxmlLoader.load();
                            stage = new Stage();
                            stage.setScene(new Scene(root, 280, 150));
                            stage.setTitle("Try to auth");
                            stage.initStyle(StageStyle.UTILITY);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.show();
                        }
                        if (stringCommand.getCommand().equals("/regNo")) {
                            break;
                        }
                        if (stringCommand.getCommand().equals("/authOk")) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("client.fxml"));
                            Parent root = fxmlLoader.load();
                            stage = new Stage();
                            stage.setScene(new Scene(root, 280, 150));
                            stage.setTitle("Your cloud storage");
                            stage.initStyle(StageStyle.UTILITY);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.show();
                        }
                        if (stringCommand.getCommand().equals("/authNo")) {
                            break;
                        }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
