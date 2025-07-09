package com.penngo.pdf;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author Jae
 * @create 2024 - 04 - 07 11:26
 */
public class PdfboxImageTest3 {



    public static void main(String[] args) {
        readTextAndImage();
    }



    /**
     * 读取pdf的文本内容和图片内容
     */
    public static void readTextAndImage() {
        String path = "E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf";
        File file = new File(path);
        // 尽可能使用try-with-resource代替try-catch-finally
        try (PDDocument document = PDDocument.load(file)) {
            int pageSize = document.getNumberOfPages();
            // 一页一页读取
            for (int i = 0; i < pageSize; i++) {
                // 文本内容
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置按顺序输出
                stripper.setSortByPosition(true);
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String text = stripper.getText(document);
                System.out.println(text.trim());
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-");

                // 图片内容
                PDPage page = document.getPage(i);
                PDResources resources = page.getResources();
                Iterable<COSName> cosNames = resources.getXObjectNames();
                if (cosNames != null) {
                    Iterator<COSName> cosNamesIter = cosNames.iterator();
                    while (cosNamesIter.hasNext()) {
                        COSName cosName = cosNamesIter.next();
                        if (resources.isImageXObject(cosName)) {
                            PDImageXObject Ipdmage = (PDImageXObject) resources.getXObject(cosName);
                            BufferedImage image = Ipdmage.getImage();
                            try (FileOutputStream out = new FileOutputStream(
                                    "E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf" + UUID.randomUUID() + ".png")) {
                                ImageIO.write(image, "png", out);
                            } catch (IOException e) {
                            }
                        }
                    }
                }

            }
        } catch (InvalidPasswordException e) {
        } catch (IOException e) {
        }


}

}
