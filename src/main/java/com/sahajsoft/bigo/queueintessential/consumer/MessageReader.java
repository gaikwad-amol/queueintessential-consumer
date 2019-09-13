package com.sahajsoft.bigo.queueintessential.consumer;

import java.util.ArrayList;
import java.util.List;

public class MessageReader {

  StringBuilder builder = new StringBuilder();

  public List<String> extractMessage(String message) {
    if (builder.length() > 0) {
      message = builder.toString().concat(message);
      builder = new StringBuilder();
    }
    List<String> messages = new ArrayList<>();
    if (message.endsWith("<END>")) {
      String[] split = message.split("<END>");

      for (int index = 0; index < split.length; index++) {
        messages.add(split[index]);
      }
    } else {
      String[] split = message.split("<END>");
      for (int index = 0; index < split.length - 1; index++) {
        messages.add(split[index]);
      }
      builder = new StringBuilder(split[split.length - 1]);
    }
    //messages.forEach(m-> System.out.println("created message - " + m));
    //System.out.println("partial message - " + builder.toString());
    return messages;
  }

  public List<String> extractMessage2(String message) {
    List<String> messages = new ArrayList<>();
    if (message.endsWith("<END>")) {
      String[] split = message.split("<END>");

      for (int index = 0; index < split.length; index++) {
        if (index == 0 && builder.length() > 0) {
          messages.add(builder.append(split[index]).toString());
          builder = new StringBuilder();
        } else {
          messages.add(split[index]);
        }
      }
    } else {
      String[] split = message.split("<END>");
      for (int index = 0; index < split.length - 1; index++) {
        if (index == 0 && builder.length() > 0) {
          messages.add(builder.append(split[index]).toString());
          builder = new StringBuilder();
        } else {
          messages.add(split[index]);
        }
      }
      builder.append(split[split.length - 1]);
    }
    return messages;
  }

  public String partialMessage() {
    return builder.toString();
  }
}
