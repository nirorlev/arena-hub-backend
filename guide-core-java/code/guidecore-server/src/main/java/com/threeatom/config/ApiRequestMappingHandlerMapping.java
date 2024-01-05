package com.threeatom.config;
 
//import com.threeatom.guidecore.annotion.ApiVersion;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
 
import java.lang.reflect.Method;
 
public class ApiRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
  /*  private static final String VERSION_FLAG = "{version}";

    //自定义匹配的处理器
    //
    //创建 ApiRequestMappingHandlerMapping 类，重写部分 RequestMappingHandlerMapping 的方法
    private static RequestCondition<ApiVersionCondition> createCondition(Class<?> clazz) {
        RequestMapping classRequestMapping = clazz.getAnnotation(RequestMapping.class);
        if (classRequestMapping == null) {
            return null;
        }
        StringBuilder mappingUrlBuilder = new StringBuilder();
        if (classRequestMapping.value().length > 0) {
            mappingUrlBuilder.append(classRequestMapping.value()[0]);
        }
        String mappingUrl = mappingUrlBuilder.toString();
        if (!mappingUrl.contains(VERSION_FLAG)) {
            return null;
        }
        ApiVersion apiVersion = clazz.getAnnotation(ApiVersion.class);
        return apiVersion == null ? new ApiVersionCondition(1) : new ApiVersionCondition(apiVersion.value());
    }
 
    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return createCondition(method.getClass());
    }
 
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return createCondition(handlerType);
    }*/
}