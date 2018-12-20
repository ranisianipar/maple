package com.maple;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class Constant {
    //origin
    public static final String LINK_ORIGIN = "http://localhost";

    //general
    public static final String LINK_ID_PARAM = "/{id:.+}";

    // assignment
    public static final String LINK_ASSIGNMENT_PREFIX = "/assignment";
    public static final String LINK_UPDATE_STATUS = "/{id:.+}/status";

    // employee
    public static final String LINK_EMPLOYEE_PREFIX = "/employee";

    //item
    public static final String LINK_ITEM_PREFIX = "/item";
    public static final String LINK_ITEM_DOWNLOAD = "/{id:.+}/download";

}
