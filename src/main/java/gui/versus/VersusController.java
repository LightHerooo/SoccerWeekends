package gui.versus;

import db.tables.opponent.DBOpponentItem;
import gui.MainController;
import gui.versus.games.GamesController;
import gui.versus.select_opponent.SelectOpponent;
import javafx.FXMLController;
import javafx.JavaFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VersusController implements Initializable, FXMLController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private VBox vbOpponents;
    @FXML
    private AnchorPane apGames;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnAddOpponent;

    // Вызывается при Load FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAddOpponent_click(null);

       // vbOpponents.getChildren().addListener((InvalidationListener) observable -> showOpponentsResult());
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Versus.fxml"));
        return fxmlLoader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    @FXML
    private void showOpponentsResult() {
        List<DBOpponentItem> ois = new ArrayList<>();
        for (Node n: vbOpponents.getChildren()) {
            if (n instanceof SelectOpponent so) {
                ois.add(so.getController().getSelectedOpponentItem());
            }
        }

        try {
            GamesController rc = new GamesController(ois);
            Parent p = rc.getLoader().load();
            JavaFXUtils.setZeroAnchors(p);
            apGames.getChildren().setAll(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeSelectOpponentNumeration() {
        int index = 1;
        for (Node p: vbOpponents.getChildren()) {
            if (p instanceof SelectOpponent so) {
                so.getController().setOpponentNumber(index);
                index++;
            }
        }
    }

    @FXML
    private void btnBack_click(ActionEvent actionEvent) throws IOException {
        MainController mc = new MainController();
        Parent p = mc.getLoader().load();
        JavaFXUtils.setZeroAnchors(p);

        Pane mp = (Pane) mainPane.getParent();
        mp.getChildren().setAll(p);
    }

    @FXML
    public void btnAddOpponent_click(ActionEvent actionEvent) {
        SelectOpponent so = new SelectOpponent();
        so.getController().getCbOpponent().getSelectionModel().selectedItemProperty().addListener(changeListener -> {
            showOpponentsResult();
        });

        so.getController().getBtnDelete().setOnAction(event -> {
            vbOpponents.getChildren().remove(so);
            showOpponentsResult();
            changeSelectOpponentNumeration();
        });

        vbOpponents.getChildren().add(so);
        changeSelectOpponentNumeration();
    }
}
