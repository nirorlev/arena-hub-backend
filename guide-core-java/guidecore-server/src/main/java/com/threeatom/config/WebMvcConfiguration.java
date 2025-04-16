package com.threeatom.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${spring.i18n:'i18n/default/default'}")
    private String i18nPath;

    @Value("${spring.timezone:'UTC+8'}")
    private String timeZone;

    @Bean
    public HttpMessageConverters fastJsonMessageConverters() {

        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        // 全局配置将会导致对象上的注解无效
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        // 时区设置
        JSON.defaultTimeZone = TimeZone.getTimeZone(timeZone);
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        // 在convert中添加配置信息.
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(0, fastConverter);
        return new HttpMessageConverters(converters);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        Locale.setDefault(new Locale("zh", "CN"));
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename(i18nPath);
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("utf-8");
        return source;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, OffsetDateTime>() {
            @Override
            public OffsetDateTime convert(String source) {
                return OffsetDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
        });
    }
}
