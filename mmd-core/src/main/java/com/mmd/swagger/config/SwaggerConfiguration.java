package com.mmd.swagger.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mmd.constant.CommonConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author dscc
 * @date 2020年3月24日
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "swagger",value = {"enable"},havingValue = "true")
public class SwaggerConfiguration {

    //为空时选定默认值
    private static final String BASE_PACKAGE = "com.mmd";
    private static final String BASE_PATH = "/**";
    private static final List<String> EXCLUDE_PATH = Arrays.asList("/error");
    private static final String TITLE = "魔魔达";
    private static final String DESCRIPTION = "魔魔达接口文档";
    private static final String VERSION = "0.0.1-SNAPSHOT";

    @Bean
    public SwaggerProperties swaggerProperties(){
        return new SwaggerProperties();
    }

    @Bean
    public Docket api(SwaggerProperties swaggerProperties){
        if(StringUtils.isEmpty(swaggerProperties.getBasePackage())){
            swaggerProperties.setBasePackage(BASE_PACKAGE);
        }
        if(swaggerProperties.getBasePath().isEmpty()){
            swaggerProperties.getBasePath().add(BASE_PATH);
        }
        List<Predicate<String>> basePath = new ArrayList();
        swaggerProperties.getBasePath().forEach(path -> basePath.add(PathSelectors.ant(path)));
        if(swaggerProperties.getExcludePath().isEmpty()){
            swaggerProperties.getExcludePath().addAll(EXCLUDE_PATH);
        }
        List<Predicate<String>> excludePath = new ArrayList<>();
        swaggerProperties.getExcludePath().forEach(path -> excludePath.add(PathSelectors.ant(path)));
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(swaggerProperties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(Predicates.and(Predicates.not(Predicates.or(excludePath)), Predicates.or(basePath)))
                .build()
                .securitySchemes(security());
    }

    private ApiInfo apiInfo(SwaggerProperties swaggerProperties){
        if(StringUtils.isEmpty(swaggerProperties.getTitle())){
            swaggerProperties.setTitle(TITLE);
        }
        if(StringUtils.isEmpty(swaggerProperties.getDescription())){
            swaggerProperties.setDescription(DESCRIPTION);
        }
        if(StringUtils.isEmpty(swaggerProperties.getVersion())){
            swaggerProperties.setVersion(VERSION);
        }
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .build();
    }

    private List<ApiKey> security() {
        return newArrayList(
                new ApiKey(CommonConstant.TOKEN_HEADER, CommonConstant.TOKEN_HEADER, "header")
        );
    }

}
