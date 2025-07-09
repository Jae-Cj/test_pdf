package com.penngo.jsoup;

import lombok.Data;

/**
 * @author Jae
 * @create 2024 - 04 - 24 16:14
 */
@Data
public class Cells {

    private int rowspan=1;

    private int colspan=1;

    private int x;

    private int y;

    private String content;

}
