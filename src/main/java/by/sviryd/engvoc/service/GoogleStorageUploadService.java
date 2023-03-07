package by.sviryd.engvoc.service;


import by.sviryd.engvoc.config.GoogleActivatorConfig;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class GoogleStorageUploadService {
    @Autowired
    private GoogleActivatorConfig config;

    public void uploadObject(String objectName, String filePath) throws IOException {
        uploadObject(config.getProjectId(), config.getDefaultBucket(), objectName, filePath);
    }

    private void uploadObject(String projectId, String bucketName, String objectName, String filePath) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
    }
}
