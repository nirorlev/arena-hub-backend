package com.threeatom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.threeatom.common.export.excel.ExcelOperator;
import com.threeatom.common.export.excel.impl.EasyExcelOperatorImpl;

@Configuration
public class ExportConfiguration {
	
	@Bean("excelService")
	public ExcelOperator createExcelOperator() {
		return new EasyExcelOperatorImpl();
	}

}
