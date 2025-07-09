package com.penngo.converter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 三角形匹配对应文字（已知三角形是否实心空心和坐标）
 * 识别后返回第几页和三角形坐标
 * @author Jae
 * @create 2024 - 04 - 19 15:38
 */
public class TriangleHtmlCoordinatesConverter {

    public static void main(String[] args) {

        // 记录起始时间
        long l = System.currentTimeMillis();
        String htmlPath = "E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.html";
        try {
            //读取url，得到Document
            File input = new File(htmlPath);
            Document document = Jsoup.parse(input, "UTF-8"); // 解析HTML文件
            int pageIndex = 0;
            // 选择id为"page_" + pageIndex的元素
            Element pageElement = document.getElementById("page_" + pageIndex);
            // 选择pageElement中的所有image标签
            Elements imageElements = pageElement.select("img");
            // .是class,#是id，默认是标签
            // 选择pageElement中的所有p标签
            Elements textElements = pageElement.select(".p");

//            System.out.println("textElements："+textElements);

            List<ElementDTO> imageElementDTOList = new ArrayList<>();

            // 获取图形坐标集合
            for (Element imageElement : imageElements) {
                ElementDTO imageElementDTO = new ElementDTO();
                String id = imageElement.attr("id");
                String imageStyle = imageElement.attr("style");
                String data = imageElement.attr("value");
                imageElementDTO.setId(id);
                imageElementDTO.setStyle(imageStyle);
                imageElementDTO.setValue(data);

//                System.out.println("imageStyle：" + imageStyle);
                imageElementDTOList.add(imageElementDTO);
//                System.out.println("imgTop：" + imgTop);

            }

            List<ImgElementAbsoluteCoordinates> imageCoordinatesList = extractImageCoordinates(imageElementDTOList);

//            System.out.println("imageCoordinatesList：" + imageCoordinatesList);

            System.out.println("---------------------------------------------------------------------------");

            // 获取文本坐标集合
            List<ElementDTO> textElementDTOList = new ArrayList<>();
            for (Element textElement : textElements) {

                ElementDTO elementDTO = new ElementDTO();
                String id = textElement.attr("id");
                String textStyle = textElement.attr("style");
                String text = textElement.childNode(0).toString();
                elementDTO.setId(id);
                elementDTO.setStyle(textStyle);
                elementDTO.setValue(text);
                textElementDTOList.add(elementDTO);
            }
            List<TextElementAbsoluteCoordinates> textCoordinatesList = extractTextCoordinates(textElementDTOList);
//            System.out.println("textCoordinatesList：" + textCoordinatesList);

            System.out.println("---------------------------------------------------------------------------");

            // 找到三角形匹配文字
            ArrayList<ImgElementAbsoluteCoordinates> imageTextCoordinatesList = new ArrayList<>();

            // 遍历 imageCoordinatesList
            for (ImgElementAbsoluteCoordinates imageCoordinate : imageCoordinatesList) {
                double imageX = Double.parseDouble(imageCoordinate.getX());
                double imageY = Double.parseDouble(imageCoordinate.getY());
                double imageW = Double.parseDouble(imageCoordinate.getW());
                // 遍历 textCoordinatesList
                for (TextElementAbsoluteCoordinates textCoordinate : textCoordinatesList) {
                    double textX = Double.parseDouble(textCoordinate.getX());
                    double textY = Double.parseDouble(textCoordinate.getY());
                    String textVal = textCoordinate.getVal();

                    // 检查三角形 x 和 y 值的绝对差是否不超过 --> 文字和图片的x距离取值范围：6-8,最大值取20都可以，因pdf而异
                    if (Math.abs(imageX + imageW - textX) <= 8 && Math.abs(imageY - textY) <= 2) {

                        // 创建一个新的 ImageTextCoordinates 对象并将其添加到列表中
                        String imageXStr = String.valueOf(imageX);
                        String imageWStr = String.valueOf(imageW);
                        String imageYStr = String.valueOf(imageY);
                        ImgElementAbsoluteCoordinates imageTextCoordinate = new ImgElementAbsoluteCoordinates();
                        imageTextCoordinate.setX(imageXStr);
                        imageTextCoordinate.setW(imageWStr);
                        imageTextCoordinate.setY(imageYStr);
                        imageTextCoordinate.setVal(textVal);
                        imageTextCoordinatesList.add(imageTextCoordinate);

                        break; // 在找到匹配项后，跳出内部循环
                    }
                }
            }

            // TODO 进行任何额外的验证或检查 （可以校验截图的坐标是否准确，如imageCoordinatesList和已经识别后传过来的三角形集合）
            System.out.println("imageTextCoordinatesList匹配数：" + imageTextCoordinatesList.size());
            // 打印匹配文字结果的 imageTextCoordinatesList
            for (ImgElementAbsoluteCoordinates imageTextCoordinate : imageTextCoordinatesList) {
                System.out.println("imageTextCoordinate：x=" + imageTextCoordinate.getX()+"，w=" + imageTextCoordinate.getW() +"，y="+
                        imageTextCoordinate.getY() +"，val="+ imageTextCoordinate.getVal());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 记录结束时间
        long l1 = System.currentTimeMillis();
        System.out.println(" ===== end pdf parse success;" +
                "共耗时 " + (l1 - l) + " ms =====，" + (l1 - l) / 1000 + "s =====");

    }

    /**
     * 文本坐标集合
     *
     * @param elements
     * @return
     */
    public static List<TextElementAbsoluteCoordinates> extractTextCoordinates(List<ElementDTO> elements) {
        List<TextElementAbsoluteCoordinates> elementAbsoluteCoordinatesList = new ArrayList<>();
        for (ElementDTO element : elements) {
            String textStyle = element.getStyle();
            String topValue = extractValue(textStyle, "top");
            String leftValue = extractValue(textStyle, "left");

            String value = element.getValue();
//            textCoordinatesList.add(new TextCoordinates(topValue, leftValue, element.getValue()));
            TextElementAbsoluteCoordinates elementAbsoluteCoordinates = new TextElementAbsoluteCoordinates();
            elementAbsoluteCoordinates.setX(leftValue);
            elementAbsoluteCoordinates.setY(topValue);
            elementAbsoluteCoordinates.setVal(value);
            elementAbsoluteCoordinatesList.add(elementAbsoluteCoordinates);
        }
        return elementAbsoluteCoordinatesList;
    }

    /**
     * 图形坐标集合
     *
     * @param elements
     * @return
     */
    public static List<ImgElementAbsoluteCoordinates> extractImageCoordinates(List<ElementDTO> elements) {
        List<ImgElementAbsoluteCoordinates> imageCoordinatesList = new ArrayList<>();
        for (ElementDTO element : elements) {
            String imageStyle = element.getStyle();
            String topValue = extractValue(imageStyle, "top");
            String leftValue = extractValue(imageStyle, "left");
            String widthValue = extractValue(imageStyle, "width");
//            String value = element.getValue();
//            imageCoordinatesList.add(new TextCoordinates(topValue, leftValue, element.getValue()));
            ImgElementAbsoluteCoordinates imageCoordinates = new ImgElementAbsoluteCoordinates();
            imageCoordinates.setX(leftValue);
            imageCoordinates.setW(widthValue);
            imageCoordinates.setY(topValue);
//            elementCoordinates.setVal(value);
            imageCoordinatesList.add(imageCoordinates);
        }
        return imageCoordinatesList;
    }

    public static String extractValue(String style, String key) {
        int startIndex = style.indexOf(key) + key.length() + 1;
        int endIndex = style.indexOf("pt;", startIndex);
        return style.substring(startIndex, endIndex);
    }
}
