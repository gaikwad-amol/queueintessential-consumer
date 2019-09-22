package com.sahajsoft.bigo.queueintessential.consumer.config;

import com.sahajsoft.bigo.queueintessential.consumer.ConsumerServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@EnableAutoConfiguration
@ComponentScan("com.sahajsoft.bigo.queueintessential.consumer")
@Slf4j
public class ConsumerApplication {

  @RequestMapping("/")
  String home() {
    return "Hello I am consumer!";
  }

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext = SpringApplication.run(ConsumerApplication.class, args);
    ConsumerProperties properties = applicationContext.getBean(ConsumerProperties.class);
    try {
      new Thread(() -> {
        try {
          ConsumerServer consumerServer = applicationContext.getBean(ConsumerServer.class);
          consumerServer.start(properties.getHostName(), properties.getBrokerPort());
        } catch (IOException e) {
          log.error("Exception occurred while starting consumer socket server", e);
        }
      }).start();
    } catch (Exception e) {
      log.error("Error occurred while starting the consumer ,", e);
    }

  }
}