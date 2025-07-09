package com.penngo.excel;
 
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
 
public class PDFUtil {
    /**
     * 用来读取pdf文件
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readPDF(String filePath) {
        String buffer = "";
        try{
            File input = new File(filePath);
            if (input != null && input.exists()) {
                PDDocument pd = PDDocument.load(input);
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(false);
                buffer = stripper.getText(pd);
                pd.close();
            }else
                buffer =  "read failed";
        }catch (Exception e){
            e.printStackTrace();
            return "read failed";
        }
        return buffer;
    }
 
    public static String readPDF2(String fileName) {
        String result = "";
        File file = new File(fileName);
        FileInputStream in = null;
        try {
            in = new FileInputStream(fileName);
            // 新建一个PDF解析器对象
            PDFParser parser = new PDFParser(new RandomAccessFile(file,"rw"));
            // 对PDF文件进行解析
            parser.parse();
            // 获取解析后得到的PDF文档对象
            PDDocument pdfdocument = parser.getPDDocument();
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            stripper .setSortByPosition(false); //sort:设置为true 则按照行进行读取，默认是false
            // 从PDF文档对象中剥离文本
            result = stripper.getText(pdfdocument);
        } catch (Exception e) {
            System.out.println("读取PDF文件" + file.getAbsolutePath() + "生失败！" + e);
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
        return result;
    }
 
    /**
     * 测试pdf文件的创建
     * @param args
     */
    public static void main(String[] args) {
        try {
//            String fileName = "600519_贵州茅台_贵州茅台2019年年度报告";
            String filePath = "E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf";  //这里先手动把绝对路径的文件夹给补上。
            PDFUtil pdfUtil = new PDFUtil();
//            String result = pdfUtil.readPDF(filePath);
            String result = pdfUtil.readPDF2(filePath);
 
            System.out.println(result);
            //将提取的表格内容写入txt文档
            FileWriter fileWriter = new FileWriter("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.txt");
            fileWriter.write(result);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
}