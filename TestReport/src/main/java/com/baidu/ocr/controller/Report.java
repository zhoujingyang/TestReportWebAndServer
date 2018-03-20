package com.baidu.ocr.controller;


import com.alibaba.fastjson.JSONObject;
import com.baidu.ocr.model.FileBean;
import com.baidu.ocr.model.UploadHtmlInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
@RestController
@Api(value = "/report/v1/", description = "测试报告平台报告相关api")
@RequestMapping("/report/v1")
public class Report {

    @Autowired
    public SqlSessionTemplate sqlSessionTemplate;

    //目标存储路径
    private static File dest;

    @RequestMapping(value = "getReportHtmlService",method = RequestMethod.GET)
    @ApiOperation(value = "获取存放测试报告html文件tomcat服务器的地址",httpMethod = "GET")
    public String getReportHtmlService(){

        return FileBean.getReportHtmlService();
    }


    @RequestMapping(value = "getPreviewHtmlService",method = RequestMethod.GET)
    @ApiOperation(value = "获取最终版本的测试报告html文件tomcat服务器的地址",httpMethod = "GET")
    public String getPreviewHtmlService(@RequestParam(required = true) String reportId){
        String finalReportPath = sqlSessionTemplate.selectOne("getFinalReportOfReportId",reportId);
        return FileBean.getReportHtmlService()+finalReportPath;
    }

    @RequestMapping(value = "getReports" , method = RequestMethod.GET)
    @ApiOperation(value = "获取所有的测试报告列表", httpMethod = "GET", notes = "获取所有的测试报告列表")
    public String getReports(@RequestParam(required = true) int numbersOfEveryPage,
                             @RequestParam(required = false) int targetPage){
        Map map = new HashMap();
        map.put("numbersOfEveryPage",numbersOfEveryPage);

        int start;
        if(1==targetPage){
            start=0;
        }else {
            start = numbersOfEveryPage * (targetPage - 1);
        }
        map.put("start",start);
        List list = sqlSessionTemplate.selectList("getTestReports",map);
        JSONObject object = new JSONObject();
        object.put("tableInfo",list);
        return object.toJSONString();
    }

    @RequestMapping(value = "getReportForName",method = RequestMethod.GET)
    @ApiOperation(value = "根据报告名称获取测试报告" , httpMethod = "GET" ,notes = "根据报告名称查询报告")
    public String getReportForName(@RequestParam(required = true) String reportName){
        List list = sqlSessionTemplate.selectList("getTestReportsForName",reportName);
        JSONObject object = new JSONObject();
        object.put("report",list);
        return object.toJSONString();
    }

    @RequestMapping(value = "uploadTestReport",method = RequestMethod.POST)
    @ApiOperation(value = "上传测试报告",httpMethod = "POST")
    public String uploadTestReport(MultipartFile file){
        if (file.isEmpty()) {
            return "文件为空";
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        log.info("上传的文件名为：" + fileName);

        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        log.info("上传的后缀名为：" + suffixName);
        if(!".html".equals(suffixName)){
            return "文件后缀名错误，必须是html";
        }

        //获取上传测试报告的ID号码
        String reportId = fileName.split("\\.")[0].split("_")[1];
        log.info("上传测试报告的ID号码是："+reportId);

        // 文件上传后的路径
        String filePath = new FileBean().getFilePath();

        //每一个项目id创建一个文件夹
        dest = new File(filePath + reportId + "/" + fileName);
        // 检测是否存在目录
        log.info("create dir :" + filePath + reportId + "/" + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();

        }

        //拼接测试报告路径
        String reportPath = reportId + "/" + fileName;

        try {
            file.transferTo(dest);
            //获取测试报告中的名字，获取名字的同时会自动解析报告并且入库信息。
            String reportName = getReportName(reportPath,reportId);
            return reportName;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败";

    }


    @RequestMapping(value = "saveTestProcess",method = RequestMethod.POST)
    @ApiOperation(value = "保存测试过程",httpMethod = "POST")
    public String saveTestProcess(@RequestBody String processHtml) throws IOException {
        JSONObject object = JSONObject.parseObject(processHtml);
        String htmlReport = object.getString("processHtml");
        String reportId = object.getString("reportId");
        System.out.println(htmlReport);
        System.out.println(reportId);

//        测试过程要单独存储，存储成html文件后并将相对路径地址存储到数据库中
//        存储用户输入的测试过程文件
        String tomcatServerPath = FileBean.getFilePath();
        String testReportFolder = tomcatServerPath + reportId;
        String testProcessPathInputMysql = saveTestProcessFileToServer(htmlReport, reportId, testReportFolder);


        //拼接原始测试报告和测试过程报告到最终测试报告中
        saveFinalReportFileToServer(htmlReport, reportId, testReportFolder);

        String testFinalPathInputMysql = reportId + "/final_"+reportId+".html";

        //存储成html格式后，把相对路径写入到数据库中 测试过程报告ProcessReportPath 最终测试报告 FinaleReportPath
        int saveSuccess = savePathToMysql(reportId, testProcessPathInputMysql, testFinalPathInputMysql);
        if(saveSuccess>0){
            return "保存成功";
        }else {
            return "保存失败";
        }
    }

    private int savePathToMysql(String reportId, String testProcessPathInputMysql, String testFinalPathInputMysql) {
        UploadHtmlInfo info = new UploadHtmlInfo();
        info.setProcessReportPath(testProcessPathInputMysql);
        info.setFinalReportPath(testFinalPathInputMysql);
        info.setReportId(reportId);
        int saveSuccess = sqlSessionTemplate.update("updateProcessAndFinalReportPath",info);
        return saveSuccess;
    }

    private void saveFinalReportFileToServer(String htmlReport, String reportId, String testReportFolder) throws IOException {
        BufferedReader destReader = new BufferedReader(new InputStreamReader(new FileInputStream(dest)));
        StringBuilder destBuilder = new StringBuilder();
        File finalReportFile = new File(testReportFolder + "/final_"+reportId+".html");
        if(finalReportFile.exists()){
            finalReportFile.delete();
        }

        String destBuffer = null;
        while((destBuffer=destReader.readLine())!=null){
            destBuilder.append(destBuffer);
        }
        BufferedWriter finalReportWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(finalReportFile,true)));
        finalReportWriter.write(destBuilder.toString());
        finalReportWriter.write(htmlReport);
        finalReportWriter.close();
    }

    private String saveTestProcessFileToServer(String htmlReport, String reportId, String testReportFolder) throws IOException {
        String testProcessFilename = "process_" + reportId + ".html";
        File testProcessFile = new File(testReportFolder + "/" + testProcessFilename);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(testProcessFile)));
        writer.write(htmlReport);
        writer.close();
        //存储测试过程文件路径到数据库中
        String testProcessPathInputMysql = reportId + "/" + testProcessFilename;
        return testProcessPathInputMysql;
    }


    /**
     * 解析html，获取报告名称
     * @param fileReportPath
     * @return
     */
    public  String getReportName(String fileReportPath,String reportId){
        FileBean bean = new FileBean();
        //拿到测试报告的html服务器的路径，这个路径是存储在application.yml配置文件中的
        String sercivePath = FileBean.getReportHtmlService();
        //拼接完整的url,这个url就可以直接访问上传的测试报告了
        String url = sercivePath + fileReportPath;
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String reportName = getReportName(doc);

        Boolean saveSuccess = saveReportMessage(doc,fileReportPath,reportId);
        if(false == saveSuccess){
            throw new RuntimeException("报告数据入库失败");
        }

        return reportName;
    }

    //获取项目名字
    private  String getReportName(Document doc) {
        Elements elements = doc.getElementsByAttributeValueMatching("href","http");
        Element element = elements.get(0);
        return element.text();
    }

    /**
     * 解析html文件，并且把信息入库
     * @param doc
     * @return
     */
    private  boolean saveReportMessage(Document doc,String fileReportPath,String reportId){
        UploadHtmlInfo info = new UploadHtmlInfo();

        //存储测试报告路径  1、原始测试报告 fileReportPath
        //测试报告路径分为三个。1、原始测试报告，2、测试过程报告ProcessReportPath 3、最终测试报告 FinaleReportPath
        //其中2、测试过程报告ProcessReportPath 3、最终测试报告 FinaleReportPath 在保存测试过程报告的时候进行设置
        //设置上传的原始测试报告路径
        info.setFileReportPath(fileReportPath);

        //设置对应icafe平台项目id
        info.setReportId(reportId);

        //获取项目名称
        String reportName = getReportName(doc);
        info.setReportName(reportName);

        //获取QA名字 QAName
        String QAName = getQAName(doc);
        info.setQAName(QAName);

        //获取RD名字 RDName
        String RDName = getRDName(doc);
        info.setRDName(RDName);

        //获取报告编写时间   ReportWriteTime
        String reportWriteTime = getReportWriteTime(doc);
        info.setReportWriteTime(reportWriteTime);

        //获取测试轮数   TestCount
        String testCount = getTestCount(doc);
        info.setTestCount(testCount);

        //获取测试结论
        String testConclusion = getTestConclusion(doc);
        info.setTestConclusion(testConclusion);

        //向数据库中插入数据
        int successCount = sqlSessionTemplate.insert("insertHtmlInfo",info);
        if(successCount>0){
            return true;
        }else {
            return false;
        }
    }

    private  String getTestConclusion(Document doc) {
        Elements elements = doc.getElementsByTag("h3");
        String testConclusionText = elements.get(0).child(0).text();
        String testConclusion = testConclusionText.substring(testConclusionText.lastIndexOf(" ")).trim();
        return testConclusion;
    }

    private  String getTestCount(Document doc) {

        //集合当中第5个元素是测试轮数
        return getNextTdTagValue(doc,4);
    }

    private  String getReportWriteTime(Document doc) {
        return getNextTdTagValue(doc,5);
    }


    private  String getRDName(Document doc) {
        //获取html中td标签中的值
        String htmlTestalue = getNextTdTagValue(doc,2);
        return htmlTestalue.substring(htmlTestalue.lastIndexOf(" ")).trim();
    }

    private  String getQAName(Document doc) {
        //获取html中td标签中的值
        String htmlTestalue = getNextTdTagValue(doc,3);
        return htmlTestalue.substring(htmlTestalue.lastIndexOf(" ")).trim();
    }

    /**
     * 获取下一个td标签中的值
     */
    private  String getNextTdTagValue(Document doc, int i) {
        Elements elements = doc.getElementsByTag("strong");
        Element element = elements.get(i);
        Elements children = element.parent().parent().getAllElements();
        String text = children.get(3).text();
        return text;
    }

}
