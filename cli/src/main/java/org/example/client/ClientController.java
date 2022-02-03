package org.example.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.FileMessage;
import org.example.model.FileRequest;
import org.example.model.FilesList;
import org.example.model.StringCommand;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    public ListView<String> clientView;
    public ListView<String> serverView;
    public Label clientLabel;
    public Label serverLabel;
    public Button butUpload;
    public Button butDownload;
    public Button butReg;
    public Button butAuth;
    public TextField dirField;
    public Button butDir;
    public Button upButton;
    public Button delBut;
    public Button renameBut;
    public TextField renameDir;
    private Path currentDir;
    private Network network;
    private Stage stage;

    private void fillCurrentDirFiles() {
        Platform.runLater(() -> {
            clientView.getItems().clear();
            clientView.getItems().add("..");
            clientView.getItems().addAll(currentDir.toFile().list());
            clientView.getItems().sorted();
            clientLabel.setText(getClientFilesDetails());
        });
    }

    private String getClientFilesDetails() {
        File[] files = currentDir.toFile().listFiles();
        long size = 0;
        String label;
        if (files != null) {
            label = files.length + " files in current dir.\n";
            for (File file : files) {
                size += files.length;
            }
            label += "Summary size: " + size + " bytes.";
        } else {
            label = "Current dir. is empty";
        }
        return label;
    }

    private void initClickListener() {
        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String fileName = clientView.getSelectionModel().getSelectedItem();
                System.out.println("chosen " + fileName);
                Path path = currentDir.resolve(fileName);
                if (Files.isDirectory(path)) {
                    currentDir = path;
                    fillCurrentDirFiles();
                }
            }
        });
        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String fileName = serverView.getSelectionModel().getSelectedItem();
                try {
                    network.write(new StringCommand("/goTo " + fileName));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            currentDir = Paths.get(System.getProperty("user.home")); /*домашний каталог*/
            fillCurrentDirFiles();
            initClickListener();

            network = Network.getInstance(message -> {
                switch (message.getType()) {
                    case FILE_MESSAGE:
                        FileMessage fileMessage = (FileMessage) message;
                        Files.write(currentDir.resolve(fileMessage.getFileName()), fileMessage.getBytes());
                        fillCurrentDirFiles();
                        break;
                    case LIST:
                        FilesList list = (FilesList) message;
                        updateServerView(list.getList());
                        break;
                    case STRING:
                        StringCommand stringCommand = (StringCommand) message;
                        if (stringCommand.getCommand().equals("/authOk")) {
                            Platform.runLater(() -> {
                                butDownload.setVisible(true);
                                butDownload.setManaged(true);
                                butUpload.setVisible(true);
                                butUpload.setManaged(true);
                                serverView.setVisible(true);
                                serverView.setManaged(true);
                                clientView.setVisible(true);
                                clientView.setManaged(true);
                                serverLabel.setVisible(true);
                                serverLabel.setManaged(true);
                                clientLabel.setVisible(true);
                                clientLabel.setManaged(true);
                                butAuth.setVisible(false);
                                butAuth.setManaged(false);
                                butReg.setVisible(false);
                                butReg.setManaged(false);
                                butDir.setVisible(true);
                                butDir.setManaged(true);
                                dirField.setVisible(true);
                                dirField.setManaged(true);
                                upButton.setVisible(true);
                                upButton.setManaged(true);
                                renameBut.setVisible(true);
                                renameBut.setManaged(true);
                                delBut.setVisible(true);
                                delBut.setManaged(true);
                                renameDir.setVisible(true);
                                renameDir.setManaged(true);
                            });
                        }
                        break;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        FileMessage fileMessage = new FileMessage(currentDir.resolve(fileName));
        network.write(fileMessage);
    }

    @FXML
    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        network.write(new FileRequest(fileName));
    }

    private void updateServerView(List<String> names) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(names);
        });
    }

    @FXML
    public void reg(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Paths.get("cli/src/main/resources/org.example.client/regClient.fxml").toUri().toURL());
            stage = new Stage();
            stage.setScene(new Scene(root, 350, 344));
            stage.setTitle("Try to auth");
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void auth(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Paths.get("cli/src/main/resources/org.example.client/authClient.fxml").toUri().toURL());
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

    @FXML
    public void butDir(ActionEvent actionEvent) throws IOException {
        network.write(new StringCommand("/dir " + dirField.getText()));
        dirField.clear();
    }

    @FXML
    public void upBut(ActionEvent actionEvent) throws IOException {
        network.write(new StringCommand("/up"));
    }

    @FXML
    public void delete(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        network.write(new StringCommand("/del " + fileName));
    }

    @FXML
    public void rename(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        network.write(new StringCommand("/rename " + fileName + " " + renameDir.getText()));
        renameDir.clear();
    }
}
