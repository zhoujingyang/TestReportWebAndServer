package com.baidu.ocr.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "/report/v1/", description = "测试报告平台分页相关api")
@RequestMapping("/report/v1")
public class Pages {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @RequestMapping(value = "getTotalPages",method = RequestMethod.GET)
    @ApiOperation(value = "获取报告总条数" , httpMethod = "GET")
    public int getTotalPages(){
        int count = sqlSessionTemplate.selectOne("getTotalPages");
        return count;
    }


    @RequestMapping(value = "getCurrentPagesInfo",method = RequestMethod.GET)
    @ApiOperation(value = "获取当前页面的测试报告信息",httpMethod = "GET")
    public String getCurrentPagesInfo(@RequestParam(required = true) int start ,
                                      @RequestParam(required = true) int end){
        Map map = new HashMap();
        map.put("start",start);
        map.put("end",end);
        List list = sqlSessionTemplate.selectList("getCurrentPagesInfo",map);
        return null;
    }
}
