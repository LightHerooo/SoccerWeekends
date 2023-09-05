package gui;

import gui.versus.VersusController;
import javafx.FXMLController;
import javafx.JavaFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class MainController implements FXMLController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnSettings;
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

    public void btnExit_click(ActionEvent actionEvent) {

    }

    public void btnSettings_click(ActionEvent actionEvent) {
    }

    public void btnStart_click(ActionEvent actionEvent) throws IOException {
        VersusController vc = new VersusController();
        Parent p = vc.getLoader().load();
        JavaFXUtils.setZeroAnchors(p);
        mainPane.getChildren().setAll(p);
    }
}
