package cool.drinkup.drinkup.workflow.service.image;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;

    private final String bucket = "object-bucket";

    private String prefix = "images/";

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
            String key = prefix + filename;
            
            // Upload the file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            log.info("Stored image with ID: {} in S3 bucket: {} with key: {}", imageId, bucket, key);
            return filename;
        } catch (IOException e) {
            log.error("Failed to store image", e);
            throw new RuntimeException("Failed to store image: " + e.getMessage(), e);
        }
    }

    public Resource loadImage(String imageId) {
        try {
            // Find file with the matching prefix in S3
            String keyPrefix = prefix + imageId;
            
            // Since we don't know the extension, we need to check if the object exists
            try {
                HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                        .bucket(bucket)
                        .key(keyPrefix)
                        .build();
                s3Client.headObject(headObjectRequest);
            } catch (NoSuchKeyException e) {
                // Object doesn't exist with the exact name, we need to try with extensions
                // For simplicity, we'll just log an error and throw an exception
                log.error("Image not found with ID: {}", imageId);
                throw new RuntimeException("Image not found with ID: " + imageId);
            }
            
            // Get the object from S3
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyPrefix)
                    .build();
            
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            byte[] content = s3Object.readAllBytes();
            
            Resource resource = new ByteArrayResource(content);
            
            log.info("Successfully loaded image with ID: {}", imageId);
            return resource;
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
            // Find the file with the given imageId in S3
            String keyPrefix = prefix + imageId;
            
            // Try to delete the object
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyPrefix)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted image with ID: {}", imageId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting image with ID: {}", imageId, e);
            return false;
        }
    }
}