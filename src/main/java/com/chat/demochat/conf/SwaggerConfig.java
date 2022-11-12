package com.chat.demochat.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class SwaggerConfig
{
    @Bean
    public Docket customDocket()
    {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.chat.demochat.controller"))//扫描的包路径
                .build();
    }

    @Bean
    public ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                .title("聊天工具")//文档说明
                .version("1.0.0")//文档版本说明
                .build();
    }


}
