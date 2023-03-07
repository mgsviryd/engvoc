package by.sviryd.engvoc.service.imageResizer;

public interface IImageResizer {
    void resize(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight)
            throws Exception;

    void resize(String inputImagePath, String outputImagePath, String mimeType, int width, int height)
            throws Exception;

    void resize(String inputImagePath, String outputImagePath, double compressionPercent)
            throws Exception;

    void resize(String inputImagePath, String outputImagePath, String mimeType, double compressionPercent) throws Exception;

    void resizeProportional(String inputImagePath, String outputImagePath, int scaledWidth)
    throws Exception;

    void resizeProportional(String inputImagePath, String mimeType, String outputImagePath, int scaledWidth) throws Exception;

    void compressImage(String inputImagePath, double qualityPercent)
    throws Exception;

    void compressImage(String pathName, String mimeType, double qualityPercent) throws Exception;
}
