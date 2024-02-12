package com.threeatom.config;

import com.threeatom.common.config.SystemConfig;
import com.threeatom.system.plugin.UserHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfiguration extends SystemConfig {

    @Bean(name = "userHelper")
    UserHelper initUserTokenHelper() {
        return null;
    }

    @Override
    public String getBusinessKey() {
        // TODO Auto-generated method stub
        return "guidecore";
    }
}
