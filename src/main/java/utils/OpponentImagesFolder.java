package utils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class OpponentImagesFolder {
    private String path;

    public OpponentImagesFolder() {
        String mainPath = System.getProperty("user.dir");
        path = String.format("%s/%s", mainPath, "opponent_images");
    }

    public String getPath() {
        return path;
    }

    public File getFolder() {
        File folder = new File(path);
        folder.mkdirs();
        return folder;
    }

    public void add() {

    }

    public File findImageFile(String imgName) {
        File folder = getFolder();
        String imgPathPattern = "%s/%s";
        String imgPath = String.format(imgPathPattern, folder.getPath(), imgName);
        File imgFile = new File(imgPath);
        if (!imgFile.exists()) {
            URL resource = getClass().getClassLoader().getResource("images/unknown.png");
            try {
                imgFile = new File(resource.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        return imgFile;
    }
}
