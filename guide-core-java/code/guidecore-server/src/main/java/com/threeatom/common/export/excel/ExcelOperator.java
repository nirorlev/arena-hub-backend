//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.export.excel;

import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.excel.vo.StudentBehaviorDataExcel;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public interface ExcelOperator {
    <T> void writeExcelToWebResponse(
            HttpServletResponse response,
            String fileName,
            String sheetName,
            Class<T> dataClass,
            List<T> datas)
            throws IOException;

    <T> void writeDynamicHeadExcelToWebResponse(
            HttpServletResponse response,
            List<GcSubject> subjectList,
            String fileName,
            String sheetName,
            List<StudentBehaviorDataExcel> studentBehaviorExcelData)
            throws IOException;
}
