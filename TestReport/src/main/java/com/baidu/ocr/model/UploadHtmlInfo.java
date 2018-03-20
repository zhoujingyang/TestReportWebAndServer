package com.baidu.ocr.model;

import lombok.Data;

@Data
public class UploadHtmlInfo {
    /**
     * 上传的原始测试报告路径
     */
    private String fileReportPath;

    /**
     * 用户填写测试过程的存储路径
     */
    private String processReportPath;

    /**
     * 最终拼接的测试路径
     */
    private String finalReportPath;

    /**
     * 对应icafe中的项目id
     */
    private String reportId;

    /**
     * 测试报告中项目名称
     */
    private String reportName;


    /**
     * 上传的原始测试报告中QAName
     */
    private String QAName;


    /**
     * 上传的原始测试报告中RDName
     */
    private String RDName;

    /**
     * 上传的原始测试报告中报告编写时间
     */
    private String reportWriteTime;

    /**
     * 上传的原始测试报告中获取测试轮数，如果原始报告中没写，数据中默认为1
     */
    private String testCount;

    /**
     * 上传的原始测试报告中获取测试结论
     */
    private String testConclusion;
}
