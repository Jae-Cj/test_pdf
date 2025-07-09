package com.penngo.jsoup;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jae
 * @create 2024 - 04 - 25 11:06
 */
public class HtmlToExcelUtils {

    public static void main(String[] args) {
        // 记录起始时间
        long l = System.currentTimeMillis();
        HtmlToExcelUtils htmlToExcelUtils = new HtmlToExcelUtils();


        String filePath = "E:\\Jae\\sibu\\需求1\\all\\ENR\\亚洲压缩包\\印尼_ENR-202404162002390648\\ENR 2\\111.html"; // 替换HTML文件路径
        File input = new File(filePath);
        try {
            HSSFWorkbook wb = htmlToExcelUtils.readHtmlStr(input);
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

    private static final long serialVersionUID = 4175158575304402752L;
    /**
     * 表格的列数
     */
    private int columnSize;
    /**
     * 表格的行数
     */
    private int rowSize;
    /**
     * 数据的行数，不含表头
     */
    private int rowSize_data;
    /**
     * 标题所占行数
     */
    private int rowSize_title = 2;
    /**
     * 表头所占行数
     */
    private int rowSize_header;
    /**
     * 工作表名称
     */
    private String sheetName;

    public HtmlToExcelUtils() {
        this.rowSize_title = rowSize_title;
        this.sheetName = sheetName;
    }

    /**
     * 使用jsoup解析html，得到表头数据List，表体数据String[][]
     *
     * @param htmlStr
     * @return
     * @throws Exception
     */
    public HSSFWorkbook readHtmlStr(File input) throws Exception {
        // 先读本地
//        Document doc = Jsoup.parseBodyFragment(htmlStr, "utf-8");
        Document doc = Jsoup.parse(input, "UTF-8"); // 解析HTML文件
        doc.select("input[type$=hidden]").remove();//删除所有input隐藏域
        doc.select("tr[style*=none]").remove();//删除隐藏的tr

        Elements captions = doc.getElementsByTag("caption");//查找表头
        String tableTitle = "";// 保存表头标题
        if (captions != null && captions.size() > 0) {
            Element caption = captions.get(0);
            tableTitle = caption.text();
        } else {
            rowSize_title = 0;//表示，没有表头
        }

        Elements trs_data = doc.getElementsByTag("tr");//获取所有tr
        rowSize = trs_data.size() + rowSize_title;//获取表格的行数，外加标题行数

        Elements theads = doc.getElementsByTag("thead");//表头thead标签

        List<Cells> finalHeaderList = new ArrayList();//存放推导完毕的正确数据

        List<Cells> dataList = new ArrayList();//表头1单元格List
        if (theads != null && theads.size() > 0) {//表示有表头
            Elements thead_trs = theads.get(0).getElementsByTag("tr");//表头中的tr
            rowSize_header = thead_trs.size();
            trs_data.removeAll(thead_trs);//移除表头中的的tr元素，trs中剩下数据行

            List<Cells> headerList = new ArrayList();//表头1单元格List
            //构造表头
            //将表头数据存到List中。x、y坐标从0开始
            //确定x坐标之1：横向推导
            int basicY_thead = rowSize_title;
            for (int i = 0; i < thead_trs.size(); i++) {
                Element thead_tr = thead_trs.get(i);
                Elements thead_tr_ths = thead_tr.getElementsByTag("th");
                for (int j = 0; j < thead_tr_ths.size(); j++) {
                    Element e = thead_tr_ths.get(j);
                    Cells td = new Cells();
                    td.setContent(e.text());
                    if (StringUtils.isNotBlank(e.attr("colspan"))) {
                        td.setColspan(Integer.valueOf(e.attr("colspan").trim()));
                    }
                    if (StringUtils.isNotBlank(e.attr("rowspan"))) {
                        td.setRowspan(Integer.valueOf(e.attr("rowspan").trim()));
                    }
                    td.setX(HorizontalDeduction_th(e));//步骤1：横向推导，但这个坐标并不是最终坐标，需要进行纵向推导
                    td.setY(i + basicY_thead);//y坐标很简单，就是tr的值
                    headerList.add(td);
                }
            }
            //确定x坐标之2：纵向推导
            finalHeaderList = verticalDeduction(headerList);

            if (trs_data.size() > 0) {//表示有表格内容数据
                rowSize_data = trs_data.size();
            } else {//表示只有表头数据，没有表格内容数据
                rowSize_data = 0;
            }

        } else {//表示没有表头
            rowSize_header = 0;
        }
        //循环每一个数据单元格
        int basicY_data = rowSize_title + rowSize_header;
        for (int i = 0; i < trs_data.size(); i++) {
            Element tr = trs_data.get(i);
            Elements tds = tr.getElementsByTag("td");
            //循环每一行的所有列
            for (int j = 0; j < tds.size(); j++) {
                Element e = tds.get(j);
                Elements inp = e.getElementsByTag("input");
                Cells td = new Cells();
                if (StringUtils.isNotBlank(e.attr("colspan"))) {
                    td.setColspan(Integer.valueOf(e.attr("colspan").trim()));
                }
                if (StringUtils.isNotBlank(e.attr("rowspan"))) {
                    if (StringUtils.isNotBlank(e.attr("rowspan"))) {
                        try {
                            td.setRowspan(Integer.valueOf(e.attr("rowspan").trim()));
                        } catch (NumberFormatException ex) {
                            td.setRowspan(1); // 如果 rowspan 为 "Infinity"，设置为 1
                        }
                    }
                }
                if (inp != null && inp.size() > 0) {//表示td中嵌套input
                    td.setContent(inp.get(0).val());
                } else {//表示td中没有嵌套input
                    td.setContent(e.text().trim());
                }
                td.setX(HorizontalDeduction_td(e));//步骤1：横向推导，但这个坐标并不是最终坐标，需要进行纵向推导
                td.setY(i + basicY_data);//y坐标很简单，就是tr的值
                dataList.add(td);
            }
        }
        //步骤2：纵向推导
        dataList = verticalDeduction(dataList);

        //表头和表内容合并为一个List
        finalHeaderList.addAll(dataList);

        //对列进行赋值，找到第一个单元格计算列宽
        Cells lastTd = finalHeaderList.get(finalHeaderList.size() - 1);
        columnSize = lastTd.getX() + lastTd.getColspan();

        int[][] contextSizeArr = new int[rowSize][columnSize];//记录内容单元格内容长度，便于进行列宽自适应调整
        String[][] dataArr = new String[rowSize_data][columnSize];
        //将表格的长度按照该单元格的位置填入字符串长度
        //不能使用普通下标方式赋值，因为如果有合并单元格的情况，数组的位置就会错位，使用坐标保证不会错位
        for (int i = 0; i < finalHeaderList.size(); i++) {
            Cells td = finalHeaderList.get(i);
            System.out.println(td);
            System.out.println(i);
            String content = td.getContent();
            //这段代码的含义是计算表格中每个单元格的内容长度，并将结果存储在`contextSizeArr`二维数组中。
            // 具体来说，`td.getY()`和`td.getX()`分别表示单元格的行索引和列索引，而`getStringLength(content)`用于获取单元格内容的长度。
            // 最后，将内容长度加1是为了预留一个额外的空间，以便在生成Excel时适应单元格的宽度。
            int stringLength = getStringLength(content + 1);
            int y = td.getY();
            int x = td.getX();
            contextSizeArr[y][x] = stringLength;

        }
        int[] maxLengthArr = getMaxLength(contextSizeArr);

        //根据解析到的数据返回POI的Excel对象
        return buildExcel(tableTitle, finalHeaderList, dataArr, maxLengthArr);
    }

    /**
     * 横向推导
     * 使用递归，进行横坐标的第一步推导，横向推导，同时删除多余的非td元素
     *
     * @param e
     * @return
     */
    private int HorizontalDeduction_th(Element e) {
        Element preElement = e.previousElementSibling();
        if (preElement != null) {
            if (!preElement.tagName().equals("th")) {//表示td的上一个兄弟节点不是td，则删除这个多余的元素
                preElement.remove();
            } else {
                int nColSpan = 1;//默认为1
                if (StringUtils.isNotBlank(preElement.attr("colspan"))) {
                    nColSpan = Integer.valueOf(preElement.attr("colspan").trim());//前一个元素的列合并情况
                }
                return HorizontalDeduction_th(preElement) + nColSpan;
            }
        }
        return 0;
    }

    /**
     * @Title : HorizontalDeduction
     * @Description : 使用递归，进行横坐标的第一步推导，横向推导，同时删除多余的非td元素
     * @author : Qin
     * @date : 2014年12月12日 下午8:51:39
     * @param e
     * @return
     */
    private  int HorizontalDeduction_td(Element e) {
        Element preElement=e.previousElementSibling();
        if(preElement!=null){
            if(!preElement.tagName().equals("td")){//表示td的上一个兄弟节点不是td，则删除这个多余的元素
                preElement.remove();
            }else{
                int nColSpan=1;//默认为1
                if(StringUtils.isNotBlank(preElement.attr("colspan"))){
                    nColSpan=Integer.valueOf(preElement.attr("colspan").trim());//前一个元素的列合并情况
                }
                return HorizontalDeduction_td(preElement) + nColSpan;
            }
        }
        return 0;
    }

    /**
     * 纵向推导
     *
     * @param headerList
     * @return
     */
    private List<Cells> verticalDeduction(List<Cells> headerList) {
        int headerSize = headerList.size();
        for (int i = 0; i < headerSize; i++) {
            Cells tdA = headerList.get(i);
            boolean flag = true;
            while (flag) {// 不断排列当前节点的位置，直到它的位置绝对正确
                flag = false;// 不需要移位
                for (int j = i - 1; j >= 0; j--) {// 找到之前与td的横坐标相等的值
                    Cells tdB = headerList.get(j);
                    if (tdA.getX() == tdB.getX()) {// A找到与其X坐标相等的B
                        // 如果B单元格“挡住”了A单元格，才进行移位操作。即：只有B占的行数
                        // 大于或等于A、B之间的距离，那么B才会挡住A
                        if (tdB.getRowspan() > tdA.getY() - tdB.getY()) {
                            // 如果存在移位单元格，则仍然需要重新判断当前的位置是否正确。需要移位
                            flag = true;
                            // A的X坐标向后推B.colspan的位置
                            tdA.setX(tdA.getX() + tdB.getColspan());
                            int YA = tdA.getY();
                            // 同时，与A同处一tr但在其后边的td节点均应向后推B.colspan位移
                            for (int m = i + 1; m < headerSize; m++) {
                                Cells Cells = headerList.get(m);
                                if (Cells.getY() == YA) {
                                    Cells.setX(Cells.getX() + tdB.getColspan());
                                }
                            }
                        }
                    }
                }
            }
        }
        return headerList;
    }

    /**
     * 中文字符与非中文字符长度计算
     *
     * @param s
     * @return
     */
    private int getStringLength(String s) {

        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        //进位取整
        return (int) Math.ceil(valueLength);
    }

    /**
     * 竖向遍历二维数组，找到每一列的最大值
     *
     * @param contextSizeArr
     * @return
     */
    private int[] getMaxLength(int[][] contextSizeArr) {
        int[] maxArr = new int[columnSize];
        for (int i = 0; i < columnSize; i++) {
            int basic = 0;
            for (int j = 0; j < rowSize; j++) {
                if (contextSizeArr[j][i] > basic) {//注意下标的写法
                    basic = contextSizeArr[j][i];
                }
            }
            maxArr[i] = basic;
        }
        return maxArr;
    }

    /**
     * @param title
     * @param finalHeaderList 表格表头数据
     * @param dataArr         表格内容数据
     * @return
     * @throws Exception
     * @Title : buildExcel
     * @Description : 依据传入的数据生成Excel文件
     * @author : Qin
     * @date : 2014年12月13日 下午6:32:06
     */
    private HSSFWorkbook buildExcel(String title, List<Cells> finalHeaderList, String[][] dataArr, int[] maxLengthArr) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = null;
        if (StringUtils.isNotBlank(sheetName)) {
            sheet = wb.createSheet(sheetName);
        } else if (StringUtils.isNotBlank(title)) {
            sheet = wb.createSheet("title");
        } else {
            sheet = wb.createSheet("Sheet1");
        }

        //表格样式
        //1、基础样式
        HSSFCellStyle basicStyle = wb.createCellStyle();
        basicStyle.setAlignment(HorizontalAlignment.CENTER);//设置水平居中
        basicStyle.setVerticalAlignment(VerticalAlignment.CENTER);    //设置垂直居中

        basicStyle.setBorderBottom(BorderStyle.THIN); // 下边框
        basicStyle.setBorderLeft(BorderStyle.THIN); // 左边框
        basicStyle.setBorderTop(BorderStyle.THIN); // 上边框
        basicStyle.setBorderRight(BorderStyle.THIN); // 右边框

        //2、标题样式
        HSSFCellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER); // 设置水平居中
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 设置垂直居中

        HSSFFont headerFont1 = wb.createFont();
        headerFont1.setBold(true); // 设置字体加粗
        headerFont1.setFontHeightInPoints((short) 14);//设置字体大小
        titleStyle.setFont(headerFont1);

        //3、偶数行样式
        HSSFCellStyle evenStyle = wb.createCellStyle();
        evenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        evenStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        evenStyle.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle oldStyle = wb.createCellStyle();
        oldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        oldStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        oldStyle.setAlignment(HorizontalAlignment.CENTER);

        //构建基本空白表格
        for (int i = 0; i < rowSize; i++) {
            sheet.createRow(i);
            for (int j = 0; j < columnSize; j++) {
                sheet.getRow(i).createCell(j).setCellStyle(basicStyle);
            }
        }
        //填充数据
        if (rowSize_title != 0) {
            //1、标题
            HSSFCell cell = sheet.getRow(0).getCell(0);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(title);
            //单元格合并
            sheet.addMergedRegion(new CellRangeAddress(0, rowSize_title - 1, 0, columnSize - 1));//起始行，终止行，起始列，终止列
        }
        //2、表头、单元格数据内容写入
        for (int i = 0; i < finalHeaderList.size(); i++) {
            try {
                Cells td = finalHeaderList.get(i);
                sheet.getRow(td.getY()).getCell(td.getX()).setCellValue(td.getContent());
                //单元格合并
                sheet.addMergedRegion(
                        new CellRangeAddress(//起始行，终止行，起始列，终止列
                                td.getY(),
                                td.getY() + (td.getRowspan() - 1),
                                td.getX(),
                                td.getX() + (td.getColspan() - 1))
                );
            } catch (ArrayIndexOutOfBoundsException e) {
                // 处理越界异常,比如打印错误日志并返回一个合法的数据结构
                System.err.println("Error processing cell at index " + i + ": " + e.getMessage());
                // 返回一个合法的单元格,比如默认值
                Cells td = new Cells();
                td.setContent("");
                td.setX(0);
                td.setY(0);
                td.setColspan(1);
                td.setRowspan(1);
                finalHeaderList.set(i, td);
            }
        }
        //3、设置每一列宽度以该列的最长内容为准
        for (int i = 0; i < maxLengthArr.length; i++) {
            sheet.setColumnWidth(i, maxLengthArr[i] * 2 * 235);
        }

        for (int i = 0; i < rowSize; i++) {
            HSSFRow row = sheet.getRow(i);
            row.setHeightInPoints(row.getHeightInPoints() + 3);
        }
        return wb;
    }
}
