package folders;

import java.io.InputStream;

public class OpponentImagesFolder extends Folder {
    public OpponentImagesFolder() {
        super(String.format("%s/%s", System.getProperty("user.dir"), "opponent_images"));
    }

    @Override
    public InputStream getDefaultResource() {
        return getClass().getResourceAsStream("/images/opponent/unknown.png");
        /*URL resource = getClass().getResource("/images/opponent/unknown.png");
        File file = null;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return file;*/
    }
}
