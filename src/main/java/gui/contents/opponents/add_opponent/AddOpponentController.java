package gui.contents.opponents.add_opponent;

import db.DBConnect;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import folders.OpponentImagesFolder;
import javafx.FXMLController;
import javafx.Stageable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.CustomAlerts;
import utils.StringGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddOpponentController implements Initializable, FXMLController, Stageable {
    private DBOpponentItem dbOpponentItem;
    private File avatarImgFile;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private Button btnChangeAvatar;
    @FXML
    private TextField tfName;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

    public AddOpponentController(DBOpponentItem dbOpponentItem) {
        this.dbOpponentItem = dbOpponentItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        OpponentImagesFolder oif = new OpponentImagesFolder();
        File file = oif.findFile(dbOpponentItem.getImageName().getValue());
        if (!file.exists()) {
            file = oif.getDefaultFile();
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            ivAvatar.setImage(new Image(fis));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (dbOpponentItem.getIdOpponent().getValue() != null) {
            tfName.setText(dbOpponentItem.getName().getValue());
        }

        btnCancel.setOnAction(this::btnCancel_click);
        btnChangeAvatar.setOnAction(this::btnChangeAvatar_click);
        btnSave.setOnAction(this::btnSave_click);
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddOpponent.fxml"));
        loader.setController(this);
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    @Override
    public Stage getStage() {
        Stage stage = new Stage();
        stage.setTitle(dbOpponentItem.getIdOpponent().getValue() == null ? "Добавить оппонента" : "Изменить оппонента");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        try {
            Scene sc = new Scene(getLoader().load());
            stage.setScene(sc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stage;
    }

    private void btnChangeAvatar_click(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        avatarImgFile = fc.showOpenDialog(mainPane.getScene().getWindow());
        if (avatarImgFile != null && avatarImgFile.exists()) {
            try(FileInputStream fis = new FileInputStream(avatarImgFile)) {
                ivAvatar.setImage(new Image(fis));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void btnCancel_click(ActionEvent actionEvent) {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    private void btnSave_click(ActionEvent actionEvent) {
        int errorItemIndex = 0;
        String errorItemPattern = "%d. %s\n";
        StringBuilder errorItems = new StringBuilder();

        final int MIN_NAME_LENGTH = 3;
        if (tfName.getText().length() < MIN_NAME_LENGTH) {
            errorItems.append(String.format(errorItemPattern, ++errorItemIndex,
                    String.format("Имя должно быть не менее %d символов", MIN_NAME_LENGTH)));
        }

        if (tfName.getText().contains(" ")) {
            errorItems.append(String.format(errorItemPattern, ++errorItemIndex, "Имя не должно содержать пробелов"));
        }

        if (errorItemIndex == 0) {
            try (Connection connection = DBConnect.getConnection();){
                DBOpponent table = new DBOpponent();
                ResultSet rs = table.selectWithOneParameter(connection, table.getName(), tfName.getText());
                if (rs.next()) {
                    DBOpponentItem item = new DBOpponentItem(rs);
                    if (!dbOpponentItem.equals(item))
                        errorItems.append(String.format(errorItemPattern, ++errorItemIndex, "Введённое имя уже используется"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (errorItemIndex > 0) {
            CustomAlerts.showWarningAlert(errorItems);
        } else {
            if (avatarImgFile != null && avatarImgFile.exists()) {
                StringGenerator sg = new StringGenerator();
                String fileExtension =  avatarImgFile.getName().substring(avatarImgFile.getName().lastIndexOf(".") + 1);
                String newFileName = String.format("%s.%s", sg.generate(15), fileExtension);

                OpponentImagesFolder oif = new OpponentImagesFolder();
                oif.delete(dbOpponentItem.getImageName().getValue());
                oif.add(avatarImgFile.getPath(), newFileName);
                dbOpponentItem.getImageName().setValue(newFileName);
            }
            dbOpponentItem.getName().setValue(tfName.getText());

            if (dbOpponentItem.getIdOpponent().getValue() == null) {
                try (Connection connection = DBConnect.getConnection();) {
                    DBOpponent table = new DBOpponent();
                    table.insert(connection, dbOpponentItem);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (Connection connection = DBConnect.getConnection();) {
                    DBOpponent table = new DBOpponent();
                    table.update(connection, dbOpponentItem);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            btnCancel_click(null);
        }
    }
}
