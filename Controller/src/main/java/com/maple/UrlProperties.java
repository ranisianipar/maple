package com.maple;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "url")
@Getter
public class UrlProperties {
    private String base;
    private String employeePrefix;
    private String itemPrefix;
    private String assignmentPrefix;
}
