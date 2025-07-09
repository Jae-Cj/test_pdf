package com.penngo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;
 
import java.io.Serializable;
 
@Data
@Builder
public class WealthEntity implements Serializable {
 
    private static final long serialVersionUID = -1760099890427975758L;
 
    @ExcelProperty(value = "排名",index = 0)
    private Integer index;
 
    @ExcelProperty(value = "公司名称",index = 1)
    private String companyName;
 
    @ExcelProperty(value = "收入",index = 2)
    private String income;
 
    @ExcelProperty(value = "利润",index = 3)
    private String profit;
 
}