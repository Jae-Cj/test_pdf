package com.penngo.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

public class JsonToExcel {

    public static void main(String[] args) throws IOException {
        // 测试用的JSON字符串，你可以用实际的字符串来代替这个数据
//        String jsonStr = "[{\"data\": [[{\"text\": \"John\"}, {\"text\": \"Doe\"}], [{\"text\": \"Jane\"}, {\"text\": \"Smith\"}]]}]";
        String jsonStr = "[{\"data\": [[{\"text\": \"John\"}, {\"text\": \"Doe\"}], [{\"text\": \"Jane\"}, {\"text\": \"Smith\"}]]}]";

        json2Excel(jsonStr);
    }

    private static void json2Excel(String jsonStr) throws IOException {
        // 将JSON字符串转换为JSONArray
        JSONArray jsonArray = new JSONArray(jsonStr);

        // 创建一个新的Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建一个工作表sheet
        Sheet sheet = workbook.createSheet();

        // 解析JSONArray并将数据写入Excel
        for (int i = 0; i < jsonArray.length(); i++) {
            // 获取数组中的JSONObject
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            // 解析内层JSONArray
            for (int rowIndex = 0; rowIndex < dataArray.length(); rowIndex++) {
                JSONArray rowData = dataArray.getJSONArray(rowIndex);
                // 创建新的row
                Row row = sheet.createRow(rowIndex);

                // 在row中创建cells并填充数据
                for (int cellIndex = 0; cellIndex < rowData.length(); cellIndex++) {
                    JSONObject cellData = rowData.getJSONObject(cellIndex);
                    String text = cellData.getString("text");
                    // 替换掉换行符，以便格式更加清晰
                    text = text.replace("\r", " ").replace("\n", " ");
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(text);
                }
            }
        }

        // 写入数据到工作簿
        FileOutputStream fileOut = new FileOutputStream("E:\\Jae\\sibu\\需求1\\印尼\\data.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // 关闭工作簿
        workbook.close();
    }
}