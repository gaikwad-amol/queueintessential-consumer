package com.sahajsoft.bigo.queueintessential.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Component
public class BrokerClient {

  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;

  private Consumer consumer;

  @Autowired
  public BrokerClient(Consumer consumer) {
    this.consumer = consumer;
  }

  public void startConnection(String ip, int port) throws IOException {
    System.out.println("Connecting " + ip + ":" + port);
    clientSocket = new Socket(ip, port);
    out = new PrintWriter(clientSocket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    System.out.println("Connected " + ip + ":" + port);
  }

  public void receiveMessage() throws IOException {
    System.out.println("receiving .. ");
    String message;
    while ((message = in.readLine()) != null) {
      System.out.println("read");
      String response = "OK";
      //out.println(response);
      consumer.receive(message);
    }
  }

  private void stopConnection() throws IOException {
    in.close();
    out.close();
    clientSocket.close();
  }

  @PreDestroy
  public void cleanUp() throws Exception {
    stopConnection();
  }
}

