package com.baidu.ocr.config;


import com.baidu.ocr.model.FileBean;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@Configuration
@EnableTransactionManagement
public class FileConfig  implements EnvironmentAware {
    private RelaxedPropertyResolver propertyResolver;

    @Bean(name = "fileBean")
    public FileBean fileBeanFactory() throws IOException {

        //自动装在FileBean，启动时就加载内容，映射到bean中
        FileBean bean = new FileBean();
        FileBean.setFilePath(propertyResolver.getProperty("path"));
        FileBean.setReportHtmlService(propertyResolver.getProperty("reportHtmlService"));

        return  bean;
    }


    @Override
    public void setEnvironment(Environment environment) {
        //自动匹配application.yml配置文件中以file开头的配置内容
        propertyResolver = new RelaxedPropertyResolver(environment, "file.");
    }
}
