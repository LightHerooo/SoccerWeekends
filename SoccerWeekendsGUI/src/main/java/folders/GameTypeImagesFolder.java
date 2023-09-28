package folders;

import java.io.InputStream;

public class GameTypeImagesFolder extends Folder {

    public GameTypeImagesFolder() {
        super(String.format("%s/%s", System.getProperty("user.dir"), "game_type_images"));
    }
    @Override
    public InputStream getDefaultResource() {
        return getClass().getResourceAsStream("/images/game_type/unknown.png");
        /*URL resource = getClass().getClassLoader().getResource("images/game_type/unknown.png");
        File file = null;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return file;*/
    }
}
