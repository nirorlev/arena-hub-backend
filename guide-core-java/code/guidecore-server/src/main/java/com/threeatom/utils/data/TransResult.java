package com.threeatom.utils.data;

import java.util.List;
import lombok.Data;

@Data
public class TransResult {
    private String from;

    private String to;

    private List<TransData> trans_result;
}
