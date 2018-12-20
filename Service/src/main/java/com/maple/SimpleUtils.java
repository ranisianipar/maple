package com.maple;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SimpleUtils {

    public static long getTotalObject(MongoRepository repo) {
        return repo.count();
    }

    public static long getTotalPages(long size, long totalObject) {
        if (totalObject % size==0)
            return totalObject/size;
        return (totalObject/size) + 1;
    }

    public static String storeFile(String folderPath, MultipartFile file, String id) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String path = folderPath+"/"+id+"_"+filename;
        if (!Files.exists(Paths.get(folderPath))) Files.createDirectory(Paths.get(folderPath));
        InputStream inputStream = file.getInputStream();
        Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        return path;
    }

    public static void deleteFile(String path) throws IOException{
        if (path == null) return;
        Files.deleteIfExists(Paths.get(path));
    }

}
