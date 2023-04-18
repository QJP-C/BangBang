package com.qjp.bang.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Bean
    public JwtInterceptor getJwtInterceptor(){
        return new JwtInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> addPathPatterns = new ArrayList<>();
        List<String> excludePathPatterns = new ArrayList<>();

        //拦截所有请求
        addPathPatterns.add("/**");

        //不需要拦截的请求
        excludePathPatterns.add("/swagger-resources/**");
        excludePathPatterns.add("/webjars/**");
        excludePathPatterns.add("/v2/**");
        excludePathPatterns.add("/swagger-ui.html/**");
        excludePathPatterns.add("/swagger-ui.html#/");
        excludePathPatterns.add("/swagger-ui.html#/**");
        excludePathPatterns.add("/swagger-ui.html**/**");
        excludePathPatterns.add("/swagger-ui.html");
        excludePathPatterns.add("/v2/api-doc");
        excludePathPatterns.add("null/swagger-resources/configuration/ui");
        excludePathPatterns.add("/");
        excludePathPatterns.add("/error");
        excludePathPatterns.add("/csrf");
        excludePathPatterns.add("/index.html");
        excludePathPatterns.add("/null/swagger-resources");
        excludePathPatterns.add("/null/swagger-resources**");



        excludePathPatterns.add("/favicon.ico");
        excludePathPatterns.add("/user/login");
        excludePathPatterns.add("/user/send");
        excludePathPatterns.add("/mo/upload");
        excludePathPatterns.add("/taskClass");
        excludePathPatterns.add("/task/new");
        excludePathPatterns.add("/taskCollect/collect/**");
        excludePathPatterns.add("/task/one/**");
        excludePathPatterns.add("/gpt");


        registry.addInterceptor(getJwtInterceptor())
                .addPathPatterns(addPathPatterns)   //拦截验证token
                .excludePathPatterns(excludePathPatterns);      //放行

    }

    /**
     * 添加静态资源文件
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/img/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger-resources/");

        registry.addResourceHandler("/swagger/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger/");

        registry.addResourceHandler("/v2/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/v2/api-docs/");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
//                .allowCredentials(true)
                .maxAge(3600);
    }


    /**
     * 扩展mvc框架的消息转换器
     *
     * @param converters
     */

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //  创建消息转化器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转化器、底层使用Jackson将Java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //  将上面的消息转化器对象追加到mvc框架的转换器集合中    设置索引（优先级）
        converters.add(0, messageConverter);
    }

}
