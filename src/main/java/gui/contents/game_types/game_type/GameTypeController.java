package gui.contents.game_types.game_type;

import db.tables.game_type.DBGameTypeItem;
import folders.GameTypeImagesFolder;
import javafx.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameTypeController implements Initializable, FXMLController {
    private DBGameTypeItem dbGameTypeItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private Label lbTitle;

    public GameTypeController(DBGameTypeItem dbGameTypeItem) {
        this.dbGameTypeItem = dbGameTypeItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameTypeImagesFolder gtif = new GameTypeImagesFolder();
        File file = gtif.findFile(dbGameTypeItem.getImageName().getValue());
        if (!file.exists()) {
            file = gtif.getDefaultFile();
        }

        try (FileInputStream fis = new FileInputStream(file);) {
            ivAvatar.setImage(new Image(fis));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lbTitle.setText(dbGameTypeItem.getTitle().getValue());
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameType.fxml"));
        loader.setController(this);

        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
}
