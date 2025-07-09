package com.penngo.pdf;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

/**
 * @author Jae
 * @create 2024 - 04 - 08 10:21
 */
public class PdfImageExtractorDBTest {

    private final static Logger LOGGER = Logger.getLogger(PdfImageExtractorOpenCVTest.class.getName());

    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.56.10:3306/test";
        String username = "root";
        String password = "root";

        // 建立数据库连接
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // 创建数据库表
//            String createTableSQL = "CREATE TABLE IF NOT EXISTS images (" +
//                    "id INT AUTO_INCREMENT PRIMARY KEY," +
//                    "page_number INT," +
//                    "image_data LONGBLOB" +
//                    ")";
//            try (Statement statement = connection.createStatement()) {
//                statement.execute(createTableSQL);
//            }

            File file = new File("E:/Jae/sibu/需求1/泰国/VT-ENR-3.1-en-GB.pdf");
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

                            BufferedImage originalImage = image.getImage();
                            int x = 10; // 设置截取的左上角 x 坐标
                            int y = 11; // 设置截取的左上角 y 坐标
                            int width = originalImage.getWidth(); // 设置截取宽度
                            int height = originalImage.getHeight(); // 设置截取高度
                            BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);
                            System.out.println("width"+width);
                            System.out.println("height"+height);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(croppedImage, "png", baos);
                            byte[] imageBytes = baos.toByteArray();

                            // 插入数据库
                            pstmt.setInt(1, pageNumber);
                            pstmt.setBytes(2, imageBytes);
                            pstmt.addBatch();

                            // 每隔一定数量提交一次批处理
                            if (imageNum % 1000 == 0) {
                                pstmt.executeBatch();
                                pstmt.clearBatch();
                            }

                            imageNum++;
                        }
                    }
                    pageNumber++;
                }

                // 执行最后一次批处理
                pstmt.executeBatch();
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
