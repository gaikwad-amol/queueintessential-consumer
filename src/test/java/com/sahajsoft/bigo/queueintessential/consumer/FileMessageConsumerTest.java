package com.sahajsoft.bigo.queueintessential.consumer;

import com.sahajsoft.bigo.queueintessential.consumer.config.ConsumerProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileMessageConsumerTest {

  private ConsumerProperties properties;
  private File testFolderToWriteFiles;

  @BeforeEach
  public void setUp() {
    properties = mock(ConsumerProperties.class);
    testFolderToWriteFiles = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("TestFolderToWriteFiles")).getFile());
  }

  @AfterEach
  public void cleanUp() {
    cleanTestFolderToWriteFiles();
  }

  @Test
  public void shouldCreateFileWithReceivedMessageAsContent() {
    testFolderToWriteFiles = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("TestFolderToWriteFiles")).getFile());
    when(properties.getFileDestinationFolder()).thenReturn(testFolderToWriteFiles);
    FileMessageConsumer fileMessageConsumer = new FileMessageConsumer(properties);
    Path filePath = fileMessageConsumer.receive("{'message_id':'testFile'}");
    System.out.println(filePath.getFileName());
    assertEquals("testFile.json", filePath.getFileName().toString());
  }

  private void cleanTestFolderToWriteFiles() {
    for (File file : Objects.requireNonNull(testFolderToWriteFiles.listFiles())) {
      if (file.getName().equals("EmptyFile.json")) {
        continue;
      }
      file.delete();
    }
  }

}