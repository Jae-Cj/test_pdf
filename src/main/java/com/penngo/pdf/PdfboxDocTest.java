package com.penngo.pdf;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Jae
 * @create 2024 - 04 - 07 9:04
 */
public class PdfboxDocTest {

    public static void main(String[] args) throws IOException {
        PDDocument doc = new PDDocument();
        File file = new File("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf");
        FileInputStream fis = new FileInputStream(file);
        doc = PDDocument.load(fis);
        PDFTextStripper pdfStripper = new PDFTextStripper();

        Splitter splitter = new Splitter();
        List<PDDocument> split = splitter.split(doc);
        for (int i = 0; i < split.size(); i++) {
            doc = split.get(i);
            PDFRenderer pdfRenderer = new PDFRenderer(doc);
            String text = pdfStripper.getText(doc);
            System.out.println("第"+(i+1)+"页内容："+text);
        }
        doc.close();
    }

}
