package gui.versus.games.game.types.big_game;

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
import utils.OpponentImagesFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BigGameOpponentController implements Initializable, FXMLController {
    private DBOpponentItem dbOpponentItem;
    private int place;
    private int score;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label lbPlace;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private Label lbScore;
    @FXML
    private Label lbName;

    public BigGameOpponentController(DBOpponentItem dbOpponentItem, int place, int score) {
        this.dbOpponentItem = dbOpponentItem;
        this.place = place;
        this.score = score;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        OpponentImagesFolder oif = new OpponentImagesFolder();
        File imgFile = oif.findImageFile(dbOpponentItem.getNameImage().getValue());
        try (FileInputStream fis = new FileInputStream(imgFile)) {
            Image img = new Image(fis);
            ivAvatar.setImage(img);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lbPlace.setText(String.format("[%d]", place));
        lbName.setText(dbOpponentItem.getName().getValue());
        lbScore.setText(Integer.toString(score));
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BigGameOpponent.fxml"));
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
