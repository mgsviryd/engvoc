package by.sviryd.engvoc.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;
import lombok.Value;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@ToString
@Value
@JsonIgnoreProperties(ignoreUnknown = true, value = {"compressionPercent", "compressionWidth", "directory"})
public class PictureMediaDTO {
    private String path;
    private String media;
    private Double compressionPercent;
    private Integer compressionWidth;
    private String directory;

    public boolean isWidthPreferredThanPercent(int actualWidth) {
        return actualWidth * compressionPercent / 100.0 < compressionWidth;
    }

    public boolean isWidthNotExpand(int width) {
        return compressionWidth < width;
    }

    public Path getMediaPath(Path source) {
        String target = directory + path + File.separator + source.getFileName();
        return Paths.get(target);
    }
}
