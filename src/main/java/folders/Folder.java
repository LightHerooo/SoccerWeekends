package folders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
        String filePath = String.format("%s/%s", folder.getPath(), fileName);
        return new File(filePath);
    }

    public void delete(String fileName) {
        File file = findFile(fileName);
        file.delete();
    }

    public void add(String filePath, String newFileName) {
        File oldFile = new File(filePath);
        if (oldFile.exists()) {
            File folder = getFolder();
            File newFile = new File(String.format("%s/%s", folder.getPath(), newFileName));
            try (FileInputStream fis = new FileInputStream(oldFile);
                 FileOutputStream fos = new FileOutputStream(newFile)) {
                byte[] buffer = new byte[256];
                int numberOfBytes = 0;
                while ((numberOfBytes = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, numberOfBytes);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public abstract File getDefaultFile();
}
