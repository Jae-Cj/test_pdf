package com.penngo.excel;

import com.alibaba.excel.EasyExcel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jae
 * @create 2024 - 04 - 18 9:53
 */
public class HtmlToExcelConverterENR {


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
            Elements textElements = pageElement.select(".p");
            System.out.println(textElements);
            for (Element imageElement : imageElements) {
                String imageStyle = imageElement.attr("style");
                System.out.println("imageStyle：" + imageStyle);
                String imgTop = extractTopValue(imageStyle);
                System.out.println("imgTop：" + imgTop);

            }

            System.out.println("---------------------------------------------------------------------------");

            for (Element textElement : textElements) {

                String textStyle = textElement.attr("style");
                System.out.println("textStyle：" + textStyle);
                String textTop = extractTopValue(textStyle);
                System.out.println("textTop：" + textTop);

            }

//            Elements select = document.select(".r");

            List<WealthEntity> list = new ArrayList<>();


            for (int i = 1; i < imageElements.size(); i++) {
                Element tr = imageElements.get(i);
                Elements tds = tr.select("td");
                Integer index = Integer.valueOf(tds.get(0).text());
                String companyName = tds.get(1).text();
                String income = tds.get(2).text();
                String profit = tds.get(3).text();
                WealthEntity wealthEntity = WealthEntity.builder()
                        .index(index)
                        .companyName(companyName)
                        .income(income)
                        .profit(profit)
                        .build();
                list.add(wealthEntity);
            }
            String fileName = "E:\\Jae\\sibu\\需求1\\印尼\\101-ENR-3.4-1.xlsx";
            EasyExcel.write(fileName, WealthEntity.class).sheet("100强").doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 记录结束时间
        long l1 = System.currentTimeMillis();
        System.out.println(" ===== end pdf parse success;" +
                "共耗时 " + (l1 - l) + " ms =====，" + (l1 - l) / 1000 + "s =====");

    }
    public static String extractTopValue(String labelStyle) {
        String topValue = null;

        // 寻找top属性的起始位置
        int topIndex = labelStyle.indexOf("top:");
        if (topIndex != -1) {
            // 找到top属性的起始位置后，查找分号的位置作为值的结束位置
            int semicolonIndex = labelStyle.indexOf(";", topIndex);
            if (semicolonIndex != -1) {
                // 提取top属性值，并去除单位pt
                topValue = labelStyle.substring(topIndex + 4, semicolonIndex).replaceAll("pt", "");
            }
        }

        return topValue;
    }
}
