package com.penngo.opencv;

/**
 * @author Jae
 * @create 2024 - 04 - 10 15:06
 */

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class ShapeRecognitionTest1 {

    public static void main(String[] args) {
        // 加载OpenCV库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // 读取图像
        String imagePath = "E:\\Jae\\sibu\\screenshot\\2.png";
        Mat image = Imgcodecs.imread(imagePath);

        // 图像预处理
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5), 0);

        // 边缘检测
        Mat edges = new Mat();
        Imgproc.Canny(grayImage, edges, 50, 150);

        // 查找轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 存储识别结果
        List<String> shapes = new ArrayList<>();

        // 遍历轮廓
        for (MatOfPoint contour : contours) {
            // 计算轮廓周长
            double perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);

            // 进行形状近似
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approxCurve, 0.03 * perimeter, true);

            // 判断形状类型
            int vertices = approxCurve.rows();
            if (vertices == 3) {
                // 三角形
                // 判断是空心三角形还是黑心三角形
                if (isHollowTriangle(approxCurve)) {
                    shapes.add("空心三角形");
                } else {
                    shapes.add("黑心三角形");
                }
            } else if (vertices == 6) {
                // 箭头
                // 判断是朝上箭头还是朝下箭头
                if (isUpArrow(approxCurve)) {
                    shapes.add("朝上方向箭头");
                } else {
                    shapes.add("朝下方向箭头");
                }
            }
        }

        // 输出识别结果
        for (String shape : shapes) {
            System.out.println("此图形是：" + shape);
        }
    }

    // 判断是否为黑心三角形
    private static boolean isHollowTriangle(MatOfPoint2f contour) {
        // 获取轮廓的凸包
//        MatOfPoint2f hull2f = new MatOfPoint2f();
//        Imgproc.convexHull(contour, hull2f);
//
//        // 判断凸包的顶点数量
//        int hullVertices = hull2f.rows();
//        if (hullVertices != 3) {
//            return false; // 非三角形，不是黑心三角形
//        }
//
//        // 判断轮廓是否包含凸包内部的点
//        for (int i = 0; i < contour.rows(); i++) {
//            if (Imgproc.pointPolygonTest(hull2f, contour.get(i, 0), false) < 0) {
//                return false; // 轮廓包含凸包内部的点，不是黑心三角形
//            }
//        }

        return true; // 符合黑心三角形的条件
    }

    // 判断是否为朝上箭头
    private static boolean isUpArrow(MatOfPoint2f contour) {
        // 获取轮廓的边界框
        RotatedRect boundingRect = Imgproc.minAreaRect(contour);

        // 判断边界框的宽高比是否符合箭头形状
        double aspectRatio = boundingRect.size.width / boundingRect.size.height;
        if (aspectRatio < 1.0) {
            return false; // 宽高比不符合箭头形状，不是朝上箭头
        }

        // 判断箭头顶部是否较窄
        double topWidthRatio = Math.abs(boundingRect.size.width - boundingRect.size.height) / boundingRect.size.width;
        if (topWidthRatio > 0.4) {
            return false; // 箭头顶部宽度较大，不是朝上箭头
        }

        return true; // 符合朝上箭头的条件
    }
}
