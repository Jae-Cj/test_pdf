package com.penngo.tabula;

import com.alibaba.fastjson.JSONArray;
import com.penngo.excel.ResidentialLand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import technology.tabula.CommandLineApp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Exception in thread "main" java.lang.ClassCastException: java.lang.String cannot be cast to com.alibaba.fastjson.JSONArray
 * 	at com.alibaba.fastjson.JSONArray.getJSONArray(JSONArray.java:261)
 * 	at com.penngo.tabula.TestPdfTabula.main(TestPdfTabula.java:38)
 */
public class TestPdfTabula {
    public static void main(String[] args) throws Exception {
        //-f导出格式,默认CSV  (一定要大写)
        //-p 指导出哪页,all是所有
        //path　D:\\1xx.pdf
        //-l 强制使用点阵模式提取PDF　（关键在于这儿）
//        String[] arguments = new String[]{"-f=JSON","-p=1", "E:\\Jae\\sibu\\需求1\\all\\ENR\\亚洲压缩包\\印尼_ENR-202404162002390648\\ENR 2\\96-ENR 2.1.pdf","-l"};
        String[] arguments = new String[]{"-f=JSON","-p=1", "E:\\春水殿_成人之美\\杜蕾斯\\杜蕾斯订单详情预览-东莞瑞佳医疗科技有限公司.pdf","-l"};
        // 注释后可以debug
//        CommandLineApp.main(arguments);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(CommandLineApp.buildOptions(), arguments);
        StringBuilder stringBuilder = new StringBuilder();
        new CommandLineApp(stringBuilder, cmd).extractTables(cmd);
        String string = stringBuilder.toString();
        String jsonStr = String.format(stringBuilder.toString());

        System.out.println("打印返回数据:  " + jsonStr);

        // 解析tabula读取pdf表格,将返回的数据转成jsonArray
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(string);
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

}