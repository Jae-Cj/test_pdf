package com.penngo.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author Jae
 * @create 2024 - 04 - 11 14:02
 */
public class OpenVCTest1 {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        /**
         从文件路径读图像
         */
        String path = "E:\\Jae\\sibu\\yinni\\98-ENR 3.1\\page1-black-heart-triangle\\BAC-VOR-DME\\98-ENR-3.1BAC-VOR-DME.png";
        Mat img = Imgcodecs.imread(path);

        /**
        图像写到指定路径
        */
        String savePath = "E:\\Jae\\sibu\\yinni\\98-ENR 3.1\\page1-black-heart-triangle\\BAC-VOR-DME\\demo_bak.jpg";
        Imgcodecs.imwrite(savePath, img);
    }


}
