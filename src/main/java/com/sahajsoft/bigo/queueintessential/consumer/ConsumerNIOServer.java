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
public class ConsumerNIOServer {

  @Autowired
  private Consumer consumer;
  private ServerSocketChannel serverSocketChannel;
  //ByteBuffer crunchifyBuffer = ByteBuffer.allocateDirect(2048);
  MessageReader messageReader = new MessageReader();
  //int count = 0;

  public void start(String hostname, int port) throws IOException {
    Selector selector = Selector.open();
    serverSocketChannel = ServerSocketChannel.open();
    InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);

    serverSocketChannel.bind(inetSocketAddress);
    serverSocketChannel.configureBlocking(false);

    int ops = serverSocketChannel.validOps();
    SelectionKey selectKy = serverSocketChannel.register(selector, ops, null);

    while (true) {

      //System.out.println("i'm a server and i'm waiting for new connection and buffer select...");
      // Selects a set of keys whose corresponding channels are ready for I/O operations
      selector.select();

      // token representing the registration of a SelectableChannel with a Selector
      Set<SelectionKey> crunchifyKeys = selector.selectedKeys();
      Iterator<SelectionKey> crunchifyIterator = crunchifyKeys.iterator();

      while (crunchifyIterator.hasNext()) {
        SelectionKey myKey = crunchifyIterator.next();

        // Tests whether this key's channel is ready to accept a new socket connection
        if (myKey.isAcceptable()) {
          SocketChannel crunchifyClient = serverSocketChannel.accept();

          // Adjusts this channel's blocking mode to false
          crunchifyClient.configureBlocking(false);

          // Operation-set bit for read operations
          crunchifyClient.register(selector, SelectionKey.OP_READ);
          log.info("Connection Accepted: " + crunchifyClient.getLocalAddress() + "\n");

          // Tests whether this key's channel is ready for reading
        } else if (myKey.isReadable()) {

          SocketChannel crunchifyClient = (SocketChannel) myKey.channel();
          ByteBuffer crunchifyBuffer = ByteBuffer.allocate(4096);
          //crunchifyBuffer.compact();
//          System.out.println("Producer crunchifyBuffer " + crunchifyBuffer.hashCode());

          crunchifyClient.read(crunchifyBuffer);
          crunchifyBuffer.flip();
          //byte[] readBytes = new byte[2048];
          //crunchifyBuffer = crunchifyBuffer.get(readBytes);
          String result = new String(crunchifyBuffer.array()).trim();
          List<String> messages = messageReader.extractMessage(result);
          messages.forEach(message -> {
            consumer.receive(message);//count++;
            });
          //broker.forward(result);
          //crunchifyBuffer.compact();
          //log.info("Message received: " + result);

//          if (result.equals("Crunchify")) {
//            crunchifyClient.close();
//            log.info("\nIt's time to close connection as we got last company name 'Crunchify'");
//            log.info("\nServer will keep running. Try running client again to establish new connection");
//          }
        }
        crunchifyIterator.remove();
      }
    }
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    serverSocketChannel.close();
  }
}
