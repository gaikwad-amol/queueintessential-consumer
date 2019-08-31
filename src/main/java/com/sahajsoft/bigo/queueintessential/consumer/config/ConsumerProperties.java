package com.sahajsoft.bigo.queueintessential.consumer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Objects;

@Configuration
@PropertySource("classpath:application.properties")
public class ConsumerProperties {

  private Environment environment;

  @Autowired
  public ConsumerProperties(Environment environment) {
    this.environment = environment;
  }

  public Integer getBrokerSocketPort() {
    return Integer.valueOf(Objects.requireNonNull(environment.getProperty("consumer.socket.port")));
  }

  public File getFileDestinationFolder() {
    return new File(Objects.requireNonNull(environment.getProperty("consumer.write.folder")));
  }

}
