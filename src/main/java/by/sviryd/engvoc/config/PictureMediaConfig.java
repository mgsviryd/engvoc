package by.sviryd.engvoc.config;

import by.sviryd.engvoc.domain.dto.PictureMediaDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "picture.media")
@Import(ServerPathConfig.class)
public class PictureMediaConfig {
    @Autowired
    private ServerPathConfig serverPathConfig;
    private boolean init;
    private boolean removePicturesIfAreNotPictureOrNoRead;
    private boolean compressMain;
    private double compressMainQuality;
    private boolean createMediaPictures;
    private boolean createMarkerPictures;
    private String defaultPictureFileName;

    private List<String> markerNames = new ArrayList<>();
    private Map<String, Integer> markerWidths = new HashMap<>();
    private List<String> paths = new ArrayList<>();
    private List<String> medias = new ArrayList<>();
    private List<Double> compressionPercents = new ArrayList<>();
    private List<Integer> compressionWidths = new ArrayList<>();
    private List<String> directories = new ArrayList<>();

    public List<PictureMediaDTO> getPictureMedias() {
        List<PictureMediaDTO> list = new ArrayList<>();
        int size = paths.size();
        for (int i = 0; i < size; i++) {
            list.add(new PictureMediaDTO(
                    paths.get(i),
                    medias.get(i),
                    compressionPercents.get(i),
                    compressionWidths.get(i),
                    serverPathConfig.getAbsolute() + serverPathConfig.getUpload() + serverPathConfig.getUploadPicture()));
        }
        return list;
    }

    public int getIndexMarker(Path path) {
        String filename = path.getFileName().toString();
        int size = markerNames.size();
        for (int i = 0; i < size; i++) {
            String m = markerNames.get(i);
            if (filename.contains(m)) {
                return i;
            }
        }
        return -1;
    }
}
