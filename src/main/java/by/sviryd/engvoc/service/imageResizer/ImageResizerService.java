package by.sviryd.engvoc.service.imageResizer;


import by.sviryd.engvoc.service.ImageService;
import by.sviryd.engvoc.service.exception.ImageExpandNotSupportedException;
import by.sviryd.engvoc.util.DoubleUtil;
import com.twelvemonkeys.image.ResampleOp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;

@Service
@Slf4j
public class ImageResizerService implements IImageResizer {
    private static final double MAX_PERCENT = 100;
    private static final String MIME_TYPE_JPG = "image/jpg";

    @Autowired
    private ImageService imageService;

    @Override
    public void resize(String inputImagePath, String outputImagePath, int width, int height)
            throws Exception {
        String mimeType = imageService.getMimeType(new File(inputImagePath));
        if (imageService.isJPG(mimeType)) {
            resizeJpg(inputImagePath, outputImagePath, width, height);
        } else {
            resizeOther(inputImagePath, outputImagePath, width, height);
        }
    }
    @Override
    public void resize(String inputImagePath, String outputImagePath, String mimeType, int width, int height)
            throws Exception {
        if (imageService.isJPG(mimeType)) {
            resizeJpg(inputImagePath, outputImagePath, width, height);
        } else {
            resizeOther(inputImagePath, outputImagePath, width, height);
        }
    }

    private void resizeOther(String inputImagePath, String outputImagePath, int width, int height) throws Exception {
        File file = new File(inputImagePath);
        String mimeType = imageService.getMimeType(file);
        BufferedImage input = imageService.getBufferedImage(file, mimeType); // Image to resample
        BufferedImageOp resampler = new ResampleOp(width, height, ResampleOp.FILTER_LANCZOS); // A good default filter, see class documentation for more info
        BufferedImage output = resampler.filter(input, null);
        imageService.write(output, outputImagePath, mimeType, MAX_PERCENT);
    }

    private void resizeJpg(String inputPathName, String outputPathName, int width, int height) throws Exception {
        File inputFile = new File(inputPathName);
        BufferedImage inputImage = null;
        BufferedImage outputImage = null;
        try {
            inputImage = imageService.getBufferedImage(inputFile, MIME_TYPE_JPG);
            outputImage = imageService.getBufferedImageByGraphics2D(width, height, inputImage);
            imageService.write(outputImage, outputPathName, MIME_TYPE_JPG, MAX_PERCENT);
        } finally {
            if (inputImage != null) {
                inputImage.flush();
            }
            if (outputImage != null) {
                outputImage.flush();
            }
        }
    }

    @Override
    public void resize(String inputImagePath, String outputImagePath, double compressionPercent) throws Exception {
        String mimeType = imageService.getMimeType(new File(inputImagePath));
        resize(inputImagePath, outputImagePath, mimeType, compressionPercent);
    }


    @Override
    public void resize(String inputImagePath, String outputImagePath, String mimeType, double compressionPercent) throws Exception {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = null;
        int scaledWidth;
        int scaledHeight;
        try {
            inputImage = imageService.getBufferedImage(inputFile, mimeType);
            double ratio = DoubleUtil.getRatio(compressionPercent);
            scaledWidth = (int) (inputImage.getWidth() * ratio);
            scaledHeight = (int) (inputImage.getHeight() * ratio);
        } finally {
            if (inputImage != null) {
                inputImage.flush();
            }
        }
        resize(inputImagePath, outputImagePath, mimeType, scaledWidth, scaledHeight);
    }

    @Override
    public void resizeProportional(String inputImagePath, String outputImagePath, int scaledWidth) throws Exception {
        String mimeType = imageService.getMimeType(new File(inputImagePath));
        resizeProportional(inputImagePath, outputImagePath, mimeType, scaledWidth);
    }

    @Override
    public void resizeProportional(String inputImagePath,  String outputImagePath, String mimeType, int scaledWidth) throws Exception {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = null;
        int scaledHeight;
        try {
            inputImage = imageService.getBufferedImage(inputFile, mimeType);
            if (scaledWidth > inputImage.getWidth()) {
                throw new ImageExpandNotSupportedException("Cannot expand image, only squeeze:" + inputImagePath);
            }
            scaledHeight = (int) (scaledWidth / (double) inputImage.getWidth() * inputImage.getHeight());
        } finally {
            if (inputImage != null) {
                inputImage.flush();
            }
        }
        resize(inputImagePath, outputImagePath, mimeType, scaledWidth, scaledHeight);
    }

    @Override
    public void compressImage(String pathName, String mimeType, double qualityPercent) throws Exception {
        BufferedImage inputImage = null;
        try {
            File inputFile = new File(pathName);
            inputImage = imageService.getBufferedImage(inputFile, mimeType);
            imageService.write(inputImage, pathName, mimeType, qualityPercent);
        } finally {
            if (inputImage != null) {
                inputImage.flush();
            }
        }
    }

    @Override
    public void compressImage(String pathName, double qualityPercent) throws Exception {
        String mimeType = imageService.getMimeType(new File(pathName));
        compressImage(pathName, mimeType, qualityPercent);
    }
}