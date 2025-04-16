package com.threeatom.system.plugin.service;

import com.threeatom.system.plugin.UserHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginInstant {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginInstant.class);

    @Autowired(required = false)
    private UserHelper userTokenHelper;

    public PluginInstant() {}

    public UserHelper getUserHelper() {
        return this.userTokenHelper;
    }
}
