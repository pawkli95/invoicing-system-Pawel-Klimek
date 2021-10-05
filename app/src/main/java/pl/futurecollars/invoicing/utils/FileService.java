package pl.futurecollars.invoicing.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import pl.futurecollars.invoicing.exceptions.FileNotCreatedException;

@Slf4j
public class FileService {

    private final File file;

    public FileService(String fileName) {
        file = new File(fileName);
        try {
            Files.createFile(Paths.get(fileName));
        } catch (FileAlreadyExistsException e) {
            log.debug("File already exists");
        } catch (IOException e) {
            throw new FileNotCreatedException(e.getMessage());
        }
    }

    public void write(String string) {
        try {
            FileUtils.write(file, string + "\n", "UTF-8", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eraseFile() {
        try {
            FileUtils.write(file, "", "UTF-8", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> read() {
        try {
            return FileUtils.readLines(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
