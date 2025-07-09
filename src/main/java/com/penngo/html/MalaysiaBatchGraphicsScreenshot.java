package com.penngo.html;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Jae
 * @create 2024 - 04 - 12 9:10
 */

public class MalaysiaBatchGraphicsScreenshot {
    public static void main(String[] args) {
        // 记录起始时间
        long l = System.currentTimeMillis();

        String filePath = "E:\\Jae\\sibu\\需求1\\马来西亚\\WM-ENR-3.3-en-MS.html"; // 替换HTML文件路径

        try {
            File input = new File(filePath);
            Document document = Jsoup.parse(input, "UTF-8"); // 解析HTML文件

            int pageIndex = 0;

            while (true) {
                // 选择id为"page_" + pageIndex的元素
                Element pageElement = document.getElementById("page_" + pageIndex);
                if (pageElement == null) {
                    break; // 如果找不到具有该ID的元素，则退出循环
                }

                // 选择pageElement中的所有image标签
                Elements imageElements = pageElement.select("img");

                int imageIndex = 1; // 初始化图像索引

                for (Element imageElement : imageElements) {

                    // 检查当前页面的图像数量是否大于20
                    if (imageElements.size() > 20) {
                        System.out.println("Page " + pageIndex + " Image " + " skipped due to limit.");
                        break; // continue是跳过当前图像，break是跳出当前页；
                    } else {
                        // 获取图像的style属性
                        String imageStyle = imageElement.attr("style");

                        // 从style属性中提取left、top、width和height值
                        String imageLeft = extractValue(imageStyle, "left");
                        String imageTop = extractValue(imageStyle, "top");
                        String imageWidth = extractValue(imageStyle, "width");
                        String imageHeight = extractValue(imageStyle, "height");

                        // 输出图像坐标和尺寸
                        System.out.println("Page " + pageIndex + " Image " + imageIndex + " Left:" + imageLeft);
                        System.out.println("Page " + pageIndex + " Image " + imageIndex + " Top:" + imageTop);
                        System.out.println("Page " + pageIndex + " Image " + imageIndex + " Width:" + imageWidth);
                        System.out.println("Page " + pageIndex + " Image " + imageIndex + " Height:" + imageHeight);

                        // 调用screenShot()方法，传入图像的坐标和尺寸
                        screenShot(pageIndex, imageIndex, imageLeft, imageTop, imageWidth, imageHeight);
                    }

                    // 递增图像索引
                    imageIndex++;
                }

                // 递增页面索引
                pageIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 记录结束时间
        long l1 = System.currentTimeMillis();
        System.out.println(" ===== end pdf parse success;" +
                "共耗时 " + (l1 - l) + " ms =====，" + (l1 - l) / 1000 + "s =====");
    }

    // 从style属性中提取指定属性值
    private static String extractValue(String style, String attribute) {
        String[] styleParts = style.split(";");
        for (String part : styleParts) {
            if (part.contains(attribute)) {
                String value = part.substring(part.indexOf(":") + 1).trim();
                if (value.endsWith("pt")) {
                    return value.substring(0, value.length() - 2); // 截去"pt"
                } else {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * 适用国家层级截图计算，不同国家使用的图不同，所以截取可能不完整，建议不同国家使用不同截图计算
     *
     * @param pageIndex
     * @param imageLeft
     * @param imageTop
     * @param imageWidth
     * @param imageHeight
     */
    private static void screenShot(int pageIndex, int imageIndex,
                                   String imageLeft, String imageTop, String imageWidth, String imageHeight) {

        try {
            // 1. 加载PDF文档
            PDDocument document = PDDocument.load(new File("E:\\Jae\\sibu\\需求1\\马来西亚\\WM-ENR-3.3-en-MS.pdf"));

            // 2. 创建PDF渲染器
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // 3. 获取每一页
//            int pageIndex = 0;
            PDPage page = document.getPage(pageIndex);

            // 4. 定义自定义坐标与系数

            boolean isScreenshotFlag = true;
            BufferedImage croppedImage;

            // 原坐标
            float left = Float.parseFloat(imageLeft);
            float top = Float.parseFloat(imageTop);
            float width = Float.parseFloat(imageWidth);
            float height = Float.parseFloat(imageHeight);

            // 不能低于300，最接近300的72倍数是360，为了提高分辨率，像素点是3的倍数，
            // 所以360即使3的倍数，也是72倍数，最合适
            // 所以马来西亚使用360人眼才看得清方形＋三角形，其实像素点是有的
            float dpi = 360f;
            float ptCommon = dpi / 72f;

            // 坐标计算系数【马来西亚】
            float leftCoefficient = -18f;
            float topCoefficient = -7f;
            float widthCoefficient = 38f;
            float heightCoefficient = 20f;

//          System.out.println(ptCommon);

            // 通用算法  imageLeft 值加上 imageWidth 值不超出图像的宽度
            float x = left * ptCommon + leftCoefficient; // 起始X坐标 系数：4.1666 === 加是往右；减是往左
            float y = top * ptCommon + topCoefficient; // 起始Y坐标 系数：4.1666 === 加是下移；减是上移

            // x与y最小值为0，`imageLeft` 值加上 `imageWidth` 值不超出图像的宽度
            if (x < 0 || y < 0) {
//                x = 0;// 可以截，但是缺角，原图也是缺角，left=0;top=4，会变成负数，所有强制最小为0
                System.out.println("Invalid image coordinates or dimensions. Skipping screenshot.");
                isScreenshotFlag = false;
            }

            float w = width * ptCommon + widthCoefficient; // 宽度 系数：4.1666
            float h = height * ptCommon + heightCoefficient; // 高度 系数：4.1666

            // 5. 渲染页面到BufferedImage
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, dpi,
                    ImageType.RGB);

            // 6. 通过自定义坐标剪切BufferedImage
            if (isScreenshotFlag) {
                croppedImage = bufferedImage.getSubimage((int) x, (int) y, (int) w, (int) h);
                // 7. 保存截图为PNG格式的图片文件
                File outputFile = new File("E:\\Jae\\sibu\\需求1\\马来西亚\\马来西亚所有图形截图\\马来西亚ENR3所有图形截图\\WM-ENR-3.3-en-MS所有页图形图片\\"
                        + "WM-ENR-3.3-en-MS" + "-Page" + pageIndex + " Image-" + imageIndex
                        + " imageLeft-" + imageLeft + " imageTop-" + imageTop + " imageWidth-" + imageWidth + " imageHeight-" + imageHeight /*+ UUID.randomUUID()*/ + ".png");
                ImageIO.write(croppedImage, "png", outputFile);
            }

            // 8. 关闭文档
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}