package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.GoogleActivatorConfig;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;


@Service
public class GoogleActivatorService {
    @Autowired
    private GoogleActivatorConfig config;
    @Getter
    private Storage storage;

    @PostConstruct
    public void init() throws IOException {
        if (config.isInit()){
            storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(config.getPathToJsonKey())))
                    .setProjectId(config.getProjectId())
                    .build().getService();
            Page<Bucket> list = storage.list(Storage.BucketListOption.userProject(config.getProjectId()));
            if (!list.getValues().iterator().hasNext()) {
                storage.create(BucketInfo.of(config.getDefaultBucket()));
            }
        }
    }
}
