package com.penngo.pdf;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * @author Jae
 * @create 2024 - 04 - 08 14:05
 */
public class PdfImageExtractorOpenCVTest {

    private final static Logger LOGGER = Logger.getLogger(PdfImageExtractorOpenCVTest.class.getName());

    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.56.10:3306/test";
        String username = "root";
        String password = "root";

        // 建立数据库连接
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // 创建数据库表
            String createTableSQL = "CREATE TABLE IF NOT EXISTS images (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "page_number INT," +
                    "image_data LONGBLOB" +
                    ")";
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }

            File file = new File("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf");
            PDDocument document = PDDocument.load(file);
            int imageNum = 1;

            String sql = "INSERT INTO images (page_number, image_data) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                int pageNumber = 1;
                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    PDPage page = document.getPage(i);
                    PDResources pdResources = page.getResources();
                    for (COSName cosName : pdResources.getXObjectNames()) {
                        if (pdResources.isImageXObject(cosName)) {
                            PDImageXObject image = (PDImageXObject) pdResources.getXObject(cosName);

                            try {
                                BufferedImage bufferedImage = image.getImage();
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                                Mat mat = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_UNCHANGED);

                                // Processes the image using OpenCV
                                // ...

                                Rect roi = new Rect(); // Replace with the calculated region of interest

                                Mat croppedImage = new Mat(mat, roi);
                                byte[] croppedImageBytes = new byte[(int) (croppedImage.total() * croppedImage.channels())];
                                croppedImage.get(0, 0, croppedImageBytes);

                                // Insert the cropped image into the database
                                pstmt.setInt(1, pageNumber);
                                pstmt.setBytes(2, croppedImageBytes);
                                pstmt.addBatch();

                                // Logging image information
                                LOGGER.info("Inserted image for page " + pageNumber + " to database");

                                // Commit the batch after a certain number of images
                                if (imageNum % 1000 == 0) {
                                    pstmt.executeBatch();
                                    pstmt.clearBatch();
                                }

                                imageNum++;

                            } catch (Exception e) {
                                LOGGER.warning("Error processing image for page " + pageNumber + ": " + e.getMessage());
                            }
                        }
                    }
                    pageNumber++;
                }

                // Execute the final batch
                pstmt.executeBatch();
            }

            document.close();
        } catch (Exception e) {
            LOGGER.severe("An error occurred: " + e.getMessage());
        }
    }

}
