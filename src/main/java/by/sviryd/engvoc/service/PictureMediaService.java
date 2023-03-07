package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.PictureMediaConfig;
import by.sviryd.engvoc.config.ServerPathConfig;
import by.sviryd.engvoc.domain.dto.PictureMediaDTO;
import by.sviryd.engvoc.io.ImageDownloader;
import by.sviryd.engvoc.service.imageResizer.ImageResizerService;
import by.sviryd.engvoc.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PictureMediaService {
    private static final double MAX_COMPRESSION_QUALITY = 100;
    private static final int MAX_DEPTH_ONLY_MAIN = 1;

    @Autowired
    private PictureMediaConfig pictureMediaConfig;

    @Autowired
    private ServerPathConfig serverPathConfig;

    @Autowired
    private ImageResizerService imageResizerService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDownloader imageDownloader;


    @PostConstruct
    public void init() throws IOException {
        if (pictureMediaConfig.isInit()) {
            processUploadPictures();
        }
    }

    private void processUploadPictures() throws IOException {
        createDirectories();
        removeNotSynchronized();
        if (pictureMediaConfig.isRemovePicturesIfAreNotPictureOrNoRead()) {
            removePicturesIfAreNotPictureOrNoRead();
        }
        if (pictureMediaConfig.isCompressMain()) {
            compressMain(pictureMediaConfig.getCompressMainQuality());
        }
        if (pictureMediaConfig.isCreateMediaPictures()) {
            removeMedias();
            createMediaPictures();
        }
        if (pictureMediaConfig.isCreateMarkerPictures()) {
            removeMarkers();
            createMarkerPictures();
        }
    }

    private void removeMedias() throws IOException {
        log.info("Remove media pictures");
        try {
            Files
                    .walk(Paths.get(getMainPath()), MAX_DEPTH_ONLY_MAIN)
                    .filter(Files::isRegularFile)
                    .forEach(this::removeAllMediaPictures);
        } catch (IOException e) {
            log.error("Cannot walk to delete media pictures.", e);
            throw e;
        }
    }

    private void removeAllMediaPictures(Path source) {
        rollbackMediaPictures(source, pictureMediaConfig.getPictureMedias());
    }

    private void compressMain(double compressQuality) throws IOException {
        log.info("Compress main pictures");
        try {
            Files
                    .walk(Paths.get(getMainPath()), MAX_DEPTH_ONLY_MAIN)
                    .filter(path -> Files.isRegularFile(path) && !isMarkerPicture(path))
                    .forEach(path -> compressImage(path, compressQuality));
        } catch (IOException e) {
            log.error("Cannot walk to compress main pictures.", e);
            throw e;
        }
    }

    private boolean isMarkerPicture(Path source) {
        return pictureMediaConfig.getIndexMarker(source) != -1;
    }

    private boolean compressImage(Path path, String mimeType, double compressQuality) {
        try {
            imageResizerService.compressImage(path.toString(), mimeType, compressQuality);
        } catch (Exception e) {
            log.error("Cannot compress image: " + path, e);
            return false;
        }
        return true;
    }

    private boolean compressImage(Path path, double compressQuality) {
        try {
            imageResizerService.compressImage(path.toString(), compressQuality);
        } catch (Exception e) {
            log.error("Cannot compress image: " + path, e);
            return false;
        }
        return true;
    }

    public boolean saveAllMediaAndMarkers(Path source, List<PictureMediaDTO> pictureMedias) {
        String mimeType;
        try {
            mimeType = imageService.getMimeType(source.toFile());
        } catch (IOException e) {
            log.error("Cannot receive picture mimeType: " + source, e);
            return false;
        }
        return saveAllMediaAndMarkers(source, mimeType, pictureMedias);
    }

    private boolean saveAllMediaAndMarkers(Path source, String mimeType, List<PictureMediaDTO> pictureMedias) {
        if (!createAllMediaPictures(source, mimeType, pictureMedias)) {
            return false;
        }
        if (!createAllMarkerPictures(source, mimeType, pictureMedias)) {
            return false;
        }
        return true;
    }

    private void removeMarkers() throws IOException {
        log.info("Remove marker pictures");
        try {
            Files
                    .walk(Paths.get(getMainPath()))
                    .filter(path -> Files.isRegularFile(path) && isMarkerPicture(path))
                    .forEach(this::deletePathIfExists);
        } catch (IOException e) {
            log.error("Cannot walk to delete marker pictures.", e);
            throw e;
        }
    }

    private void createMarkerPictures() throws IOException {
        log.info("Create marker pictures");
        try {
            Files
                    .walk(Paths.get(getMainPath()))
                    .filter(path -> Files.isRegularFile(path) && !isMarkerPicture(path))
                    .forEach(path -> {
                        try {
                            createMarkerPictures(path);
                        } catch (Exception e) {
                            throw new RuntimeException("Cannot create marker picture:" + path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Cannot walk to create marker pictures.", e);
            throw e;
        }
    }

    private void removePicturesIfAreNotPictureOrNoRead() throws IOException {
        log.info("Remove not pictures");
        try {
            Files
                    .walk(Paths.get(getMainPath()))
                    .filter(path -> Files.isRegularFile(path))
                    .forEach(this::removeIfNotPictureOrNoRead);
        } catch (IOException e) {
            log.error("Cannot walk to remove not pictures.", e);
            throw e;
        }

    }

    private void removeNotSynchronized() {
        log.info("Remove not synchronized");
        List<PictureMediaDTO> pictureMedias = pictureMediaConfig.getPictureMedias();
        for (PictureMediaDTO p : pictureMedias) {
            String child = getMainPath() + p.getPath();
            Path mainPath = Paths.get(getMainPath());
            Path childPath = Paths.get(child);
            walkAndRemoveIfNotExistsInMain(mainPath, childPath);
        }
    }

    private String getMainPath() {
        return serverPathConfig.getAbsolute() + serverPathConfig.getUploadPicture();
    }

    private void walkAndRemoveIfNotExistsInMain(Path mainPath, Path childPath) {
        try {
            Set<Path> fileNames = Files.list(mainPath).map(Path::getFileName).collect(Collectors.toSet());
            Files
                    .walk(childPath)
                    .filter(path -> !fileNames.contains(path.getFileName()) && Files.isRegularFile(path))
                    .forEach(this::deletePathIfExists);
        } catch (IOException e) {
            log.error("Files operations with: " + mainPath + " / " + childPath, e);
        }
    }

    public void removeIfNotPictureOrNoRead(Path p) {
        try {
            if (!imageService.isImage(p.toString())) {
                deletePathIfExists(p);
            }
        } catch (Exception e) {
            deletePathIfExists(p);
        }
    }

    private void deletePathIfExists(Path p) {
        try {
            Files.deleteIfExists(p);
        } catch (IOException e) {
            log.error("Cannot delete path: " + p, e);
        }
    }

    private void createMediaPictures() throws IOException {
        log.info("Create media pictures");
        try {
            Files
                    .walk(Paths.get(getMainPath()), MAX_DEPTH_ONLY_MAIN)
                    .filter(Files::isRegularFile)
                    .forEach(this::createAllMediaPictures);
        } catch (IOException e) {
            log.error("Cannot walk to create media pictures.", e);
            throw e;
        }
    }

    private boolean createAllMediaPictures(Path source) {
        String mimeType;
        try {
            mimeType = imageService.getMimeType(source.toFile());
        } catch (IOException e) {
            log.error("Cannot receive mime type: " + source, e);
            return false;
        }
        return createAllMediaPictures(source, mimeType, pictureMediaConfig.getPictureMedias());
    }

    private boolean createAllMediaPictures(Path source, String mimeType, List<PictureMediaDTO> pictureMedias) {
        try {
            for (PictureMediaDTO p : pictureMedias) {
                Path target = p.getMediaPath(source);
                int width;
                try {
                    width = imageService.getWidth(source.toString());
                } catch (IOException e) {
                    throw new RuntimeException("Cannot receive width: " + target, e);
                }

                if (p.isWidthPreferredThanPercent(width) && p.isWidthNotExpand(width)) {
                    imageResizerService.resizeProportional(source.toString(), target.toString(), mimeType, p.getCompressionWidth());
                } else {
                    imageResizerService.resize(source.toString(), target.toString(), mimeType, p.getCompressionPercent());
                }
            }
        } catch (Exception e) {
            log.error("Cannot create all media pictures: " + source, e);
            rollbackMediaPictures(source, pictureMedias);
            return false;
        }
        return true;
    }

    private void rollbackMediaPictures(Path source, List<PictureMediaDTO> pictureMedias) {
        for (PictureMediaDTO p : pictureMedias) {
            Path target = p.getMediaPath(source);
            deletePathIfExists(target);
        }
    }

    public String getDefaultPicturePath() {
        return getPictureMediaPathName(getDefaultPictureFileName());
    }
    public String getDefaultPictureFileName(){
        return pictureMediaConfig.getDefaultPictureFileName();
    }

    public String saveDefaultPictureOrRollback(URL url) {
        return savePictureOrRollback(url, getDefaultPicturePath(), pictureMediaConfig.getPictureMedias());
    }

    public String savePictureOrRollback(URL url) {
        if (url == null) {
            return null;
        }
        String targetPathName = getPictureMediaPathNameAsUUID(url.toString());
        String pathName = savePictureOrRollback(url, targetPathName, pictureMediaConfig.getPictureMedias());
        if (pathName == null) {
            return null;
        } else {
            Path fileName = Paths.get(pathName).getFileName();
            return fileName.toString();
        }
    }

    public String savePictureOrRollback(URL url, String targetPathName, List<PictureMediaDTO> pictureMedias) {
        if (url == null){
            return null;
        }
        if (!savePicture(url, targetPathName, pictureMedias)) {
            deletePathIfExists(Paths.get(targetPathName));
            return null;
        }
        return targetPathName;
    }

    private boolean savePicture(URL url, String targetPathName, List<PictureMediaDTO> pictureMedias) {
        if (!imageDownloader.downloadByUrl(url, targetPathName)) {
            return false;
        }
        Path target = Paths.get(targetPathName);
        String mimeType;
        try {
            mimeType = imageService.getMimeType(target.toFile());
        } catch (IOException e) {
            log.error("Cannot receive mime type.", e);
            return false;
        }
        if (!compressImage(target, mimeType, MAX_COMPRESSION_QUALITY)) {
            return false;
        }
        return saveAllMediaAndMarkers(target, mimeType, pictureMedias);
    }


    private String getPictureMediaPathNameAsUUID(String pathName) {
        return serverPathConfig.getAbsolute() + serverPathConfig.getUploadPicture() + File.separator + getPictureFileNameAsUUID(pathName);
    }
    private String getPictureMediaPathName(String pathName) {
        return serverPathConfig.getAbsolute() + serverPathConfig.getUploadPicture() + File.separator + pathName;
    }

    public String getPictureFileNameAsUUID(String imageUrl) {
        String extension = FilenameUtils.getExtension(imageUrl).toLowerCase();
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }

    private void createMarkerPictures(Path source) throws Exception {
        String mimeType = imageService.getMimeType(source.toFile());
        createMarkerPictures(source, mimeType);

    }

    private void createMarkerPictures(Path source, String mimeType) throws Exception {
        int size = pictureMediaConfig.getMarkerNames().size();
        for (int i = 0; i < size; i++) {
            Path target = getMarkerPath(source, i);
            Integer scaledWidth = getMarkerWidth(source, i);
            Pair<Integer, Integer> widthHeight = adjustAndGetWidthHeight(source, scaledWidth);
            if (widthHeight != null) {
                imageResizerService.resize(source.toString(), target.toString(), mimeType, widthHeight.getFirst(), widthHeight.getSecond());
            }
        }
    }

    private Pair<Integer, Integer> adjustAndGetWidthHeight(Path source, Integer scaledWidth) throws Exception {
        Pair<Integer, Integer> widthHeight;
        widthHeight = imageService.getWidthHeight(source.toString());
        Integer width = widthHeight.getFirst();
        if (scaledWidth >= width) {
            return widthHeight;
        } else {
            int markerHeight = (int) Math.round(scaledWidth / ((double) width) * widthHeight.getSecond());
            return new Pair<>(scaledWidth, markerHeight);
        }
    }

    private Integer getMarkerWidth(Path source, int i) {
        String key;
        Path parent = source.getParent();
        String markerName = pictureMediaConfig.getMarkerNames().get(i);
        if (parent.toString().equals(getMainPath())) {
            key = markerName;
        } else {
            key = markerName + parent.getFileName();
        }
        return pictureMediaConfig.getMarkerWidths().get(key);
    }

    private Path getMarkerPath(Path source, int iMarker) {
        String s = source.getFileName().toString();
        String[] split = s.split("\\.");
        String marker = pictureMediaConfig.getMarkerNames().get(iMarker);
        return source.resolveSibling(split[0] + marker + "." + split[1]);
    }

    public boolean createAllMarkerPictures(Path source, String mimeType, List<PictureMediaDTO> pictureMedias) {
        try {
            createMarkerPictures(source, mimeType);
            for (PictureMediaDTO p : pictureMedias) {
                Path target = p.getMediaPath(source);
                createMarkerPictures(target);
            }
        } catch (Exception e) {
            log.error("Cannot create all marker picture: " + source, e);
            rollbackMarkerPictures(source, pictureMedias);
            return false;
        }
        return true;
    }

    private void rollbackMarkerPictures(Path source, List<PictureMediaDTO> pictureMedias) {
        int sizeMarker = pictureMediaConfig.getMarkerNames().size();
        for (int i = 0; i < sizeMarker; i++) {
            Path targetMarker = getMarkerPath(source, i);
            deletePathIfExists(targetMarker);
        }
        for (PictureMediaDTO p : pictureMedias) {
            Path targetMedia = p.getMediaPath(source);
            for (int i = 0; i < sizeMarker; i++) {
                Path targetMarker = getMarkerPath(targetMedia, i);
                deletePathIfExists(targetMarker);
            }
        }
    }

    private void createDirectories() throws IOException {
        log.info("Create directories");
        List<PictureMediaDTO> pictureMedias = pictureMediaConfig.getPictureMedias();
        for (PictureMediaDTO p : pictureMedias) {
            String path = getMainPath() + p.getPath();
            serverPathConfig.createDirectories(path);
        }
    }
}
