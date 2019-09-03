package com.sahajsoft.bigo.queueintessential.consumer;

import com.sahajsoft.bigo.queueintessential.consumer.config.ConsumerProperties;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class FileMessageConsumer implements Consumer {

  private ConsumerProperties properties;

  @Autowired
  public FileMessageConsumer(ConsumerProperties properties) {
    this.properties = properties;
  }

  @Override
  public Path receive(String message) {
    File folder = properties.getFileDestinationFolder();
    if (!folder.exists()) {
      folder.mkdir();
    }
    Path filePath = null;
    try {
      JSONObject jsonMessage = new JSONObject(message);
      String uuid = jsonMessage.getString("message_id");
      filePath = Files.write(Paths.get(folder.getAbsolutePath() + "/" + uuid + ".json"), message.getBytes());
      log.info("success-" + filePath.toString());
    } catch (Exception e) {
      log.error("Failed to create file for message - " + message);
    }
    return filePath;
  }
}
