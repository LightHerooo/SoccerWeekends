package gui.contents.games.select_opponent.statistics;

import db.DBConnect;
import db.DBFunctions;
import db.tables.opponent.DBOpponentItem;
import javafx.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import folders.OpponentImagesFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
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
        int numberOfLoses = 0;
        String statisticPattern = "%s %s";
        try (Connection connection = DBConnect.getConnection();){
            String query = DBFunctions.GetNumberOfOpponentGames.getName();
            CallableStatement cs = connection.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, dbOpponentItem.getIdOpponent().getValue());
            cs.execute();
            numberOfGames = cs.getInt(1);

            query = DBFunctions.GetNumberOfOpponentWins.getName();
            cs = connection.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, dbOpponentItem.getIdOpponent().getValue());
            cs.execute();
            numberOfWins = cs.getInt(1);

            numberOfLoses = numberOfGames - numberOfWins;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Label lb = lbNumberOfGames;
        lb.setText(String.format(statisticPattern, lb.getText(), numberOfGames));

        lb = lbNumberOfWins;
        lb.setText(String.format(statisticPattern, lb.getText(), numberOfWins));

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
