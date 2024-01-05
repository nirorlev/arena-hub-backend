package com.threeatom.common.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @title: PdfServicePt
 * @projectName jeeplus-core
 * @description: TODO
 * @date 2022/8/22/02214:55
 */
public interface PdfServicePt {

    ByteArrayOutputStream getPdfBytes(PdfModel model) throws IOException;
}
