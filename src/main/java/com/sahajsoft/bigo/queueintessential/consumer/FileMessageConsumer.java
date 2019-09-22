package com.sahajsoft.bigo.queueintessential.consumer;

import com.sahajsoft.bigo.queueintessential.consumer.config.ConsumerProperties;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Component
public class FileMessageConsumer implements Consumer {

  private ConsumerProperties properties;
  private BlockingQueue<String> queue;
  private File folder;

  @Autowired
  public FileMessageConsumer(ConsumerProperties properties) {
    this.properties = properties;
    folder = properties.getFileDestinationFolder();
    if (!folder.exists()) {
      folder.mkdir();
    }
    queue = new LinkedBlockingDeque<>(properties.getQueueCapacity());
    writeFile();
  }

  @Override
  public Path receive(String message) {
    Path filePath = null;
    try {
      queue.put(message);
    } catch (Exception e) {
      log.error("Failed before to create file for message - " + message, e);
    }
    return filePath;
  }

  private void writeFile() {
    int threads = properties.threads();
    ExecutorService executorService = Executors.newFixedThreadPool(threads);
    for (int i = 0; i < threads; i++) {
      executorService.submit(new MessageWriter());
    }
  }

  class MessageWriter implements Runnable {

    @Override
    public void run() {
      while (true) {
        String message = queue.poll();
        if (!StringUtils.isEmpty(message)) {
          if (message.equals("LAST")) {
            log.info("Done");
          } else {
            JSONObject jsonMessage = new JSONObject(message);
            String uuid = jsonMessage.getString("message_id");
            try {
              Files.write(Paths.get(folder.getAbsolutePath() + "/" + uuid + ".json"), message.getBytes());
            } catch (IOException e) {
              log.error("Failed to create file for message - " + message, e);
            }
          }

        }
      }
    }
  }
}
