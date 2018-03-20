package com.baidu.unittest;

import com.baidu.ocr.utils.ReportAnalysis;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class test {


    @Test
    public void test1() throws IOException {
        System.out.println(test.class.getResource("").getPath());
        File directory = new File("");// 参数为空
        String courseFile = directory.getCanonicalPath();
        System.out.println(courseFile);
    }

    @Test
    public void test2() throws IOException {

        File file = new File("/workspace/baidu/baidu/vis/ocr-qa/OCR/TestReport/reportFiles/1/report_1.html");

        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",file.getName(), "text/html", input);

//        ReportAnalysis.getReportName(multipartFile);
    }
}
