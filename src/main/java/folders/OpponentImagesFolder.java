package folders;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class OpponentImagesFolder extends Folder {
    public OpponentImagesFolder() {
        super(String.format("%s/%s", System.getProperty("user.dir"), "opponent_images"));
    }

    @Override
    public File getDefaultFile() {
        URL resource = getClass().getClassLoader().getResource("images/opponent/unknown.png");
        File file = null;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return file;
    }
}
