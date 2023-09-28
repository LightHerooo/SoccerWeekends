package javafx_utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;

public interface FXMLController {
    FXMLLoader getLoader() throws IOException;
    Pane getMainPane();
}
