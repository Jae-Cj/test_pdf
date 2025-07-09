package com.penngo.tabula;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import technology.tabula.CommandLineApp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
/**
 * @author Jae
 * @create 2024 - 04 - 25 9:08
 */
public class TabulaPDF2ExcelTest {


    public static void main(String[] args) throws Exception {
        //-f导出格式,默认CSV  (一定要大写)
        //-p 指导出哪页,all是所有
        //path　D:\\1xx.pdf
        //-l 强制使用点阵模式提取PDF　（关键在于这儿）
        String[] arguments = new String[]{"-f=JSON", "-p=all", "E:\\Jae\\sibu\\需求1\\all\\ENR\\亚洲压缩包\\印尼_ENR-202404162002390648\\ENR 2\\96-ENR 2.1.pdf", "-l"};
        // 注释后可以debug
//        CommandLineApp.main(arguments);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(CommandLineApp.buildOptions(), arguments);
        StringBuilder stringBuilder = new StringBuilder();
        new CommandLineApp(stringBuilder, cmd).extractTables(cmd);
        String string = stringBuilder.toString();
        System.out.println("打印返回数据:  " + string);

        // 解析JSON结果并添加分页标识 也是41页
        String modifiedString = addPageNumberToJson(string); // 添加分页标识
//        System.out.println("打印返回数据:  " + modifiedString);
    }

    public static String addPageNumberToJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);

        // 获取表格数组节点
        ArrayNode tablesNode = (ArrayNode) rootNode;
        int pageCount = tablesNode.size();

        // 遍历每个表格节点，添加页码信息
        for (int i = 0; i < pageCount; i++) {
            ObjectNode tableNode = (ObjectNode) tablesNode.get(i);
            tableNode.put("pagenum", i + 1); // 页码从1开始
        }

        return mapper.writeValueAsString(rootNode);
    }

}
