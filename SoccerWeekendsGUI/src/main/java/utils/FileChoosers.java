package utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class FileChoosers {
    public static File chooseImageFile(Window window) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        fc.getExtensionFilters().add(ef);
        return fc.showOpenDialog(window);
    }
}
