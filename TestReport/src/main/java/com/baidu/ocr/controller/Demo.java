package com.baidu.ocr.controller;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ocr.model.demo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Api(value = "/report/v1/", description = "测试报告平台api")
@RequestMapping("/report/v1/")
public class Demo {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;


    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    @RequestMapping(value = "getDemo", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ApiOperation(value = "这是一个演示get请求的方法", httpMethod = "GET", notes = "这是一个演示get请求的方法")
    public String getOrderList(@RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) String name) {

    List<demo> list = sqlSessionTemplate.selectList("getUser");

        JSONObject object = new JSONObject();
        object.put("data",list);
        return object.toJSONString();
    }

}
