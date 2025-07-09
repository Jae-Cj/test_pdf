package com.penngo.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
public class MyPdf extends PDFDomTree{
    public MyPdf() throws IOException {
        super();
    }

    @Override
    protected void startNewPage(){
        System.out.println("====页码:" + pagecnt);
        super.startNewPage();
    }


//    @Override
//    protected void renderText(String data, TextMetrics metrics)
//    {
//        System.out.println("====文本:" + data + "," +  ",x:" + (int)metrics.getX() + ",top:" + (int)metrics.getTop() + ",width:" + (int)metrics.getWidth() + ",height:" + (int)metrics.getHeight() );
//        curpage.appendChild(createTextElement(data, metrics.getWidth()));
//    }
//
//    @Override
//    protected void renderPath(List<PathSegment> path, boolean stroke, boolean fill) throws IOException
//    {
//        System.out.println("====线条size:" + path.size());
//        for(int i = 0; i < path.size(); i++){
//            PathSegment path1 = path.get(i);
//            System.out.println("====线条"+i+",x1:" + path1.getX1() + ",y1:" + path1.getY1() + ",x2:" + path1.getX2() + ",y2:" + path1.getY2() + ",stroke:" + stroke + ",fill:" + fill);
//        }
//
//        super.renderPath(path, stroke, fill);
//    }

//    @Override
//    protected void renderImage(float x, float y, float width, float height, ImageResource resource) throws IOException
//    {
//        System.out.println("====图片:" + "x:" + x + ",y:" + y + ",width:" + width + ",height:" + height);
//        curpage.appendChild(createImageElement(x, y, width, height, resource));
//    }

    public void parsePdf(PDDocument doc){
        try
        {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();
            LSOutput output = impl.createLSOutput();
            writer.getDomConfig().setParameter("format-pretty-print", true);
            createDOM(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        // 记录起始时间
        long l = System.currentTimeMillis();
        try {

//            File pdfFile = new File("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.pdf");
//            File outFile = new File("E:\\Jae\\sibu\\需求1\\印尼\\98-ENR 3.1.html");
            File pdfFile = new File("E:\\春水殿_成人之美\\杜蕾斯\\杜蕾斯订单详情预览-东莞瑞佳医疗科技有限公司.pdf");
            File outFile = new File("E:\\春水殿_成人之美\\杜蕾斯\\杜蕾斯订单详情预览-东莞瑞佳医疗科技有限公司.html");


            PDDocument pdf = PDDocument.load(pdfFile);

            PDFDomTree pdfDomTree = new PDFDomTree();

            pdfDomTree.writeText(pdf, new OutputStreamWriter(new FileOutputStream(outFile)));

            Document doc = pdfDomTree.getDocument();
            System.out.println(doc);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        // 记录结束时间
        long l1 = System.currentTimeMillis();
        System.out.println(" ===== end pdf parse success;" +
                "共耗时 " + (l1 - l) + " ms =====，" + (l1 - l) / 1000 + "s =====");
    }
}
