package com.penngo.tabula;

/**
 * @author Jae
 * @create 2024 - 04 - 07 15:56
 */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TabulaMultiPageToExcel {
    public static void main(String[] args) {
        try {
            // 读取PDF文件
//            File pdfFile = new File("E:\\Jae\\sibu\\需求1\\导航数据解析文档\\PDF源文档\\空域\\96-ENR 2.1-印度尼西亚.pdf");
            File pdfFile = new File("E:\\春水殿_成人之美\\杜蕾斯\\杜蕾斯订单详情预览-东莞瑞佳医疗科技有限公司.pdf");

            // 创建PDDocument对象
            PDDocument document = PDDocument.load(pdfFile);

            // 创建ObjectExtractor对象
            ObjectExtractor extractor = new ObjectExtractor(document);

            // 创建一个新的Excel工作簿
            Workbook workbook = new XSSFWorkbook();

            // 循环处理每一页
            for (int pageNum = 1; pageNum <= document.getNumberOfPages(); pageNum++) {
                // 提取当前页的表格数据
                Page currentPage = extractor.extract(pageNum);
                BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
                List<Table> tables = bea.extract(currentPage);

                // 创建一个工作表
                Sheet sheet = workbook.createSheet("Page " + pageNum);

                // 循环处理每个提取的表格
                int rowIndex = 0;
                for (Table table : tables) {
                    // 创建行
                    Row row = sheet.createRow(rowIndex);
                    // 循环处理每个表格的行和列
                    for (int i = 0; i < table.getRowCount(); i++) {
                        List<RectangularTextContainer> rows = table.getRows().get(i);
                        // 创建一个新的Excel行
                        Row excelRow = sheet.createRow(rowIndex);
                        // 循环处理每个单元格
                        for (int j = 0; j < rows.size(); j++) {
                            // 获取单元格数据并写入到Excel表格中
                            Cell cell = excelRow.createCell(j);
                            cell.setCellValue(rows.get(j).getText());
                        }
                        // 增加行索引
                        rowIndex++;
                    }
                }
            }

            // 将Excel数据写入到输出文件
//            FileOutputStream fileOut = new FileOutputStream("E:\\Jae\\sibu\\需求1\\导航数据解析文档\\PDF源文档\\空域\\96-ENR 2.1-印度尼西亚.xlsx");
            FileOutputStream fileOut = new FileOutputStream("E:\\春水殿_成人之美\\杜蕾斯\\杜蕾斯订单详情预览-东莞瑞佳医疗科技有限公司.xlsx");
            workbook.write(fileOut);
            fileOut.close();

            // 关闭Extractor
            extractor.close();

            // 关闭PDDocument
            document.close();

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}