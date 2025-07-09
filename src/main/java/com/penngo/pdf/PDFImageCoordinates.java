package com.penngo.pdf;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

/**
 * @author Jae
 * @create 2024 - 04 - 11 16:34
 */
public class PDFImageCoordinates {
    public static void main(String[] args) {
        String filePath = "E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf"; // 替换为您的PDF文件路径

        try (PDDocument document = PDDocument.load(new File(filePath))) {
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                PDPage page = document.getPage(pageIndex);
                PDResources resources = page.getResources();
                int imageIndex = 0;

                for (COSName name : resources.getXObjectNames()) {
                    if (resources.isImageXObject(name)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(name);

                        // 获取图像坐标和尺寸
                        float imageLeft = image.getCOSStream().getFloat("Left");
                        float imageTop = image.getCOSStream().getFloat("Top");
                        float imageWidth = image.getWidth();
                        float imageHeight = image.getHeight();

                        // 示例输出图像坐标和尺寸，并加上注释
                        System.out.println("Page " + (pageIndex + 1) + " Image " + (imageIndex + 1) + " Left: " + imageLeft); // 左侧坐标
                        System.out.println("Page " + (pageIndex + 1) + " Image " + (imageIndex + 1) + " Top: " + imageTop); // 顶部坐标
                        System.out.println("Page " + (pageIndex + 1) + " Image " + (imageIndex + 1) + " Width: " + imageWidth); // 宽度
                        System.out.println("Page " + (pageIndex + 1) + " Image " + (imageIndex + 1) + " Height: " + imageHeight); // 高度
                        imageIndex++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
