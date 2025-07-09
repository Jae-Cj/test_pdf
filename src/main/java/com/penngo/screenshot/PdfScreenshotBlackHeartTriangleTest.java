package com.penngo.screenshot;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

/**
 * 黑心三角形
 * @author Jae
 * @create 2024 - 04 - 08 10:01
 */
public class PdfScreenshotBlackHeartTriangleTest {

    private final static Logger logger = Logger.getLogger(PdfScreenshotBlackHeartTriangleTest.class.getName());

    public static void main(String[] args) {
        try {
            // 1. 加载PDF文档
            PDDocument document = PDDocument.load(new File("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf"));

            // 2. 创建PDF渲染器
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // 3. 获取第一页
            int pageIndex = 0;
            PDPage page = document.getPage(pageIndex);

            // 4. 自定义坐标
            float dpi = 300f;
            float ptCommon = dpi / 72f;
            System.out.println(ptCommon);

            // 原坐标
            float left = 61f;
            float top = 489f;
            float width = 5f;
            float height = 4f;

            // 坐标计算系数
            float leftCoefficient = 0f;
            float topCoefficient = 2f;
            float widthCoefficient = 8f;
            float heightCoefficient = 11f;

            // 第一页第一个黑心三角形算法
            float x = left * ptCommon + leftCoefficient; // 起始X坐标 系数：4.1666
            float y = top * ptCommon - topCoefficient; // 起始Y坐标 系数：4.1666
            float w = width * ptCommon + widthCoefficient; // 宽度 系数：4.1666
            float h = height * ptCommon + heightCoefficient; // 高度 系数：4.1666

            // 第一页第二个黑心三角形算法【会往右偏移，比较特殊，但是应该足够ai模型识别】
//            float startX = 62.35f * ptCommon; // 起始X坐标 系数：4.1666
//            float startY = 206.68f * ptCommon - 2f; // 起始Y坐标 系数：4.1666
//            float width = 6.25f * ptCommon + 7f; // 宽度 系数：4.1666
//            float height = 5.75f * ptCommon + 10f; // 高度 系数：4.1666

            // 第一页第一个朝下箭头算法
//            float startX = 326f * ptCommon; // 起始X坐标 系数：4.1666
//            float startY = 177f * ptCommon + 1f; // 起始Y坐标 系数：4.1666
//            float width = 6f * ptCommon + 7f; // 宽度 系数：4.1666
//            float height = 26f * ptCommon + 8f; // 高度 系数：4.1666

            // 第一页第三个黑心三角形
//            float startX = 252.1313f; // 起始X坐标 系数：4.1333
//            float startY = 1002.364f; // 起始Y坐标 系数：4.1420
//            float width = 30f; // 宽度 系数：6
//            float height = 30f; // 高度 系数：7.5

            // 5. 渲染页面到BufferedImage
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, dpi,
                    org.apache.pdfbox.rendering.ImageType.RGB);

            // 6. 通过自定义坐标剪切BufferedImage
            BufferedImage croppedImage = bufferedImage.getSubimage(
                    (int) x, (int) y, (int) w, (int) h);

            // 7. 保存截图为PNG格式的图片文件
            File outputFile = new File("E:\\Jae\\sibu\\需求1\\印尼\\黑心三角形\\" + "98-ENR-3.1" + "BAC-VOR-DME" /*+ UUID.randomUUID()8*/ +".png");
            ImageIO.write(croppedImage, "png", outputFile);

            // 8. 关闭文档
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
