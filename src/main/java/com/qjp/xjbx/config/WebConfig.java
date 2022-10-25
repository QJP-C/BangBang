package com.qjp.xjbx.config;


import com.qjp.xjbx.common.JacksonObjectMapper;
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
    public JWTInterceptor jwtInterceptor(){
        return new JWTInterceptor();
    }

     @Override
     public void addCorsMappings(CorsRegistry registry) {
     registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowCredentials(true)
            .maxAge(3600);
}
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> addPathPatterns = new ArrayList<>();
        List<String> excludePathPatterns = new ArrayList<>();

        //拦截所有请求
        addPathPatterns.add("/**");

        //不需要拦截的请求
        excludePathPatterns.add("/user/login");
        excludePathPatterns.add("/user/register");
        excludePathPatterns.add("/user/email");
        excludePathPatterns.add("/user/yz");
        excludePathPatterns.add("/favicon.ico");
        excludePathPatterns.add("/imserver/**");
        excludePathPatterns.add("/files/**");
        excludePathPatterns.add("/alipay/**");
        excludePathPatterns.add("/doc.html");
        excludePathPatterns.add("/webjars/**");
        excludePathPatterns.add("/index.html");
        excludePathPatterns.add("/swagger-resources/**");
        excludePathPatterns.add("/user/sms/**");






        registry.addInterceptor(jwtInterceptor())
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
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/img/");
        registry.addResourceHandler("/img/**").addResourceLocations("file:E:\\xiangmu\\xjbx\\src\\main\\resources\\static\\img\\");

    }

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
      log.info("扩展消息转换器。。");
      //  创建消息转化器
      MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
       // 设置对象转化器、底层使用Jackson将Java对象转换为json
      messageConverter.setObjectMapper(new JacksonObjectMapper());
      //  将上面的消息转化器对象追加到mvc框架的转换器集合中    设置索引（优先级）
      converters.add(0,messageConverter);
  }

}
