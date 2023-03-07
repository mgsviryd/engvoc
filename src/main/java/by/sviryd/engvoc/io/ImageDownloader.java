package by.sviryd.engvoc.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ImageDownloader {
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 20000;
    private static final int TASK_SUBMIT_EXTRA = 1000;
    private static final int TASK_TIMEOUT = TASK_SUBMIT_EXTRA + CONNECTION_TIMEOUT + READ_TIMEOUT;

    public boolean downloadByUrl(URL url, String pathName) {
        ExecutorService service = null;
        boolean[] isDownload = {true};
        try {
            service = Executors.newSingleThreadExecutor();
            Future<?> result = service.submit(() -> {
                try {
                    FileUtils.copyURLToFile(
                            url,
                            new File(pathName),
                            CONNECTION_TIMEOUT, READ_TIMEOUT);
                } catch (IOException e) {
                    log.error("Cannot copy URL: " + url + "to file: " + pathName, e);
                    isDownload[0] = false;
                }
            });
            result.get(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
            return isDownload[0];
        } catch (Exception e) {
            log.error("Cannot copy URL: " + url + "to file: " + pathName, e);
            return false;
        } finally {
            if (service != null) service.shutdown();
        }
    }
}
