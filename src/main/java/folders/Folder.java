package folders;

import java.io.File;

public abstract class Folder {
    private String path;

    public Folder(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public File getFolder() {
        File folder = new File(path);
        folder.mkdirs();
        return folder;
    }

    public File findFile(String fileName) {
        File folder = getFolder();
        String imgPathPattern = "%s/%s";
        String filePath = String.format(imgPathPattern, folder.getPath(), fileName);
        return new File(filePath);
    }

    public abstract File getDefaultFile();
}
