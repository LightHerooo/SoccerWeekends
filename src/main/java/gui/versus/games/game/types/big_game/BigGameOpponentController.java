package gui.versus.games.game.types.big_game;

import db.tables.opponent.DBOpponentItem;
import folders.OpponentImagesFolder;
import gui.versus.games.game.types.OpponentResult;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class BigGameOpponentController implements Initializable, FXMLController {
    private DBOpponentItem dbOpponentItem;
    private int score;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView ivPlace;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private Label lbScore;
    @FXML
    private Label lbName;

    public BigGameOpponentController(DBOpponentItem dbOpponentItem, int score) {
        this.dbOpponentItem = dbOpponentItem;
        this.score = score;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        OpponentImagesFolder oif = new OpponentImagesFolder();
        File imgFile = oif.findFile(dbOpponentItem.getImageName().getValue());
        if (!imgFile.exists()) {
            imgFile = oif.getDefaultFile();
        }

        try (FileInputStream fis = new FileInputStream(imgFile)) {
            Image img = new Image(fis);
            ivAvatar.setImage(img);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

    public void setPlaceImg(OpponentResult opponentResult) {
        String pathPattern = "images/opponent_result/%s";
        String fileName = null;
        switch (opponentResult) {
            case FIRST -> fileName = "first_place.png";
            case SECOND -> fileName = "second_place.png";
            case THIRD -> fileName = "third_place.png";
        }

        URL fileURL = getClass().getClassLoader().getResource(String.format(pathPattern, fileName));
        if (fileURL != null) {
            try {
                File file = new File(fileURL.toURI());
                try (FileInputStream fis = new FileInputStream(file)) {
                    ivPlace.setImage(new Image(fis));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
