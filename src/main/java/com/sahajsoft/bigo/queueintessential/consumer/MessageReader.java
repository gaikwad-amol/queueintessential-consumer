package com.sahajsoft.bigo.queueintessential.consumer;

import java.util.ArrayList;
import java.util.List;

public class MessageReader {

  private static final String MESSAGE_SEPARATOR = "<END>";
  StringBuilder builder = new StringBuilder();

  public List<String> extractMessage(String message) {
    if (builder.length() > 0) {
      message = builder.toString().concat(message);
      builder = new StringBuilder();
    }
    List<String> messages = new ArrayList<>();
    if (message.endsWith(MESSAGE_SEPARATOR)) {
      String[] split = message.split(MESSAGE_SEPARATOR);

      for (int index = 0; index < split.length; index++) {
        messages.add(split[index]);
      }
    } else {
      String[] split = message.split(MESSAGE_SEPARATOR);
      for (int index = 0; index < split.length - 1; index++) {
        messages.add(split[index]);
      }
      builder = new StringBuilder(split[split.length - 1]);
    }
    return messages;
  }

  public String partialMessage() {
    return builder.toString();
  }
}
