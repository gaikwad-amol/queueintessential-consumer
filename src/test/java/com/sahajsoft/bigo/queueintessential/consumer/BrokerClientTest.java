package com.sahajsoft.bigo.queueintessential.consumer;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class BrokerClientTest {
  private static final int PORT = 6666;
  private TestServer server;
  BrokerClient brokerClient;

  @BeforeEach
  public void setUp() {
    startTestServer();
  }

  private void startTestServer() {
    Thread serverThread = new Thread(() -> {
      try {
        server = new TestServer();
        server.start(PORT);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    serverThread.start();
  }

  @Ignore
  @Test
  public void shouldReceiveMessage() {
    final boolean[] isMethodCalled = {false};
    givenBrokerClient(isMethodCalled);
    givenConnectedToServer();

    whenMessageSendByServer();

    thenBrokerClientReceivesTheMessage();
    Assertions.assertTrue(isMethodCalled[0]);
  }

  @AfterEach
  public void tearDown() {
    try {
      server.stop();
    } catch (IOException e) {

    }
  }

  private void thenBrokerClientReceivesTheMessage() {
    try {
      brokerClient.receiveMessage();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void whenMessageSendByServer() {
    server.sendMessage("<Message>");
  }

  private void givenConnectedToServer() {
    try {
      brokerClient.startConnection("localhost", PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void givenBrokerClient(boolean[] isMethodCalled) {
    brokerClient = new BrokerClient(new Consumer() {
      @Override
      public Path receive(String message) {
        try {
          isMethodCalled[0] = true;
          brokerClient.cleanUp();
        } catch (Exception e) {
          System.out.println("closed");
          e.printStackTrace();
        }
        return null;
      }
    });
  }

}

class TestServer {
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;

  void start(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    clientSocket = serverSocket.accept();
    out = new PrintWriter(clientSocket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  void sendMessage(String message) {
    out.println(message);
  }

  public void stop() throws IOException {
    in.close();
    out.close();
    clientSocket.close();
    serverSocket.close();
  }
}

