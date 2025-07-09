package com.penngo.excel;

import com.alibaba.excel.EasyExcel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlToExcelConverter {

    public static void main(String[] args) {

        String url = "https://www.maigoo.com/news/3jcNODk3.html";
        try {
            //读取url，得到Document
            Document document = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .timeout(30000)
                    .header("referer","https://www.maigoo.com")
                    .get();
            Elements select = document.select("#t_container > div:eq(21) table tr");
            List<WealthEntity> list = new ArrayList<>();
            for (int i = 1; i < select.size(); i++) {
                Element tr = select.get(i);
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
            String fileName = "D:/2023财富世界100强.xlsx";
            EasyExcel.write(fileName,WealthEntity.class).sheet("100强").doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
