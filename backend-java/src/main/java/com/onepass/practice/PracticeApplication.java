package com.onepass.practice;

import com.onepass.practice.auth.JwtProperties;
import com.onepass.practice.config.WebCorsProperties;
import com.onepass.practice.contentimport.ContentImportProperties;
import com.onepass.practice.practice.PracticeProperties;
import com.onepass.practice.wechat.WechatMiniProgramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackageClasses = {
        JwtProperties.class,
        WebCorsProperties.class,
        PracticeProperties.class,
        WechatMiniProgramProperties.class,
        ContentImportProperties.class
})
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class PracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeApplication.class, args);
    }
}
