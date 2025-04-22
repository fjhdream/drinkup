package cool.drinkup.drinkup.workflow.service.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageService {

    @Value("${app.image-storage.path:./uploaded-images}")
    private String storagePath;
    
    private Path rootLocation;

    public void init() {
        try {
            rootLocation = Paths.get(storagePath);
            Files.createDirectories(rootLocation);
            log.info("Storage initialized at: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not initialize storage location", e);
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public String storeImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Cannot store empty file");
            }
            
            // Generate a unique ID for the image
            String imageId = UUID.randomUUID().toString();
            
            // Get file extension
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // Create the final filename with ID
            String filename = imageId + extension;
            
            // Create the target path
            Path targetPath = rootLocation.resolve(filename);
            
            // Copy the file to the target path, replacing if it exists
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("Stored image with ID: {} at path: {}", imageId, targetPath);
            return imageId;
        } catch (IOException e) {
            log.error("Failed to store image", e);
            throw new RuntimeException("Failed to store image: " + e.getMessage(), e);
        }
    }

    public Resource loadImage(String imageId) {
        try {
            // Find the file with the given imageId
            File directory = rootLocation.toFile();
            File[] files = directory.listFiles((dir, name) -> name.startsWith(imageId));
            
            if (files == null || files.length == 0) {
                log.error("Image not found with ID: {}", imageId);
                throw new RuntimeException("Image not found with ID: " + imageId);
            }
            
            Path filePath = files[0].toPath();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                log.error("Could not read image with ID: {}", imageId);
                throw new RuntimeException("Could not read image with ID: " + imageId);
            }
        } catch (IOException e) {
            log.error("Could not load image", e);
            throw new RuntimeException("Could not load image", e);
        }
    }

    /**
     * Deletes an image by its ID
     * 
     * @param imageId the ID of the image to delete
     * @return true if the image was successfully deleted, false otherwise
     */
    public boolean deleteImage(String imageId) {
        try {
            // Find the file with the given imageId
            File directory = rootLocation.toFile();
            File[] files = directory.listFiles((dir, name) -> name.startsWith(imageId));
            
            if (files == null || files.length == 0) {
                log.warn("Image not found with ID: {}", imageId);
                return false;
            }
            
            boolean allDeleted = true;
            for (File file : files) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.error("Failed to delete image file: {}", file.getAbsolutePath());
                    allDeleted = false;
                }
            }
            
            if (allDeleted) {
                log.info("Successfully deleted image with ID: {}", imageId);
                return true;
            } else {
                log.error("Failed to delete some files for image with ID: {}", imageId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting image with ID: {}", imageId, e);
            return false;
        }
    }
} 