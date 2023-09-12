package folders;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class GameTypeImagesFolder extends Folder {

    public GameTypeImagesFolder() {
        super(String.format("%s/%s", System.getProperty("user.dir"), "game_type_images"));
    }
    @Override
    public File getDefaultFile() {
        URL resource = getClass().getClassLoader().getResource("images/game_type/unknown.png");
        File file = null;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return file;
    }
}
