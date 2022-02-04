package org.example.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.model.StringCommand;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthClient implements Initializable {
    public TextField login;
    public PasswordField password;
    private Network network;

    @FXML
    public void auth(ActionEvent actionEvent) throws IOException {
        String log = login.getText().trim();
        String pass = password.getText().trim();
        StringCommand command = new StringCommand(String.format("/auth %s %s",
                log, pass));
        network.write(command);
        login.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            network = Network.getInstance(message -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}