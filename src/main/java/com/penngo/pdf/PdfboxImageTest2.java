package com.penngo.pdf;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Jae
 * @create 2024 - 04 - 07 10:46
 */
public class PdfboxImageTest2 {


    public static void main(String[] args) {
        // 获取项目目录
        String basePath = System.getProperty("user.dir");
        // 定义PDF目录
        String pdfDir = basePath + File.separator + "src" + File.separator + "pdfDir" + File.separator + "印尼";
        // 文件名
        String fileName = "98-ENR 3.1.pdf";
        // 完整文件路径
        String fileFullName = pdfDir + File.separator + fileName;
        // 打印文件路径
        System.out.println("pdf文件路径：" + fileFullName);
        // 打印开始读取PDF
        System.out.println("====read pdf begin ====");
        // 记录起始时间
        long l = System.currentTimeMillis();

        try {
            readPDF(fileFullName, pdfDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 记录结束时间
        long l1 = System.currentTimeMillis();
        System.out.println(" ===== end pdf parse success;共耗时 " + (l1 - l) + " ms =====");

    }

    /*
     * 读PDF文件，使用了pdfbox开源项目
     */
    public static void readPDF(String fileName, String outPath) {
        // 创建文件对象
        File file = new File(fileName);
        // 输入流
        FileInputStream in = null;
        try {

            // 获取解析后得到的PDF文档对象
            PDDocument pdfdocument = PDDocument.load(file);

            int pageNumbers = pdfdocument.getNumberOfPages();
            System.out.println("PDF总页数：" + pageNumbers);
            //新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            //sort设置为true 则按照行进行读取，默认是false
            stripper.setSortByPosition(true);
            // 写入到文件
            FileWriter fileWriter = new FileWriter(outPath + File.separator + "pdf_extractText.txt");
            // 遍历每一页
            for (int i = 1; i <= pdfdocument.getNumberOfPages(); i++) {
                //读取文字
                System.out.println("第 " + i + " 页 ");
                // 设置起始页码
                stripper.setStartPage(i);
                // 设置结束页码
                stripper.setEndPage(i);
                // 获取文本内容
                String result = stripper.getText(pdfdocument);
                System.out.println("PDF文件的文本内容如下：");
                System.out.println(result);
                // 写入文本内容到文件
                fileWriter.write(result);
                // 清空缓冲区
                fileWriter.flush();


                // 读取图片
                // 获取当前页
                PDPage page = pdfdocument.getPage(i - 1);
                // 获取页资源
                PDResources pdResources = page.getResources();
                // 图片索引
                int index = 1;
                //获取页中的对象
                System.out.println("PDF文件的图片：");
                // 遍历页资源
                for (COSName csName : pdResources.getXObjectNames()) {
                    // 获取PDF对象
                    PDXObject pdxObject = pdResources.getXObject(csName);
                    // 如果对象为图像对象
                    if (pdxObject instanceof PDImageXObject) {
                        // 获取数据流
                        PDStream pdStream = pdxObject.getStream();
                        // 创建图像对象
                        PDImageXObject image = new PDImageXObject(pdStream, pdResources);
                        // 创建图像文件
                        File imgFile = new File(String.format(outPath + File.separator + "page%d-image%d.png", i, index++));
                        // 写入图像文件
                        ImageIO.write(image.getImage(), "PNG", imgFile);
                    }
                }

            }
            // 关闭文件写入器
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("读取PDF文件" + file.getAbsolutePath() + "失败！" + e);
            e.printStackTrace();
        } finally {
            if (in != null) { // 如果输入流不为空
                try {
                    in.close(); // 关闭输入流
                } catch (IOException e1) {
                }
            }
        }
    }


}
