package gui.contents.games.game.types.duel;

import db.tables.opponent.DBOpponentItem;
import folders.OpponentImagesFolder;
import gui.contents.games.game.types.OpponentResult;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx_utils.FXMLController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class DuelOpponentController implements Initializable, FXMLController {
    private DBOpponentItem dbOpponentItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView ivOpponentResult;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private Label lbName;

    public DuelOpponentController(DBOpponentItem dbOpponentItem) {
        this.dbOpponentItem = dbOpponentItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        OpponentImagesFolder oif = new OpponentImagesFolder();
        File imgFile = oif.findFile(dbOpponentItem.getImageName().getValue());
        if (imgFile.exists()) {
            try (FileInputStream fis = new FileInputStream(imgFile)) {
                Image img = new Image(fis);
                ivAvatar.setImage(img);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (InputStream is = oif.getDefaultResource()) {
                ivAvatar.setImage(new Image(is));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        lbName.setText(dbOpponentItem.getName().getValue());
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DuelOpponent.fxml"));
        loader.setController(this);
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    public void setIvOpponentResultImg(OpponentResult opponentResult) {
        String pathPattern = "/images/opponent_result/%s";
        String fileName = null;
        switch (opponentResult) {
            case WINNER -> fileName = "winner.png";
            case LOSER -> fileName = "loser.png";
            case DRAW -> fileName = "draw.png";
        }

        String path = String.format(pathPattern, fileName);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            ivOpponentResult.setImage(new Image(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
