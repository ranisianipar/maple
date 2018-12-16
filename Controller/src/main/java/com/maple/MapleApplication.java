package com.maple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MapleApplication {
	private static final Logger log = LoggerFactory.getLogger(MapleApplication.class);

	public static void main(String[] args) { SpringApplication.run(MapleApplication.class, args); }
}
