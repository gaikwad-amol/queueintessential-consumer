package com.sahajsoft.bigo.queueintessential.consumer;

import com.sahajsoft.bigo.queueintessential.consumer.config.ConsumerProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

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
  public void shouldCreateFileWithReceivedMessageAsContent() throws InterruptedException {
    testFolderToWriteFiles = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("TestFolderToWriteFiles")).getFile());
    when(properties.getFileDestinationFolder()).thenReturn(testFolderToWriteFiles);
    when(properties.getQueueCapacity()).thenReturn(1000);
    when(properties.threads()).thenReturn(2);
    FileMessageConsumer fileMessageConsumer = new FileMessageConsumer(properties);
    fileMessageConsumer.receive("{'message_id':'testFile'}");
    Thread.sleep(5000);
    Optional<File> first = Arrays.stream(testFolderToWriteFiles.listFiles()).filter(s -> s.getName().equals("testFile.json")).findFirst();
    assertEquals("testFile.json", first.get().getName());
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