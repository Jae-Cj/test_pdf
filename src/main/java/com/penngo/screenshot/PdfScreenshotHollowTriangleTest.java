package com.penngo.screenshot;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * 空心三角形
 * @author Jae
 * @create 2024 - 04 - 09 16:35
 */
public class PdfScreenshotHollowTriangleTest {

    private final static Logger logger = Logger.getLogger(PdfScreenshotHollowTriangleTest.class.getName());

    public static void main(String[] args) {
        try {
            // 1. 加载PDF文档
            PDDocument document = PDDocument.load(new File("E:\\Jae\\sibu\\需求1\\泰国\\VT-ENR-3.1-en-GB.pdf"));

            // 2. 创建PDF渲染器
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // 3. 获取第一页
            int pageIndex = 0;
            PDPage page = document.getPage(pageIndex);

            // 4. 定义自定义坐标
            float dpi = 300f;
            float ptCommon = dpi / 72f;
            System.out.println(ptCommon);

            // 原坐标
            float left = 74f;
            float top = 404f;
            float width = 7f;
            float height = 6f;

            // 坐标计算系数
            float leftCoefficient = 4f;
            float topCoefficient = 2f;
            float widthCoefficient = 8f;
            float heightCoefficient = 11f;

            // 第一页第一个空心三角形算法
            float x = left * ptCommon - leftCoefficient; // 起始X坐标 系数：4.1666 加是往右；减是往左
            float y = top * ptCommon - topCoefficient; // 起始Y坐标 系数：4.1666 加是下移；减是上移
            float w = width * ptCommon + widthCoefficient; // 宽度 系数：4.1666
            float h = height * ptCommon + heightCoefficient; // 高度 系数：4.1666

            // 第一页第二个黑心三角形算法【会往右偏移，比较特殊，但是应该足够ai模型识别】
//            float startX = 62.35f * ptCommon; // 起始X坐标 系数：4.1666
//            float startY = 206.68f * ptCommon - 2f; // 起始Y坐标 系数：4.1666
//            float width = 6.25f * ptCommon + 7f; // 宽度 系数：4.1666
//            float height = 5.75f * ptCommon + 10f; // 高度 系数：4.1666

            // 5. 渲染页面到BufferedImage
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, dpi,
                    org.apache.pdfbox.rendering.ImageType.RGB);

            // 6. 通过自定义坐标剪切BufferedImage
            BufferedImage croppedImage = bufferedImage.getSubimage((int) x, (int) y, (int) w, (int) h);

            // 7. 保存截图为PNG格式的图片文件
            File outputFile = new File("E:\\Jae\\sibu\\需求1\\泰国\\空心三角形\\" + "VT-ENR-3.1-en-GB" + "第一页第一个空心三角形" + UUID.randomUUID() +".png");
            ImageIO.write(croppedImage, "png", outputFile);

            // 8. 关闭文档
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
