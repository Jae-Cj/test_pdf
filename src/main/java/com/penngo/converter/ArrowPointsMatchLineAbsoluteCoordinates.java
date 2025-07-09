package com.penngo.converter;

import lombok.Data;

/**
 * 箭头匹配最近两根线类
 * @author Jae
 * @create 2024 - 04 - 23 16:44
 */
@Data
public class ArrowPointsMatchLineAbsoluteCoordinates {

    private String x;

    private String y;

    private String w;

    private String h;

    private String pageNum;

    private String val;

    // TODO 明天改造
    private LineElementAbsoluteCoordinates lineElementAbsoluteCoordinates;
}
