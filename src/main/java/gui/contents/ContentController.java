package gui.contents;

import gui.MainController;
import javafx.FXMLController;
import javafx.JavaFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ContentController implements Initializable, FXMLController {
    private String contentInfo;
    private Parent content;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button btnGoToMainMenu;
    @FXML
    private Label lbContentInfo;
    @FXML
    private AnchorPane apContent;

    public ContentController(String contentInfo, Parent content) {
        this.contentInfo = contentInfo;

        JavaFXUtils.setZeroAnchors(content);
        this.content = content;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbContentInfo.setText(contentInfo);
        apContent.getChildren().setAll(content);

        btnGoToMainMenu.setOnAction(this::btnGoToMainMenu_click);
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Content.fxml"));
        loader.setController(this);

        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    @FXML
    public void btnGoToMainMenu_click(ActionEvent actionEvent) {
        try {
            MainController controller = new MainController();
            Parent p = controller.getLoader().load();
            JavaFXUtils.setZeroAnchors(p);

            Pane parent = (Pane)mainPane.getParent();
            parent.getChildren().setAll(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
