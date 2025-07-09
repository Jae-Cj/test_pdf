package com.penngo.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

/**
 * @author Jae
 * @create 2024 - 04 - 09 13:48
 */
public class PdfScreenshot {
    public static void main(String[] args) {
        try {
            // 1. 加载PDF文档
            PDDocument document = PDDocument.load(new File("E:\\\\Jae\\\\sibu\\\\需求1\\\\印尼\\\\98-ENR 3.1.pdf"));

            // 2. 获取PDF页面大小
            PDPage page = document.getPage(0);
            int pageWidth = (int) page.getMediaBox().getWidth();
            int pageHeight = (int) page.getMediaBox().getHeight();

            // 3. 创建PDF渲染器
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // 4. 定义自定义坐标
            float startX = 60; // 起始X坐标 像素
            float startY = 169; // 起始Y坐标 像素
            float width = 5; // 宽度 像素
            float height = 4; // 高度 像素

            // 5. 将像素坐标转换为pt坐标
            startX *= 4;
            startY *= 4;
            width *= 4;
            height *= 4;

            // 6. 渲染页面到BufferedImage
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300,
                    org.apache.pdfbox.rendering.ImageType.RGB);

            // 7. 通过自定义坐标剪切BufferedImage
            BufferedImage croppedImage = bufferedImage.getSubimage((int) startX, (int) startY, (int) width, (int) height);

            // 8. 保存截图为PNG格式的图片文件
            File outputFile = new File("E:\\\\Jae\\\\sibu\\\\需求1\\\\印尼\\\\98-ENR 3.1" + UUID.randomUUID() + ".png");
            ImageIO.write(croppedImage, "png", outputFile);

            // 9. 关闭文档
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
