package com.sahajsoft.bigo.queueintessential.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class ConsumerServer {

  private Consumer consumer;

  private ServerSocketChannel serverSocketChannel;
  private MessageReader messageReader = new MessageReader();

  @Autowired
  public ConsumerServer(Consumer consumer) {
    this.consumer = consumer;
  }

  public void start(String hostname, int port) throws IOException {
    Selector selector = Selector.open();
    serverSocketChannel = ServerSocketChannel.open();
    InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);

    serverSocketChannel.bind(inetSocketAddress);
    serverSocketChannel.configureBlocking(false);

    int ops = serverSocketChannel.validOps();
    SelectionKey selectKy = serverSocketChannel.register(selector, ops, null);

    while (true) {
      selector.select();
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

      while (selectionKeyIterator.hasNext()) {
        SelectionKey myKey = selectionKeyIterator.next();

        if (myKey.isAcceptable()) {
          SocketChannel socketChannel = serverSocketChannel.accept();
          socketChannel.configureBlocking(false);

          socketChannel.register(selector, SelectionKey.OP_READ);
          log.info("Connection Accepted: " + socketChannel.getLocalAddress() + "\n");

        } else if (myKey.isReadable()) {
          SocketChannel socketChannel = (SocketChannel) myKey.channel();
          ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
          socketChannel.read(byteBuffer);
          byteBuffer.flip();
          String result = new String(byteBuffer.array()).trim();
          List<String> messages = messageReader.extractMessage(result);
          messages.forEach(message -> {
            consumer.receive(message);
          });
        }
        selectionKeyIterator.remove();
      }
    }
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    serverSocketChannel.close();
  }
}
