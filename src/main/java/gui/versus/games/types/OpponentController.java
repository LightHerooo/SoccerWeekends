package gui.versus.games.types;

import db.tables.opponent.DBOpponentItem;
import javafx.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class OpponentController implements Initializable, FXMLController {
    private DBOpponentItem DBOpponentItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private Label lbName;

    public OpponentController(DBOpponentItem DBOpponentItem) {
        this.DBOpponentItem = DBOpponentItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String imgPathPattern = "opponent_images/%s";
        String imgPath = String.format(imgPathPattern, DBOpponentItem.getNameImage().getValue());
        URL resource = getClass().getClassLoader().getResource(imgPath);
        if (resource == null) {
            imgPath = String.format(imgPathPattern, "unknown.png");
            resource = getClass().getClassLoader().getResource(imgPath);
        }

        try {
            File f = new File(resource.toURI());
            try (FileInputStream fis = new FileInputStream(f)) {
                Image img = new Image(fis);
                ivAvatar.setImage(img);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        lbName.setText(DBOpponentItem.getName().getValue());
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Opponent.fxml"));
        loader.setController(this);
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    public void setBorderColorInImage(Color color) {
        Pane p = (Pane)ivAvatar.getParent();
        p.setBackground(Background.fill(color));
    }
}
