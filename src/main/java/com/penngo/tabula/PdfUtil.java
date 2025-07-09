package com.penngo.tabula;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.penngo.excel.ResidentialLand;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * @author Jae
 * @create 2024 - 04 - 07 11:32
 */
public class PdfUtil {

    public static void main(String[] args) {
        JSONArray jsonArray = parsePdfSimpleTable("E:\\Jae\\sibu\\需求1\\all\\ENR\\亚洲压缩包\\印尼_ENR-202404162002390648\\ENR 2\\96-ENR 2.1 FIR, UTA, FSS, CTA, TMA AMDT 92 28 MAY 20.pdf", 0);
//        System.out.println(jsonArray);

        List<ResidentialLand> listInfo = new ArrayList<>();
        for (int i = 0; i <jsonArray.size() ; i++) {
            //获取每个页
            JSONArray jsonPage = jsonArray.getJSONArray(i);
            //遍历页
            for (int j = 0; j < jsonPage.size(); j++) {
                //获取每页中的data
                JSONArray dataArr = jsonPage.getJSONObject(j).getJSONArray("data");
                //遍历data中的每个单元格
                for (int k = 0; k < dataArr.size() ; k++) {
                    //遍历data中的每一条,也就是单元格中的每一行
                    JSONArray dataD = dataArr.getJSONArray(k);
                    String xuhao = dataD.getJSONObject(0).get("text").toString().replaceAll("\r","");
                    //如果第1个单元格的数据是序号,则跳出个这个循环,因为不能将标题存入表中
                    if(xuhao.contains("序号")){
                        continue;
                    }
                    ResidentialLand info = new ResidentialLand();
                    info.setId(UUID.randomUUID().toString());
                    info.setDelFlag("0");
                    //通过下标获取每个单元格的数据,下标是固定的 ,最多有9个单元格
                    info.setNum(dataD.getJSONObject(0).get("text").toString().replaceAll("\r",""));
                    info.setParcelCode(dataD.getJSONObject(1).get("text").toString().replaceAll("\r",""));
                    info.setPosition(dataD.getJSONObject(2).get("text").toString().replaceAll("\r",""));
                    info.setMeasure(dataD.getJSONObject(3).get("text").toString().replaceAll("\r",""));
                    info.setType(dataD.getJSONObject(4).get("text").toString().replaceAll("\r",""));
                    info.setSupplyWay(dataD.getJSONObject(5).get("text").toString().replaceAll("\r",""));
                    info.setSupplyTime(dataD.getJSONObject(6).get("text").toString().replaceAll("\r","").replaceAll(" ",""));
                    info.setPushOnMain(dataD.getJSONObject(7).get("text").toString().replaceAll("\r",""));
                    info.setRemark(dataD.getJSONObject(8).get("text").toString().replaceAll("\r",""));
                    listInfo.add(info);
                }
            }
        }

        int a = 0;
        for (int i = 0; i < listInfo.size(); i++) {
            //判断序号如果是空,则将序号空的数据与上一条数据合并
            if("".equals(listInfo.get(i).getNum()) || listInfo.get(i).getNum() == null){
                a++;
                listInfo.get(i-a).setParcelCode(listInfo.get(i-a).getParcelCode()+listInfo.get(i).getParcelCode());
                listInfo.get(i-a).setPosition(listInfo.get(i-a).getPosition()+listInfo.get(i).getPosition());
                listInfo.get(i-a).setMeasure(listInfo.get(i-a).getMeasure()+listInfo.get(i).getMeasure());
                listInfo.get(i-a).setType(listInfo.get(i-a).getType()+listInfo.get(i).getType());
                listInfo.get(i-a).setSupplyWay(listInfo.get(i-a).getSupplyWay()+listInfo.get(i).getSupplyWay());
                listInfo.get(i-a).setSupplyTime(listInfo.get(i-a).getSupplyTime()+listInfo.get(i).getSupplyTime());
                listInfo.get(i-a).setPushOnMain(listInfo.get(i-a).getPushOnMain()+listInfo.get(i).getPushOnMain());
                listInfo.get(i-a).setRemark(listInfo.get(i-a).getRemark()+listInfo.get(i).getRemark());
                a--;
            }
        }
        for (int i = 0; i < listInfo.size(); i++) {
            //遍历删除序号是空的数据
            if("".equals(listInfo.get(i).getNum()) || listInfo.get(i).getNum() == null){
                listInfo.remove(listInfo.get(i));
            }
        }
    }

//        System.out.println("=====================================================================================================");
//        String s = outputMdFormatForVerify(objects);
//        System.out.println(s);


    private static final SpreadsheetExtractionAlgorithm SPREADSHEEET_EXTRACTION_ALGORITHM = new SpreadsheetExtractionAlgorithm();
    private static final ThreadLocal<List<String>> THREAD_LOCAL = new ThreadLocal<>();

    private static Logger logger = LoggerFactory.getLogger(PdfUtil.class);

    /**
     * JsonArray数据转换为 Markdown格式
     * @param jsonArr
     * @return
     */
    private static String outputMdFormatForVerify(JSONArray jsonArr) {
        StringBuilder mdStrBld = new StringBuilder();
        StringBuilder headerStrBld = new StringBuilder("|");
        StringBuilder segmentStrBld = new StringBuilder("|");
        for (int row = 0; row < jsonArr.size(); row++) {
            StringBuilder bodyStrBld = new StringBuilder("|");
            JSONObject rowObj = (JSONObject) jsonArr.get(row);
            if (row == 0) {
                rowObj.forEach((k, v) -> {
                    headerStrBld.append(" ").append(k).append(" |");
                    segmentStrBld.append(" ").append("---").append(" |");
                });
                headerStrBld.append("\n");
                segmentStrBld.append("\n");
                mdStrBld.append(headerStrBld).append(segmentStrBld);
            }
            rowObj.forEach((k, v) -> bodyStrBld.append("").append(v).append("|"));
            bodyStrBld.append("\n");
            mdStrBld.append(bodyStrBld);
        }
        return mdStrBld.toString();
    }

    /**
     * @description: 解析 pdf 中简单的表格并返回 json 数组
     * @param {*} String PDF文件路径
     * @param {*} int 自定义起始行
     * @return {*}
     */
    public static JSONArray parsePdfSimpleTable(String pdfPath, int customStart) {
        return parsePdfTable(pdfPath, customStart, (cellList, rowIndex, reArr) -> {
            JSONObject jsonObj = new JSONObject();
            // 遍历单元格获取每个单元格内字段内容
//            List<String> headList = ObjectUtil.isNullObj(THREAD_LOCAL.get()) ? new ArrayList<>()
//                    : THREAD_LOCAL.get();
            List<String> headList = Optional.ofNullable(THREAD_LOCAL.get())
                    .map(ArrayList::new)
                    .orElseGet(ArrayList::new);

            for (int colIndex = 0; colIndex < cellList.size(); colIndex++) {
                String text = cellList.get(colIndex).getText().replace("\r", " ");
                if (rowIndex == customStart) {
                    headList.add(text);
                } else {
                    jsonObj.put(headList.get(colIndex), text);
                }
            }

            if (rowIndex == customStart) {
                THREAD_LOCAL.set(headList);
            }

            if (!jsonObj.isEmpty()) {
                reArr.add(jsonObj);
            }
        });
    }

    /**
     * @description: 解析pdf表格（私有方法）
     *               使用 tabula-java 的 sdk 基本上都是这样来解析 pdf 中的表格的，所以可以将程序提取出来，直到 cell
     *               单元格为止
     * @param {*} String pdf 路径
     * @param {*} int 自定义起始行
     * @param {*} PdfCellCallback 特殊回调处理
     * @return {*}
     */
    private static JSONArray parsePdfTable(String pdfPath, int customStart, PdfCellCustomProcess callback) {
        JSONArray reJsonArr = new JSONArray(); // 存储解析后的JSON数组

        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PageIterator pi = new ObjectExtractor(document).extract(); // 获取页面迭代器

            // 遍历所有页面
            while (pi.hasNext()) {
                Page page = pi.next(); // 获取当前页
                List<Table> tableList = SPREADSHEEET_EXTRACTION_ALGORITHM.extract(page); // 解析页面上的所有表格

                // 遍历所有表格
                for (Table table : tableList) {
                    List<List<RectangularTextContainer>> rowList = table.getRows(); // 获取表格中的每一行

                    // 遍历所有行并获取每个单元格信息
                    for (int rowIndex = customStart; rowIndex < rowList.size(); rowIndex++) {
                        List<RectangularTextContainer> cellList = rowList.get(rowIndex); // 获取行中的每个单元格
                        callback.handler(cellList, rowIndex, reJsonArr);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("function[PdfUtil.parsePdfTable] Exception [{} - {}] stackTrace[{}]",
                    e.getCause(), e.getMessage(), e.getStackTrace());
        } finally {
            THREAD_LOCAL.remove();
            logger.info("PDF解析后格式：{}",reJsonArr);
        }
        return reJsonArr; // 返回解析后的JSON数组
    }


}

