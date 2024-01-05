package com.threeatom.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebCorsConfiguration {
	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration=new CorsConfiguration();
		
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addExposedHeader("Content-Disposition");
		return corsConfiguration;
	}
	
	@Bean
	public  FilterRegistrationBean<CorsFilter> corsFilter() {
		UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig());
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
	    bean.setOrder(0);//配置CorsFilter优先级
		return bean;
	}
}
