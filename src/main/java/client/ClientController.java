package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import common.FileMessage;
import common.FileRequest;
import common.FilesList;

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
    private Path currentDir;
    private Network network;

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
}
