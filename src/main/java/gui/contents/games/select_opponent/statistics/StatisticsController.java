package gui.contents.games.select_opponent.statistics;

import db.DBConnect;
import db.DBProcedures;
import db.tables.opponent.DBOpponentItem;
import folders.OpponentImagesFolder;
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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable, FXMLController {

    private DBOpponentItem dbOpponentItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private Label lbName;
    @FXML
    private Label lbNumberOfGames;
    @FXML
    private Label lbNumberOfWins;
    @FXML
    private Label lbNumberOfDraws;
    @FXML
    private Label lbNumberOfLoses;

    public StatisticsController(DBOpponentItem dbOpponentItem) {
        this.dbOpponentItem = dbOpponentItem;
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

        int numberOfGames = 0;
        int numberOfWins = 0;
        int numberOfDraws = 0;
        int numberOfLoses = 0;
        try (Connection connection = DBConnect.getConnection();){
            String query = DBProcedures.GetOpponentStats.getQuery();
            CallableStatement cs = connection.prepareCall(query);
            cs.setObject(1, dbOpponentItem.getIdOpponent().getValue());

            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                numberOfGames = rs.getInt(1);
                numberOfWins = rs.getInt(2);
                numberOfDraws = rs.getInt(3);
                numberOfLoses = rs.getInt(4);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String statisticPattern = "%s %s";
        Label lb = lbNumberOfGames;
        lb.setText(String.format(statisticPattern, lb.getText(), numberOfGames));

        lb = lbNumberOfWins;
        lb.setText(String.format(statisticPattern, lb.getText(), numberOfWins));

        lb = lbNumberOfDraws;
        lb.setText(String.format(statisticPattern, lb.getText(), numberOfDraws));

        lb = lbNumberOfLoses;
        lb.setText(String.format(statisticPattern, lb.getText(), numberOfLoses));
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Statistics.fxml"));
        loader.setController(this);

        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
}
