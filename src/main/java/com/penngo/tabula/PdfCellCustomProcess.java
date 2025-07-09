package com.penngo.tabula;

import com.alibaba.fastjson.JSONArray;
import technology.tabula.RectangularTextContainer;

import java.util.List;

/**
 * @author Jae
 * @create 2024 - 04 - 07 11:36
 */
@FunctionalInterface
public interface PdfCellCustomProcess {

    /**
     * @description: 自定义单元格回调处理
     * @return {*}
     */
    void handler(List<RectangularTextContainer> cellList, int rowIndex, JSONArray reJsonArr);
}

