package com.baidu.ocr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .pathMapping("/")
                .select()
                .paths(PathSelectors.regex("/report.*"))
                .build();
    }

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder().title("OCR test Report service")
                .contact(new Contact("zhoujingyang", "", "zhoujingyang01@baidu.com"))
                .description("this is OCR test Report service")
                .version("1.0")
                .build();
    }
}
