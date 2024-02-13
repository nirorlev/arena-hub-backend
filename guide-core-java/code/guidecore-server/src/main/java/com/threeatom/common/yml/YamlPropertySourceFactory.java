//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.yml;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {
    public YamlPropertySourceFactory() {}

    public PropertySource<?> createPropertySource(String name, EncodedResource resource)
            throws IOException {
        String sourceName =
                (String) Optional.ofNullable(name).orElse(resource.getResource().getFilename());
        if (!resource.getResource().exists()) {
            return new PropertiesPropertySource(sourceName, new Properties());
        } else if (!sourceName.endsWith(".yml") && !sourceName.endsWith(".yaml")) {
            return super.createPropertySource(name, resource);
        } else {
            Properties propertiesFromYaml = this.loadYaml(resource);
            return new PropertiesPropertySource(sourceName, propertiesFromYaml);
        }
    }

    private Properties loadYaml(EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new Resource[] {resource.getResource()});
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
