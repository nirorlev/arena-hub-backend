package com.threeatom.config;

import com.threeatom.common.export.excel.ExcelOperator;
import com.threeatom.common.export.excel.impl.EasyExcelOperatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExportConfiguration {

    @Bean("excelService")
    public ExcelOperator createExcelOperator() {
        return new EasyExcelOperatorImpl();
    }
}
