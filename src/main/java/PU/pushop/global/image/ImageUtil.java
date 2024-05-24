package PU.pushop.global.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class ImageUtil {

    // 이미지 크기 줄이기
    public static String resizeImageFile(MultipartFile file, String filePath, String formatName) throws IOException {
        long startTime = System.currentTimeMillis(); // 시작 시간 기록

        BufferedImage inputImage = ImageIO.read(file.getInputStream());
        int originWidth = inputImage.getWidth();
        int originHeight = inputImage.getHeight();
        int newWidth = 500;

        File outputFile = new File(filePath);

        if (originWidth > newWidth) {
            int newHeight = (originHeight * newWidth) / originWidth;
            Image resizeImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newImage.createGraphics();
            g2d.drawImage(resizeImage, 0, 0, null);
            g2d.dispose();

            // 임시 파일에 이미지를 저장하고 파일 크기를 확인
            ImageIO.write(newImage, formatName, outputFile);

            // 초기 파일 크기가 2MB를 초과하면 품질을 조정하여 용량 절반으로 줄이기
            if (outputFile.length() > 2 * 1024 * 1024) {
                log.info("Initial file size is larger than 2MB, compressing...");

                // JPEG 품질 조정을 통해 파일 크기 줄이기
                if (formatName.equalsIgnoreCase("jpeg") || formatName.equalsIgnoreCase("jpg")) {
                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
                    if (writers.hasNext()) {
                        ImageWriter writer = writers.next();
                        ImageWriteParam param = writer.getDefaultWriteParam();
                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(0.5f); // 초기 품질 설정 50%

                        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
                            writer.setOutput(ios);
                            writer.write(null, new IIOImage(newImage, null, null), param);

                            // 파일 크기 확인 후 1MB 이하로 압축.
                            float quality = 0.5f;
                            while (outputFile.length() > 1024 * 1024 && quality > 0.2f) {
                                quality -= 0.2f;
                                param.setCompressionQuality(quality);
                                writer.write(null, new IIOImage(newImage, null, null), param);
                            }
                        }
                        writer.dispose();
                    }
                } else {
                    ImageIO.write(newImage, formatName, outputFile);
                }
            }
        } else {
            file.transferTo(outputFile);
        }

        long endTime = System.currentTimeMillis(); // 종료 시간 기록
        long duration = endTime - startTime; // 총 걸린 시간 계산

        log.info("Final file size: " + outputFile.length() + " bytes");
        log.info("Total time taken: " + duration + " ms");
        return outputFile.getName();
    }
}

