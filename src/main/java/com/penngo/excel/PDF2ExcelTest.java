package com.penngo.excel;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jae
 * @create 2024 - 04 - 18 16:19
 */
public class PDF2ExcelTest {

    public static void main(String[] args) {

        getTableInfo();
    }

    public static void getTableInfo() {
        InputStream in = null;
        try {
            in = new FileInputStream(new File("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (PDDocument document = PDDocument.load(in)) {
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();
            while (pi.hasNext()) {
                Page page = pi.next();
                List<Table> tables = sea.extract(page);
                // fix 相比于官网的示例，这里多了一步操作，进行了一下去重。
                tables = tables.stream().distinct().collect(Collectors.toList());
                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();
                    for (List<RectangularTextContainer> cells : rows) {
                        for (RectangularTextContainer content : cells) {
                            System.out.print(content.getText().replace("\r", " ") + "|");
                        }
                        System.out.println();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
