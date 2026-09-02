package gs.konick.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static void createFileIfNotExists(String fileName) {
        System.out.println("Creating file: " + fileName);
        Path path = Paths.get(fileName);
        System.out.println(path.toAbsolutePath());
        try {
            Files.createDirectories(path.getParent()); // если папки нет
            if (Files.notExists(path)) {
                Files.createFile(path); // создаст файл, если его нет
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}