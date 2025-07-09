package com.penngo.screenshot;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

/**
 * 箭头朝下  arrow-points-down
 * @author Jae
 * @create 2024 - 04 - 09 16:36
 */
public class PdfScreenshotArrowPointsDownTest {

    private final static Logger logger = Logger.getLogger(PdfScreenshotArrowPointsDownTest.class.getName());

    public static void main(String[] args) {
        try {
            // 1. 加载PDF文档
            PDDocument document = PDDocument.load(new File("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf"));

            // 2. 创建PDF渲染器
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // 3. 获取第一页
            int pageIndex = 0;
            PDPage page = document.getPage(pageIndex);

            // 4. 定义自定义坐标与系数

            // 原坐标
            float left = 326f;
            float top = 177f;
            float width = 6f;
            float height = 26f;

            float dpi = 300f;
            float ptCommon = dpi / 72f;

            // 坐标计算系数
            float leftCoefficient = 0f;
            float topCoefficient = 1f;
            float widthCoefficient = 7f;
            float heightCoefficient = 8f;

//          System.out.println(ptCommon);

            // 第一页第一个朝下箭头截图算法
            float x = left * ptCommon + leftCoefficient; // 起始X坐标 系数：4.1666
            float y = top * ptCommon + topCoefficient; // 起始Y坐标 系数：4.1666
            float w = width * ptCommon + widthCoefficient; // 宽度 系数：4.1666
            float h = height * ptCommon + heightCoefficient; // 高度 系数：4.1666

            // 5. 渲染页面到BufferedImage
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, dpi,
                    ImageType.RGB);

            // 6. 通过自定义坐标剪切BufferedImage
            BufferedImage croppedImage = bufferedImage.getSubimage((int) x, (int) y, (int) w, (int) h);

            // 7. 保存截图为PNG格式的图片文件
            File outputFile = new File("E:\\Jae\\sibu\\需求1\\印尼\\朝下箭头\\" + "98-ENR-3.1" + "BND-VOR-DME--TKG-VOR-DME" /*+ UUID.randomUUID()8*/ +".png");
            ImageIO.write(croppedImage, "png", outputFile);

            // 8. 关闭文档
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
