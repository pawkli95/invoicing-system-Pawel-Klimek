package pl.futurecollars.invoicing.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class FileService {

    private final File file;

    public FileService(String fileName) {
        file = new File(fileName);
    }

    public void write(String string) {
        try {
            FileUtils.write(file, string + "delimeter \n", "UTF-8", true);
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
            String string = FileUtils.readFileToString(file, "UTF-8");
            if (string.isEmpty()) {
                return Collections.emptyList();
            }
            return Arrays.asList(string.split("delimeter \n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
