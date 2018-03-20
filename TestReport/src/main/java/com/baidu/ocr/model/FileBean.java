package com.baidu.ocr.model;


import lombok.Data;

@Data
public class FileBean {
    /**
     * 对应application.yml配置文件中的path
     */
    private static String filePath;

    /**
     * 对应application.yml配置文件中的reportHtmlService
     */
    private static String reportHtmlService;

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        FileBean.filePath = filePath;
    }

    public static String getReportHtmlService() {
        return reportHtmlService;
    }

    public static void setReportHtmlService(String reportHtmlService) {
        FileBean.reportHtmlService = reportHtmlService;
    }
}