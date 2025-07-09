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
 * 箭头匹配对应文字（已知箭头朝向和坐标,已经完成了最近的第一根线匹配）
 * 识别后返回第几页和箭头坐标
 *
 * @author Jae
 * @create 2024 - 04 - 23 9:22
 */
public class ArrowPointsHtmlCoordinatesConverter {

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
            // 选择pageElement中的所有r标签【与传过来的箭头集合匹配，x相同的r线最接近箭头的；两根】
            Elements lineElements = pageElement.select(".r");
            ArrayList<Object> objects = new ArrayList<>();
//            System.out.println("textElements："+textElements);

            // 获取线条坐标集合
            List<ElementDTO> lineElementDTOList = new ArrayList<>();

            for (Element lineElement : lineElements) {
                ElementDTO lineElementDTO = new ElementDTO();
//                String id = lineElement.attr("id");
                String limeStyle = lineElement.attr("style");
                String data = lineElement.attr("value");
//                lineElementDTO.setId(id);
                lineElementDTO.setStyle(limeStyle);
                lineElementDTO.setValue(data);

                lineElementDTOList.add(lineElementDTO);

            }

            List<LineElementAbsoluteCoordinates> lineCoordinatesList = extractLineCoordinates(lineElementDTOList);
            // 测试数据
//            List<LineElementAbsoluteCoordinates> lineCoordinatesList = new ArrayList<>();
//            LineElementAbsoluteCoordinates lineElementAbsoluteCoordinates1 = new LineElementAbsoluteCoordinates();
//            lineElementAbsoluteCoordinates1.setX("319");
//            lineElementAbsoluteCoordinates1.setY("176");
//            lineElementAbsoluteCoordinates1.setW("20");
//
//            LineElementAbsoluteCoordinates lineElementAbsoluteCoordinates2 = new LineElementAbsoluteCoordinates();
//            lineElementAbsoluteCoordinates2.setX("0");
//            lineElementAbsoluteCoordinates2.setY("0");
//            lineElementAbsoluteCoordinates2.setW("0");
//
//            lineCoordinatesList.add(lineElementAbsoluteCoordinates1);
//            lineCoordinatesList.add(lineElementAbsoluteCoordinates2);
            // 获取图形坐标集合
//            List<ElementDTO> imageElementDTOList = new ArrayList<>();

//            for (Element imageElement : imageElements) {
//                ElementDTO imageElementDTO = new ElementDTO();
//                String id = imageElement.attr("id");
//                String imageStyle = imageElement.attr("style");
//                String data = imageElement.attr("value");
//                imageElementDTO.setId(id);
//                imageElementDTO.setStyle(imageStyle);
//                imageElementDTO.setValue(data);
//                imageElementDTOList.add(imageElementDTO);
//
//            }

//            List<ImgElementAbsoluteCoordinates> imageCoordinatesList = extractImageCoordinates(imageElementDTOList);
            // 已经获取到第一页的箭头朝上朝下和坐标
            List<ImgElementAbsoluteCoordinates> imageCoordinatesList = new ArrayList<>();
            ImgElementAbsoluteCoordinates imageCoordinates1 = new ImgElementAbsoluteCoordinates();
            imageCoordinates1.setX("306");
            imageCoordinates1.setY("505");
            imageCoordinates1.setW("6");
            imageCoordinates1.setH("25");
            imageCoordinates1.setVal("朝上");

            ImgElementAbsoluteCoordinates imageCoordinates2 = new ImgElementAbsoluteCoordinates();
            imageCoordinates2.setX("325");
            imageCoordinates2.setW("6");
            imageCoordinates2.setY("284");
            imageCoordinates2.setH("27");
            imageCoordinates2.setVal("朝下");

            ImgElementAbsoluteCoordinates imageCoordinates3 = new ImgElementAbsoluteCoordinates();
            imageCoordinates3.setX("326");
            imageCoordinates3.setW("6");
            imageCoordinates3.setY("177");
            imageCoordinates3.setH("26");
            imageCoordinates3.setVal("朝下");

            imageCoordinatesList.add(imageCoordinates1);
            imageCoordinatesList.add(imageCoordinates2);
            imageCoordinatesList.add(imageCoordinates3);

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

            // 找到朝下箭头匹配最近的起点r线坐标
            arrowPointsDownMatchLine(lineCoordinatesList, imageCoordinatesList);
            System.out.println("---------------------------------------------------------------------------");
            // 找到朝上箭头匹配最近的起点r线坐标
            arrowPointsUpMatchLine(lineCoordinatesList, imageCoordinatesList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 记录结束时间
        long l1 = System.currentTimeMillis();
        System.out.println(" ===== end pdf parse success;" +
                "共耗时 " + (l1 - l) + " ms =====，" + (l1 - l) / 1000 + "s =====");

    }

    /**
     * 找到朝上箭头匹配最近的起点r线坐标
     * @param lineCoordinatesList
     * @param imageCoordinatesList
     */
    private static void arrowPointsUpMatchLine(List<LineElementAbsoluteCoordinates> lineCoordinatesList,
                                               List<ImgElementAbsoluteCoordinates> imageCoordinatesList) {
        List<ArrowPointsMatchLineAbsoluteCoordinates> arrowPointsMatchLineAbsoluteCoordinatesList = new ArrayList<>();

        // 遍历 LineCoordinatesList
        for (LineElementAbsoluteCoordinates lineCoordinate : lineCoordinatesList) {
            double lineX = Double.parseDouble(lineCoordinate.getX());
            double lineY = Double.parseDouble(lineCoordinate.getY());
            double lineW = Double.parseDouble(lineCoordinate.getW());
            // 遍历 imgCoordinatesList
            for (ImgElementAbsoluteCoordinates imgCoordinate : imageCoordinatesList) {
                double imageX = Double.parseDouble(imgCoordinate.getX());
                double imageY = Double.parseDouble(imgCoordinate.getY());
                // 箭头朝上需要imageH
                double imageH = Double.parseDouble(imgCoordinate.getH());
                    double imageW = Double.parseDouble(imgCoordinate.getW());
//                    String textVal = imgCoordinate.getVal();

                // 检查箭头 x 值的绝对差是否不超过 --> 文字和图片的x距离取值范围：6-8,最大值取20都可以，因pdf而异
                // 注意：前端所有的绝对位置坐标左上角为起点
                if (Math.abs(imageY + imageH - lineY) <= 5 && Math.abs(imageX - lineX) <= 15 && lineW > 1 && lineW < 25 && lineX < imageX) {
                    // 创建一个新的 lineImageCoordinates 对象并将其添加到列表中
                    String imageXStr = String.valueOf(imageX);
                    String imageWStr = String.valueOf(imageW);
                    String imageYStr = String.valueOf(imageY);
                    ArrowPointsMatchLineAbsoluteCoordinates arrowPointsMatchLineAbsoluteCoordinates = new ArrowPointsMatchLineAbsoluteCoordinates();
                    arrowPointsMatchLineAbsoluteCoordinates.setX(imageXStr);
                    arrowPointsMatchLineAbsoluteCoordinates.setW(imageWStr);
                    arrowPointsMatchLineAbsoluteCoordinates.setY(imageYStr);
                    arrowPointsMatchLineAbsoluteCoordinates.setLineElementAbsoluteCoordinates(lineCoordinate);
                    arrowPointsMatchLineAbsoluteCoordinatesList.add(arrowPointsMatchLineAbsoluteCoordinates);

                    break; // 在找到匹配项后，跳出内部循环
                }

            }
        }

        // TODO 进行任何额外的验证或检查 （可以校验截图的坐标是否准确，如imageCoordinatesList和已经识别后传过来的三角形集合）
        System.out.println("箭头朝上匹配数最近的第一条线【为起点】：" + arrowPointsMatchLineAbsoluteCoordinatesList.size());
        // 打印匹配线坐标
        for (ArrowPointsMatchLineAbsoluteCoordinates arrowPointsImageCoordinate : arrowPointsMatchLineAbsoluteCoordinatesList) {
            System.out.println("箭头匹配坐标：x=" + arrowPointsImageCoordinate.getX() +
                    "，y=" + arrowPointsImageCoordinate.getY() +
                    "，w=" + arrowPointsImageCoordinate.getW() +
                    "，最近的第一根线条坐标：x=" + arrowPointsImageCoordinate.getLineElementAbsoluteCoordinates().getX() +
                    "，y=" + arrowPointsImageCoordinate.getLineElementAbsoluteCoordinates().getY() +
                    "，w=" + arrowPointsImageCoordinate.getLineElementAbsoluteCoordinates().getW()
            );
        }
    }

    /**
     * 找到朝下箭头匹配最近的起点r线坐标
     * @param lineCoordinatesList
     * @param imageCoordinatesList
     */
    private static void arrowPointsDownMatchLine(List<LineElementAbsoluteCoordinates> lineCoordinatesList,
                                                 List<ImgElementAbsoluteCoordinates> imageCoordinatesList) {

        List<ArrowPointsMatchLineAbsoluteCoordinates> arrowPointsMatchLineAbsoluteCoordinatesList = new ArrayList<>();

        // 遍历 LineCoordinatesList
        for (LineElementAbsoluteCoordinates lineCoordinate : lineCoordinatesList) {
            double lineX = Double.parseDouble(lineCoordinate.getX());
            double lineY = Double.parseDouble(lineCoordinate.getY());
            double lineW = Double.parseDouble(lineCoordinate.getW());
            // 遍历 imgCoordinatesList
            for (ImgElementAbsoluteCoordinates imgCoordinate : imageCoordinatesList) {
                double imageX = Double.parseDouble(imgCoordinate.getX());
                double imageY = Double.parseDouble(imgCoordinate.getY());
                // 箭头朝下不需要imageH
//                double imageH = Double.parseDouble(imgCoordinate.getH());
                double imageW = Double.parseDouble(imgCoordinate.getW());
//                String textVal = imgCoordinate.getVal();

                // 检查箭头 x 值的绝对差是否不超过 --> 线和图片的x距离取值范围：因pdf而异
                if ((Math.abs(imageY - lineY) <= 5 && Math.abs(imageX - lineX) <= 15 && lineW > 1 && lineW < 25)) {
                    // 创建一个新的 lineImageCoordinates 对象并将其添加到列表中
                    String imageXStr = String.valueOf(imageX);
                    String imageYStr = String.valueOf(imageY);
                    String imageWStr = String.valueOf(imageW);

                    // 最近的第一条
                    ArrowPointsMatchLineAbsoluteCoordinates arrowPointsMatchOneLineAbsoluteCoordinates = new ArrowPointsMatchLineAbsoluteCoordinates();
//                    ArrowPointsMatchLineAbsoluteCoordinates arrowPointsMatchLineAbsoluteCoordinates = new ArrowPointsMatchLineAbsoluteCoordinates();
                    arrowPointsMatchOneLineAbsoluteCoordinates.setX(imageXStr);
                    arrowPointsMatchOneLineAbsoluteCoordinates.setY(imageYStr);
                    arrowPointsMatchOneLineAbsoluteCoordinates.setW(imageWStr);
                    arrowPointsMatchOneLineAbsoluteCoordinates.setLineElementAbsoluteCoordinates(lineCoordinate);
                    arrowPointsMatchLineAbsoluteCoordinatesList.add(arrowPointsMatchOneLineAbsoluteCoordinates);

                    break; // 在找到匹配项后，跳出内部循环
                }


            }
        }

        // TODO 进行任何额外的验证或检查 （可以校验截图的坐标是否准确，如imageCoordinatesList和已经识别后传过来的三角形集合）
        System.out.println("箭头朝下匹配数最近的第一条线【为起点】：" + arrowPointsMatchLineAbsoluteCoordinatesList.size());
        // 打印匹配线坐标
        for (ArrowPointsMatchLineAbsoluteCoordinates arrowPointsImageCoordinate : arrowPointsMatchLineAbsoluteCoordinatesList) {
            System.out.println("箭头朝下的坐标：x=" + arrowPointsImageCoordinate.getX() +
                    "，y=" + arrowPointsImageCoordinate.getY() +
                    "，w=" + arrowPointsImageCoordinate.getW() +
                    "，最近的第一根线条坐标arrowPointsMatchOneLineAbsoluteCoordinates：x=" + arrowPointsImageCoordinate.getLineElementAbsoluteCoordinates().getX() +
                    "，y=" + arrowPointsImageCoordinate.getLineElementAbsoluteCoordinates().getY() +
                    "，w=" + arrowPointsImageCoordinate.getLineElementAbsoluteCoordinates().getW()
            );
        }
    }

    /**
     * 线条坐标集合
     *
     * @param elements
     * @return
     */
    private static List<LineElementAbsoluteCoordinates> extractLineCoordinates(List<ElementDTO> elements) {
        List<LineElementAbsoluteCoordinates> elementAbsoluteCoordinatesList = new ArrayList<>();
        for (ElementDTO element : elements) {
            String textStyle = element.getStyle();
            String topValue = extractValue(textStyle, "top");
            String leftValue = extractValue(textStyle, "left");
            String widthValue = extractValue(textStyle, "width");

//            String value = element.getValue();
//            textCoordinatesList.add(new TextCoordinates(topValue, leftValue, element.getValue()));
            LineElementAbsoluteCoordinates elementAbsoluteCoordinates = new LineElementAbsoluteCoordinates();
            elementAbsoluteCoordinates.setX(leftValue);
            elementAbsoluteCoordinates.setY(topValue);
            elementAbsoluteCoordinates.setW(widthValue);
            elementAbsoluteCoordinatesList.add(elementAbsoluteCoordinates);
        }
        return elementAbsoluteCoordinatesList;
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