package gui.contents.opponents.opponent;

import db.DBConnect;
import db.DBFunctions;
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
import java.sql.SQLException;
import java.sql.Types;
import java.util.ResourceBundle;

public class OpponentController implements Initializable, FXMLController {
    private DBOpponentItem dbOpponentItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private Label lbName;
    @FXML
    private Label lbID;
    @FXML
    private Label lbNumberOfGames;
    @FXML
    private Label lbNumberOfWins;
    @FXML
    private Label lbNumberOfLoses;

    public OpponentController(DBOpponentItem dbOpponentItem) {
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

        int opponentId = dbOpponentItem.getIdOpponent().getValue();
        int numberOfGames = 0;
        int numberOfWins = 0;
        int numberOfLoses = 0;
        try (Connection connection = DBConnect.getConnection();){
            String query = DBFunctions.GetNumberOfOpponentGames.getName();
            CallableStatement cs = connection.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, opponentId);
            cs.execute();
            numberOfGames = cs.getInt(1);

            query = DBFunctions.GetNumberOfOpponentWins.getName();
            cs = connection.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, opponentId);
            cs.execute();
            numberOfWins = cs.getInt(1);

            numberOfLoses = numberOfGames - numberOfWins;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        lbName.setText(dbOpponentItem.getName().getValue());
        lbID.setText(String.format("#%d", opponentId));

        String opponentStatisticsPattern = "%s %d";
        Label lbStatistic = lbNumberOfGames;
        lbStatistic.setText(String.format(opponentStatisticsPattern, lbStatistic.getText(), numberOfGames));

        lbStatistic = lbNumberOfWins;
        lbStatistic.setText(String.format(opponentStatisticsPattern, lbStatistic.getText(), numberOfWins));

        lbStatistic = lbNumberOfLoses;
        lbStatistic.setText(String.format(opponentStatisticsPattern, lbStatistic.getText(), numberOfLoses));
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
}
