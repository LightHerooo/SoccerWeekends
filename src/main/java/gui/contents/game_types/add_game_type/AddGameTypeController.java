package gui.contents.game_types.add_game_type;

import db.DBConnect;
import db.tables.game_type.DBGameType;
import db.tables.game_type.DBGameTypeItem;
import folders.GameTypeImagesFolder;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.CustomAlerts;
import utils.FileChoosers;
import utils.StringGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddGameTypeController implements Initializable, FXMLController, Stageable {
    private DBGameTypeItem dbGameTypeItem;
    private File pictureImgFile;

    @FXML
    public AnchorPane mainPane;
    @FXML
    private ImageView ivPicture;
    @FXML
    private Button btnChangePicture;
    @FXML
    private TextField tfTitle;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

    public AddGameTypeController(DBGameTypeItem dbGameTypeItem) {
        this.dbGameTypeItem = dbGameTypeItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameTypeImagesFolder gtif = new GameTypeImagesFolder();
        File file = gtif.findFile(dbGameTypeItem.getImageName().getValue());
        if (!file.exists()) {
            file = gtif.getDefaultFile();
        }

        try (FileInputStream fis = new FileInputStream(file);){
            ivPicture.setImage(new Image(fis));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (dbGameTypeItem.getIdGameType().getValue() != null) {
            tfTitle.setText(dbGameTypeItem.getTitle().getValue());
        }

        btnChangePicture.setOnAction(this::btnChangePicture_click);
        btnCancel.setOnAction(this::btnCancel_click);
        btnSave.setOnAction(this::btnSave_click);
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddGameType.fxml"));
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
        stage.setTitle(dbGameTypeItem.getIdGameType().getValue() == null ? "Добавить тип игры" : "Изменить тип игры");
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

    private void btnChangePicture_click(ActionEvent actionEvent) {
        pictureImgFile = FileChoosers.chooseImageFile(mainPane.getScene().getWindow());
        if (pictureImgFile != null && pictureImgFile.exists()) {
            try(FileInputStream fis = new FileInputStream(pictureImgFile)) {
                ivPicture.setImage(new Image(fis));
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

        if (tfTitle.getText().isEmpty()) {
            errorItems.append(String.format(errorItemPattern, ++errorItemIndex, "Название не может быть пустым"));
        }

        if (errorItemIndex == 0) {
            try (Connection connection = DBConnect.getConnection();){
                DBGameType table = new DBGameType();
                ResultSet rs = table.selectWithOneParameter(connection, table.getTitle(), tfTitle.getText());
                if (rs.next()) {
                    DBGameTypeItem item = new DBGameTypeItem(rs);
                    if (!dbGameTypeItem.equals(item))
                        errorItems.append(String.format(errorItemPattern, ++errorItemIndex, "Введённое название уже существует"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (errorItemIndex > 0) {
            CustomAlerts.showWarningAlert(errorItems);
        } else {
            if (pictureImgFile != null && pictureImgFile.exists()) {
                StringGenerator sg = new StringGenerator();
                String fileExtension =  pictureImgFile.getName().substring(pictureImgFile.getName().lastIndexOf(".") + 1);
                String newFileName = String.format("%s.%s", sg.generate(15), fileExtension);

                GameTypeImagesFolder gtif = new GameTypeImagesFolder();
                gtif.delete(dbGameTypeItem.getImageName().getValue());
                gtif.add(pictureImgFile.getPath(), newFileName);
                dbGameTypeItem.getImageName().setValue(newFileName);
            }
            dbGameTypeItem.getTitle().setValue(tfTitle.getText());

            if (dbGameTypeItem.getIdGameType().getValue() == null) {
                try (Connection connection = DBConnect.getConnection();) {
                    DBGameType table = new DBGameType();
                    table.insert(connection, dbGameTypeItem);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (Connection connection = DBConnect.getConnection();) {
                    DBGameType table = new DBGameType();
                    table.update(connection, dbGameTypeItem);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            btnCancel_click(null);
        }
    }
}
