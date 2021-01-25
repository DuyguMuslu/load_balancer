package com.balancer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConfigProvider {

    static Logger logger = LoggerFactory.getLogger(com.balancer.config.ConfigProvider.class);

    private ApplicationConfig config;

    private ConfigProvider() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        try {
            config = mapper.readValue(new File("src/main/resources/application.yml"), ApplicationConfig.class);
        } catch (IOException e) {
            logger.error(String.format("error while loading config : %s", e.toString()));
        }
    }

    private static class ApplicationConfigHelper {
        private static final com.balancer.config.ConfigProvider INSTANCE = new com.balancer.config.ConfigProvider();
    }

    public static ApplicationConfig getConfig() {
        return ApplicationConfigHelper.INSTANCE.config;
    }

}