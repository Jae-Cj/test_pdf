//package com.penngo.converter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Main {
//    public static void main(String[] args) {
//        ArrayList<ElementDTO> elements = new ArrayList<>();
//        elements.add(new ElementDTO("p0", "top:23.01752pt;left:56.7pt;line-height:11.7075pt;font-family:Arial;font-size:10.5pt;width:16.9575pt;", "AIP"));
//        elements.add(new ElementDTO("p1", "top:23.01752pt;left:76.5765pt;line-height:11.7075pt;font-family:Arial;font-size:10.5pt;width:57.7185pt;", "INDONESIA"));
//        elements.add(new ElementDTO("p2", "top:23.01752pt;left:137.2455pt;line-height:11.7075pt;font-family:Arial;font-size:10.5pt;width:24.507004pt;", "(VOL"));
//        elements.add(new ElementDTO("p3", "top:23.01752pt;left:164.6295pt;line-height:11.7075pt;font-family:Arial;font-size:10.5pt;width:6.415512pt;", "I)"));
//        elements.add(new ElementDTO("p4", "top:23.01752pt;left:354.9pt;line-height:11.7075pt;font-family:Arial;font-size:10.5pt;width:22.165497pt;", "ENR"));
//        elements.add(new ElementDTO("p5", "top:23.01752pt;left:379.9845pt;line-height:11.7075pt;font-family:Arial;font-size:10.5pt;width:14.595032pt;", "3.1"));
//
//        List<ElementAbsoluteCoordinates> elementAbsoluteCoordinatesList = extractTextCoordinates(elements);
//        for (ElementAbsoluteCoordinates elementAbsoluteCoordinates : elementAbsoluteCoordinatesList) {
//            System.out.println(elementAbsoluteCoordinates);
//        }
//    }
//
//    public static List<ElementAbsoluteCoordinates> extractTextCoordinates(List<ElementDTO> elements) {
//        List<ElementAbsoluteCoordinates> elementAbsoluteCoordinatesList = new ArrayList<>();
//        for (ElementDTO element : elements) {
//            String topValue = extractValue(element.getStyle(), "top").replace("pt", "");
//            String leftValue = extractValue(element.getStyle(), "left");
//            elementAbsoluteCoordinatesList.add(new ElementAbsoluteCoordinates(topValue, leftValue, null,element.getValue()));
//        }
//        return elementAbsoluteCoordinatesList;
//    }
//
//    public static String extractValue(String style, String key) {
//        int startIndex = style.indexOf(key) + key.length() + 1;
//        int endIndex = style.indexOf("pt;", startIndex);
//        return style.substring(startIndex, endIndex);
//    }
//}