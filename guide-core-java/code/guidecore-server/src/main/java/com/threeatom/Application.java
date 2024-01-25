package com.threeatom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@MapperScan("com.threeatom.*.mapper")
@MapperScan("com.threeatom.common.database")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


    }

//public class Application extends SpringBootServletInitializer {
//	public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//
//
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(Application.class);
//    }


//	@Bean
//    public TomcatServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint constraint = new SecurityConstraint();
//                constraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                constraint.addCollection(collection);
//                context.addConstraint(constraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(httpConnector());
//        return tomcat;
//    }
//
//    @Bean
//    public Connector httpConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        //Connector监听的http的端口号
//        connector.setPort(80);
//        connector.setSecure(false);
//        //监听到http的端口号后转向到的https的端口号
//        connector.setRedirectPort(443);
//        return connector;
//    }

	
	
}
