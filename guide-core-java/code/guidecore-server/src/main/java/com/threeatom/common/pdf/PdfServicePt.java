package com.threeatom.common.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface PdfServicePt {

    ByteArrayOutputStream getPdfBytes(PdfModel model) throws IOException;
}
