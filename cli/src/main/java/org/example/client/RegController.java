package org.example.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.model.StringCommand;

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
    public Label labelReg;
    private Network network;

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
        labelReg.setText("Now, close this window and try to auth!");
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
