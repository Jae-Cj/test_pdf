package com.penngo.pdf;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 用于提取PDF中的图片并保存为PNG文件
 * @author Jae
 * @create 2024 - 04 - 07 9:01
 */
public class PdfboxImageTest1 {

    public static void main(String[] args) throws IOException {
        // 用于计数提取的图片数量
        int count = 0;
        // 要读取的PDF文件路径
        File file = new File("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf");
        // 创建文件输入流
        FileInputStream fis = new FileInputStream(file);
        // 加载PDF文档
        PDDocument document = PDDocument.load(fis);
        // 获取PDF文档的总页数
        int allPages = document.getNumberOfPages();
        // 遍历每一页
        for (int i = 0; i < allPages; i++) {
            // 获取当前页面
            PDPage page = document.getPage(i);
            // 获取页面资源
            PDResources resources = page.getResources();
            // 获取页面中所有对象的名称
            Iterable<COSName> xObjectNames = resources.getXObjectNames();
            // 如果对象名称不为空
            if (xObjectNames != null){
                // 遍历页面中所有对象的名称
                for (COSName key : xObjectNames) {
                    // 如果对象为图像对象
                    if (resources.isImageXObject(key)) {
                        // 获取图像对象
                        PDImageXObject image = (PDImageXObject) resources.getXObject(key);
                        // 获取图像的BufferedImage
                        BufferedImage bImage = image.getImage();
                        // 将图像以PNG格式保存为新文件，文件名包括页码和计数
                        ImageIO.write(bImage, "PNG",
                                new File("E:\\Jae\\sibu\\需求1\\印尼\\" + "image_" + (i + 1) + "页" + count + ".png"));
                        count++;
                    }
                }
            }
        }
        // 关闭文档
        document.close();
    }


}
