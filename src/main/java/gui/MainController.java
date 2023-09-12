package gui;

import gui.contents.ContentController;
import gui.contents.game_types.GameTypesController;
import gui.contents.games.GamesController;
import gui.contents.opponents.OpponentsController;
import javafx.FXMLController;
import javafx.JavaFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public final class MainController implements FXMLController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button btnGames;
    @FXML
    private Button btnOpponents;
    @FXML
    private Button btnGameTypes;
    @FXML
    private Button btnExit;

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        return fxmlLoader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    @FXML
    public void btnGames_click(ActionEvent actionEvent) throws IOException {
        GamesController gc = new GamesController();
        ContentController cc = new ContentController("Здесь отображены результаты игр и статистика выбранных оппонентов.", gc.getLoader().load());

        Parent p = cc.getLoader().load();
        JavaFXUtils.setZeroAnchors(p);
        mainPane.getChildren().setAll(p);
    }

    public void btnOpponents_click(ActionEvent actionEvent) throws IOException {
        OpponentsController oc = new OpponentsController();
        ContentController cc = new ContentController("Здесь отображены все существующие оппоненты.", oc.getLoader().load());

        Parent p = cc.getLoader().load();
        JavaFXUtils.setZeroAnchors(p);
        mainPane.getChildren().setAll(p);
    }


    public void btnGameTypes_click(ActionEvent actionEvent) throws IOException {
        GameTypesController gtc = new GameTypesController();
        ContentController cc = new ContentController("Здесь отображены все существующие типы игр.", gtc.getLoader().load());

        Parent p = cc.getLoader().load();
        JavaFXUtils.setZeroAnchors(p);
        mainPane.getChildren().setAll(p);
    }

    public void btnExit_click(ActionEvent actionEvent) {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}
