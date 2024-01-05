//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.api;

import com.threeatom.common.api.version.ApiVersion;
import com.threeatom.common.api.version.ApiVersionCondition;
import java.lang.reflect.Method;
import java.util.Objects;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    public CustomRequestMappingHandlerMapping() {
    }

    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion apiVersion = (ApiVersion)AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return this.createRequestCondition(apiVersion);
    }

    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = (ApiVersion)AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return this.createRequestCondition(apiVersion);
    }

    private RequestCondition<ApiVersionCondition> createRequestCondition(ApiVersion apiVersion) {
        if (Objects.isNull(apiVersion)) {
            return null;
        } else {
            int value = apiVersion.value();
            Assert.isTrue(value >= 1, "Api Version Must be greater than or equal to 1");
            return new ApiVersionCondition(value);
        }
    }
}
