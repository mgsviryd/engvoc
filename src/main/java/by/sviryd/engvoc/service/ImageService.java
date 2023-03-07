package by.sviryd.engvoc.service;

import by.sviryd.engvoc.util.DoubleUtil;
import by.sviryd.engvoc.util.FileUtil;
import by.sviryd.engvoc.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class ImageService {
    private static final String MIME_TYPE_JPEG = "image/jpeg";
    private static final String MIME_TYPE_JPG = "image/jpg";

    public boolean isPictureUnknownType(Path source) {
        String str = source.toString();
        String suffix = str.substring(str.lastIndexOf(".") + 1);
        String[] readerFileSuffixes = ImageIO.getReaderFileSuffixes();
        List<String> list = Arrays.asList(readerFileSuffixes);
        return !list.contains(suffix);
    }

    public String changeImageTypeTo(String pathName, String imageType) {
        int i = pathName.lastIndexOf(".");
        return pathName.substring(0, i + 1) + imageType;
    }

    public String getMimeType(File file) throws IOException {
        return FileUtil.getMimeType(file);
    }

    public boolean isJPEG(String mimeType) throws IOException {
        return MIME_TYPE_JPEG.equals(mimeType);
    }

    public boolean isJPG(String mimeType) throws IOException {
        return MIME_TYPE_JPG.equals(mimeType);
    }

    public boolean isImage(String pathName) throws Exception {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = getBufferedImage(new File(pathName));
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (bufferedImage != null) {
                bufferedImage.flush();
            }
        }
    }

    public String getExtension(String inputImagePath) {
        return FilenameUtils.getExtension(inputImagePath).toLowerCase();
    }

    public BufferedImage getBufferedImage(File file, String mimeType) throws Exception {
//        if (isJPEG(mimeType)) {
//            try {
//                return getBufferedImageByJPEGCodec(file);
//            } catch (Exception e) {
//            }
//        }
        try {
            return getBufferedImageByImageIO(file, mimeType);
        } catch (Exception e) {
        }
        try {
            return getBufferedImageByImageIO(file);
        } catch (Exception e) {
        }
        throw new RuntimeException("Cannot create buffered image: " + file);
    }

    public BufferedImage getBufferedImage(File file) throws Exception {
        String mimeType = getMimeType(file);
        return getBufferedImage(file, mimeType);
    }

    public BufferedImage getBufferedImageByImageIO(File file, String mimeType) throws Exception {
        ImageInputStream input = null;
        ImageReader reader = null;
        try {
            input = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(mimeType);
            if (!readers.hasNext()) {
                throw new IllegalArgumentException("No reader for: " + file);
            }
            reader = readers.next();
            reader.setInput(input);
            ImageReadParam param = reader.getDefaultReadParam();
            return reader.read(0, param);

        } catch (Exception e) {
            log.error("Cannot create buffered image by ImageIO using mimetype: " + file, e);
            throw e;
        } finally {
            if (reader != null) {
                reader.dispose();
            }
            if (input != null) {
                input.close();
            }
        }
    }

    public BufferedImage getBufferedImageByImageIO(File file) throws Exception {
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            log.error("Cannot create buffered image by ImageIO using format: " + file, e);
            throw e;
        }
    }

//    public BufferedImage getBufferedImageByJPEGCodec(File file) throws Exception {
//        FileInputStream src = null;
//        try {
//            src = new FileInputStream(file);
//            return JPEGCodec.createJPEGDecoder(src).decodeAsBufferedImage();
//        } catch (Exception e) {
//            log.info("Cannot create buffered JPEG image by JPEGCodec: " + file, e);
//            throw e;
//
//        } finally {
//            if (src != null) {
//                src.close();
//            }
//        }
//    }

    public BufferedImage getBufferedImageByGraphics2D(int width, int height, BufferedImage inputImage) {
        Image tmp = inputImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage outputImage = getBufferedImage(width, height, inputImage);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }


    public BufferedImage getBufferedImage(int width, int height, BufferedImage inputImage) {
        BufferedImage bufferedImage;
        int type = inputImage.getType();
        if (type != BufferedImage.TYPE_CUSTOM) {
            bufferedImage = new BufferedImage(width,
                    height, type);
        } else {
            bufferedImage = new BufferedImage(width,
                    height, BufferedImage.TYPE_INT_ARGB);
        }
        return bufferedImage;
    }

    public boolean write(BufferedImage image, String format, File file) throws Exception {
        try {
            return ImageIO.write(image, format, file);
        } catch (Exception e) {
            log.error("Cannot write buffered image by ImageIO: " + file, e);
            throw e;
        }
    }

    public void write(BufferedImage outputImage, String outputPathName, String mimeType, double qualityPercent) throws IOException {
        OutputStream os = null;
        ImageWriter writer = null;
        ImageOutputStream ios = null;
        try {
            File compressedImageFile = new File(outputPathName);
            os = new FileOutputStream(compressedImageFile);
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mimeType);
            writer = writers.next();
            if (writer == null) {
                writer = getImageWriterByFormat(outputPathName);
            }

            ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                try {
                    float ratio = (float) DoubleUtil.getRatio(qualityPercent);
                    param.setCompressionQuality(ratio);
                } catch (Exception e) {
                }
            }
            writer.write(null, new IIOImage(outputImage, null, null), param);
        } finally {
            if (writer != null) {
                writer.dispose();
            }
            if (ios != null) {
                ios.close();
            }
            if (os != null) {
                os.close();
            }
            if (outputImage != null) {
                outputImage.flush();
            }
        }
    }

    private ImageWriter getImageWriterByFormat(String outputPathName) {
        String extension = getExtension(outputPathName);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(extension);
        return writers.next();
    }

    public int getWidth(String inputImagePath) throws Exception {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = null;
        int width;
        try {
            inputImage = getBufferedImage(inputFile);
            width = inputImage.getWidth();
        } finally {
            if (inputImage != null) {
                inputImage.flush();
            }
        }
        return width;
    }

    public Pair<Integer, Integer> getWidthHeight(String inputImagePath) throws Exception {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = null;
        int width;
        int height;
        try {
            inputImage = getBufferedImage(inputFile);
            width = inputImage.getWidth();
            height = inputImage.getHeight();
        } finally {
            if (inputImage != null) {
                inputImage.flush();
            }
        }
        return new Pair<>(width, height);
    }

    public int[][] getRgbData(String inputImagePath) throws Exception {
        File inputFile = new File(inputImagePath);
        int[][] rgbData;
        BufferedImage inputImage = null;
        try {
            inputImage = getBufferedImage(inputFile);
            int inputWidth = inputImage.getWidth();
            int inputHeight = inputImage.getHeight();
            rgbData = new int[inputWidth][inputHeight];
            for (int i = 0; i < inputWidth; i++) {
                for (int j = 0; j < inputHeight; j++) {
                    rgbData[i][j] = inputImage.getRGB(i, j);
                }
            }
        } finally {
            if (inputImage != null) {
                inputImage.flush();
            }
        }
        return rgbData;
    }
}
