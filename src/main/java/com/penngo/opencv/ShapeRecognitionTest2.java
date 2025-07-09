package com.penngo.opencv;

/**
 * @author Jae
 * @create 2024 - 04 - 10 15:56
 */

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ShapeRecognitionTest2 {
    public static void main(String[] args) {
        // 加载OpenCV库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // 加载图片
        String imagePath = "E:\\Jae\\sibu\\screenshot\\1.png";
        Mat image = Imgcodecs.imread(imagePath);

        // 进行图像预处理（例如灰度化、二值化等）
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 127, 255, Imgproc.THRESH_BINARY);

        // 垂直倒影
        Mat verticalFlip = new Mat();
        Core.flip(binaryImage, verticalFlip, 0);

        // 水平倒影
        Mat horizontalFlip = new Mat();
        Core.flip(binaryImage, horizontalFlip, 1);

        // 查找轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 遍历每个轮廓
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);

            // 判断是否为黑心三角形
            if (isHollowTriangle(contour)) {
                System.out.println("黑心三角形");
            }

            // 判断是否为空心三角形
            if (isHollowShape(contour)) {
                System.out.println("空心三角形");
            }

            // 判断是否为朝上方向箭头
            if (isUpArrow(contour)) {
                System.out.println("朝上方向箭头");
            }

            // 判断是否为朝下方向箭头
            if (isDownArrow(contour)) {
                System.out.println("朝下方向箭头");
            }
        }
    }

    // 判断是否为黑心三角形
    private static boolean isHollowTriangle(MatOfPoint contour) {
        // 获取轮廓的凸包
//        MatOfInt hull = new MatOfInt();
//        Imgproc.convexHull(contour, hull);
//
//        // 将凸包转换为轮廓点的形式
//        MatOfPoint hullPoints = new MatOfPoint();
//        hullPoints.create((int) hull.size().height, 1, CvType.CV_32SC2);
//        for (int i = 0; i < hull.size().height; i++) {
//            int index = (int) hull.get(i, 0)[0];
//            double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1] };
//            hullPoints.put(i, 0, point);
//        }
//
//        // 判断凸包的顶点数量
//        if (hullPoints.rows() != 3) {
//            return false; // 非三角形，不是黑心三角形
//        }
//
//        // 判断轮廓是否包含凸包内部的点
//        for (int i = 0; i < contour.rows(); i++) {
//            if (Imgproc.pointPolygonTest(hullPoints, contour.get(i, 0), false) < 0) {
//                return false; // 轮廓包含凸包内部的点，不是黑心三角形
//            }
//        }

        return false; // 符合黑心三角形的条件
    }

    // 判断是否为空心三角形
    private static boolean isHollowShape(MatOfPoint contour) {
        // 获取轮廓的近似多边形
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
        double epsilon = 0.02 * Imgproc.arcLength(contour2f, true);
        Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);

        // 判断近似多边形的顶点数量
        if (approxCurve.rows() != 4) {
            return false; // 非三角形，不是空心三角形
        }

        return true; // 符合空心三角形的条件
    }

    // 判断是否为朝上方向箭头
    private static boolean isUpArrow(MatOfPoint contour) {
        // 获取轮廓的边界框
        Rect boundingRect = Imgproc.boundingRect(contour);

        // 判断边界框的宽高比是否符合箭头的特征
        double aspectRatio = (double) boundingRect.width / boundingRect.height;
        if (aspectRatio >= 0.8 && aspectRatio <= 1.2) {
            return true; // 符合朝上方向箭头的条件
        }

        return false;
    }

    // 判断是否为朝下方向箭头
    private static boolean isDownArrow(MatOfPoint contour) {
        // 获取轮廓的边界框
        Rect boundingRect = Imgproc.boundingRect(contour);

        // 判断边界框的宽高比是否符合箭头的特征
        double aspectRatio = (double) boundingRect.width / boundingRect.height;
        if (aspectRatio >= 0.8 && aspectRatio <= 1.2) {
            return true; // 符合朝下方向箭头的条件
        }

        return false;
    }
}
