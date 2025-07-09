package com.penngo.opencv;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.FileWriter;
import java.io.IOException;


/**
 * @author Jae
 * @create 2024 - 04 - 11 9:06
 */
public class OpenCVTest {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // 导入
        Mat mat = Imgcodecs.imread("E:\\Jae\\sibu\\yinni\\98-ENR 3.1\\page1\\arrow-points-down\\BND-VOR-DME--TKG-VOR-DME\\98-ENR-3.1BND-VOR-DME--TKG-VOR-DME.png");
//        projectionVerticality(mat);
        projectionHorizontal(mat);

    }

    /**
     * 水平投影
     * @param mat 输入的图像
     * @return 投影后的图像
     */
    private static Mat projectionHorizontal(Mat mat) {
        // 克隆输入的图像，获取同样的Mat对象
        Mat projectionMat = mat.clone();
        // 将图像颜色设置为白色
        projectionMat.setTo(new Scalar(255));
        //创建一个list用于存储每一列的黑点数量
        Double[] dotList = new Double[mat.rows()];
        String dump = mat.dump();

        // 导出
        String filePath = "E:\\Jae\\sibu\\yinni\\98-ENR 3.1\\page1\\arrow-points-down\\BND-VOR-DME--TKG-VOR-DME\\98-ENR-3.1BND-VOR-DME--TKG-VOR-DME.txt";

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(dump);
            writer.close();
            System.out.println("导出成功！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("导出失败：" + e.getMessage());
        }

        System.out.println("水平投影正在处理：" +dump);
        // 获取图像的列数
        int col = mat.cols();
        // 获取图像的行数
        int row = mat.rows();
        // 计算每一行的黑点数量
        for (int y = 0; y < row; y++) {
            dotList[y] = 0.0;
            for (int x = 0; x < col; x++) {
                double binData = mat.get(y, x)[0];
                // 判断像素值是否为黑色
                if (binData == 0) {
                    dotList[y]++;
                }
            }
        }
        // 生成投影图
        for (int y = 0; y < mat.rows(); y++) {
            for (int x = 0; x < mat.cols(); x++) {
                try {
                    if (x < dotList[y]) {
                        // 将投影图中对应位置的像素值设为黑色
                        projectionMat.put(y, x, 0);
                    }
                } catch (Exception e) {
                    System.out.println("水平投影正在处理第：" + y + "行，第" + x + "列");
                }
            }
        }
        return projectionMat;
    }

    /**
     * 垂直投影
     * @param mat 输入的图像
     * @return 投影后的图像
     */
    private static Mat projectionVerticality(Mat mat) {
        // 克隆输入的图像，获取同样的Mat对象
        Mat projectionMat = mat.clone();
        // 将图像颜色设置为白色
        projectionMat.setTo(new Scalar(255));
        // 创建一个列表，用于存储每一列的黑点数量
        Double[] dotList = new Double[mat.cols()];
        String dump = mat.dump();
        System.out.println("垂直投影正在处理："+ dump);
        // 获取图像的列数
        int col = mat.cols();
        // 获取图像的行数
        int row = mat.rows();
        // 计算每一列的黑点数量
        for (int x = 0; x < col; x++) {
            dotList[x] = 0.0;
            for (int y = 0; y < row; y++) {
                double binData = mat.get(y, x)[0];
                // 判断像素值是否为黑色
                if (binData == 0) {

                    dotList[x]++;
                }
            }
        }
        // 生成投影图
        for (int x = 0; x < mat.cols(); x++) {
            for (int y = 0; y < mat.rows(); y++) {
                if (x == 147) {
                    System.out.println("下一行将会出现错误");
                }
                if (y < dotList[x]) {
                    projectionMat.put(y, x, 0);
                    System.out.println("垂直投影正在处理第："+ x + "列" + y + "行");
                }

            }

        }

        return projectionMat;
    }
}

