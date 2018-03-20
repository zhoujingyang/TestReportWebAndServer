package com.baidu.ocr.model;


import lombok.Data;

@Data
public class TestReport {

    /**
     * 主键id
     */
    private int id;

    /**
     * 测试报告id，对应icafe平台项目id，也是上传测试报告中报告名字包含的id
     */
    private String reportId;

    /**
     * 测试报告名称
     */
    private String reportName;

    /**
     * QA名字
     */
    private String QAName;

    /**
     * RD名字
     */
    private String RDName;

    /**
     * 测试轮数
     */
    private String testCount;

    /**
     * 报告书写时间
     */
    private String reportWriteTime;

    /**
     * 最终测试报告路径，相对于tomcat中ROOT目录的相对路径
     */
    private String finalReportPath;

    /**
     * 测试结论
     */
    private String testConclusion;
}
