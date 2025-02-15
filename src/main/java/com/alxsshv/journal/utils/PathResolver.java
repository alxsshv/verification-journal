package com.alxsshv.journal.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PathResolver {
    private static String fileSeparator = FileSystems.getDefault().getSeparator();


    public String getFileSeparator(){
        duplicateBackslashForWindowsSeparator();
        return fileSeparator;
    }

    public void createFilePathIfNotExist(String path) throws IOException {
        String[] folders = path.split(getFileSeparator());
        StringBuilder currentPath = new StringBuilder();
        for (String folder : folders){
            currentPath.append(folder).append(getFileSeparator());
            createFolderIfNotExist(String.valueOf(currentPath));
        }
    }

    public void createFolderIfNotExist(String folderPath) throws IOException {
        File directory = new File(folderPath);
        if (!directory.exists() && !directory.getPath().isEmpty() && isDirectory(folderPath)) {
            log.info("Создание отсутствующей директории {}", directory.getAbsolutePath());
            boolean isCreated = directory.mkdir();
            if (!isCreated){
                throw new IOException("Ошибка создания директории " +  directory.getPath());
            }
        }
    }

    public boolean isDirectory(String filePath){
        String fileName = filePath;
        if (filePath.contains(getFileSeparator())){
            String[] filePathParts= fileName.split(getFileSeparator());
            fileName = filePathParts[filePathParts.length-1];
        }
        String fileTemplate = "[\\w\\/\\\\]+\\.[\\w\\/\\\\]+";
        return !Pattern.matches(fileTemplate,fileName);
    }

    private void duplicateBackslashForWindowsSeparator(){
        if (fileSeparator.equals("\\")) {
            fileSeparator = fileSeparator + fileSeparator;
        }
    }

}
