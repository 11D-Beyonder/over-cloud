package org.joe.cloud.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {
    private final OpenApiExtensionResolver openApiExtensionResolver;

    @Autowired
    public Knife4jConfig(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }

    @Bean
    public Docket openApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("网站前端接口分组")
                .apiInfo(
                        new ApiInfoBuilder()
                                .title("OverCloud API")
                                .description("基于SpringBoot和Vue框架开发的Web文件系统，旨在为用户提供一个简单、方便的文件存储方案，能够以完善的目录结构体系，对文件进行管理 。")
                                .version("1.0")
                                .contact(new Contact("Tianze Zhu", "11d-beyonder.github.io", "ztz20001117@126.com"))
                                .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.joe.cloud.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(openApiExtensionResolver.buildSettingExtensions());
    }
}
