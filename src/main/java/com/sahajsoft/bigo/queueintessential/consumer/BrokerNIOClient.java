package com.sahajsoft.bigo.queueintessential.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class BrokerNIOClient {

  private SocketChannel socketChannel;

  private Consumer consumer;
  MessageReader messageReader = new MessageReader();
 //ByteBuffer buffer = ByteBuffer.allocate(1024);
  private int count = 0;

  @Autowired
  public BrokerNIOClient(Consumer consumer) {
    this.consumer = consumer;
  }

  public void startConnection(String ip, int port) throws IOException {
    InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
    socketChannel = SocketChannel.open();
    socketChannel.connect(inetSocketAddress);
    log.info("is connected - " + socketChannel.isConnected());
  }

  public void receiveMessage() {
    ByteBuffer buffer = ByteBuffer.allocate(4096);
    //buffer.clear();
    while (true) {
      try {
        if (!(socketChannel.read(buffer) > 0)) break;
      } catch (IOException e) {
        log.error("error occurred while reading socket ",e);
      }
      buffer.flip();
      //      if(buffer.hasArray()) {
//        receivedText = new String(buffer.array()).trim();
//      }
      //String receivedText = new String(buffer.array()).trim();
      //String receivedText = bb_to_str(buffer, StandardCharsets.UTF_8);

      //log.info("receivedText - " + receivedText);
      String receivedText = new String(buffer.array()).trim();
      List<String> messages = messageReader.extractMessage(receivedText);
      messages.forEach(message -> {
        count++;
//        System.out.println("consumer received " + count);
        consumer.receive(message);
      });
      buffer = ByteBuffer.allocate(4096);
      //consumer.receive(receivedText);
      //buffer.clear();
      //System.out.println("buffer after receiving receivedText : " + buffer.hashCode());
    }

  }

  public String bb_to_str(ByteBuffer buffer, Charset charset) {
    byte[] bytes;
    if (buffer.hasArray()) {
      bytes = buffer.array();
    } else {
      bytes = new byte[buffer.remaining()];
      buffer.get(bytes);
    }
    return new String(bytes, charset);
  }
}
