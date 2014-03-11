/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 1, 2008
 */
public class Client {

  private static void handle(InputStream inputStream) throws IOException {
    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    while (true) {
      String inputLine = bufferedReader.readLine();
      if(inputLine == null) break;
      System.out.println(inputLine);
    }
    bufferedReader.close();
  }

  private static void startClient()  {
    InputStreamReader streamReader =  new InputStreamReader(System.in);
    LineNumberReader lineReader = new LineNumberReader(streamReader);
    System.out.println("Please input command line");
    while(true) {
      try {
        String line  = lineReader.readLine();
        if("exit".equals(line)) {
          System.out.println("bye");
          break;
        } else if(line.startsWith("send")) {
          line  = line.substring("send".length());
          String [] elements = line.split(",");
          String message = "Please type: send host, port, file";
          if(elements.length < 3) {
            System.out.println(message);
            return;
          }
          send(elements[0], elements[1], new File(elements[2].trim()));
        } else  {
          System.out.println("Incorrect command");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void send(String host, String port, File file) throws Exception {
    Socket clientSocket = null;
    clientSocket = new Socket(host.trim(), Integer.parseInt(port.trim()));

    FileInputStream fileInputStream = new FileInputStream(file);
    byte [] buff = new byte[1024];
    int read = -1;
    
    OutputStream outputStream = clientSocket.getOutputStream();
    outputStream.write((file.getName()+"/").getBytes());
    while( (read = fileInputStream.read(buff)) != -1) {
      outputStream.write(buff, 0, read);
    }
    outputStream.flush();

    clientSocket.shutdownOutput();

    handle(clientSocket.getInputStream());
    clientSocket.close();
  }


  public static void main(String[] args)  {
    startClient();
  }
}
