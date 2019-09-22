package com.sahajsoft.bigo.queueintessential.consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

public class ConsumerServerTest {

  @Test
  public void test() throws IOException, InterruptedException {
    Consumer consumer = Mockito.mock(Consumer.class);
    ConsumerServer consumerServer = new ConsumerServer(consumer);
    new Thread(() -> {
      try {
        consumerServer.start("localhost", 8888);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
    Thread.sleep(3000);
    SocketChannel socketChannel = SocketChannel.open(serverAddress());
    Assertions.assertTrue(socketChannel.isConnected());
  }

  private InetSocketAddress serverAddress() throws UnknownHostException {
    byte[] byteArr = new byte[]{(byte) 127, (byte) 0, (byte) 0, (byte) 1};
    return new InetSocketAddress(InetAddress.getByAddress("localhost", byteArr), 8888);
  }
}