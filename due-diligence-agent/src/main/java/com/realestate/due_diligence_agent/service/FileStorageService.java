package com.realestate.due_diligence_agent.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final Path storageLocation = Paths.get("generated_reports");

    public FileStorageService() {
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize report storage directory", e);
        }
    }

    public String saveFile(String filename, byte[] content) throws IOException {
        Path targetPath = this.storageLocation.resolve(filename);
        try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
            fos.write(content);
        }
        return targetPath.toAbsolutePath().toString();
    }

    public byte[] loadFile(String filename) throws IOException {
        Path targetPath = this.storageLocation.resolve(filename);
        return Files.readAllBytes(targetPath);
    }
}