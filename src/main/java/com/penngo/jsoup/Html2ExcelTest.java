package com.penngo.jsoup;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Jae
 * @create 2024 - 04 - 25 9:57
 */
public class Html2ExcelTest {

    public static void main(String[] args) {

        // 记录起始时间
        long l = System.currentTimeMillis();
        HtmlToExcelUtils htmlToExcelUtils = new HtmlToExcelUtils();


        String filePath = "E:\\Jae\\sibu\\需求1\\all\\ENR\\亚洲压缩包\\印尼_ENR-202404162002390648\\ENR 2\\111.html"; // 替换HTML文件路径
        File input = new File(filePath);
        try {
            HSSFWorkbook wb = readHtmlStr(input);
            String excelPath = "E:\\Jae\\sibu\\需求1\\all\\ENR\\亚洲压缩包\\印尼_ENR-202404162002390648\\ENR 2\\96-ENR-2.1-Json.xlsx"; // 指定的路径和文件名
            wb.write(new FileOutputStream(excelPath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 记录结束时间
        long l1 = System.currentTimeMillis();
        System.out.println(" ===== end pdf parse success;" +
                "共耗时 " + (l1 - l) + " ms =====，" + (l1 - l) / 1000 + "s =====");
    }

    private static HSSFWorkbook readHtmlStr(File input) {

        return null;
    }


}
