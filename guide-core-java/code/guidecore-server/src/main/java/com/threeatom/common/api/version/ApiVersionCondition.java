//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.api.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    private static final Pattern VERSION_PREFIX_PATTERN = Pattern.compile("/v(\\d+)/");
    private int apiVersion;

    public ApiVersionCondition(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public int getAPiVersion() {
        return this.apiVersion;
    }

    public ApiVersionCondition combine(ApiVersionCondition other) {
        return new ApiVersionCondition(other.getAPiVersion());
    }

    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        Matcher m = VERSION_PREFIX_PATTERN.matcher(request.getRequestURI());
        if (m.find()) {
            Double version = Double.valueOf(m.group(1));
            if (version >= (double) this.apiVersion) {
                return this;
            }
        }

        return null;
    }

    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        return other.getAPiVersion() - this.getAPiVersion();
    }
}
