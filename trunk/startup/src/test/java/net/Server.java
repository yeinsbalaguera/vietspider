/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 1, 2008
 */
public class Server {
  
  private File folder;
  
  public Server() {
    folder = new File("C:\\Temp");
//    try {
//      folder = new File(Server.class.getResource("").toURI());
//    } catch (Exception e) {
//      e.printStackTrace();
//      System.exit(0);
//    }
  }

  private void handle(Socket socket) {
    InputStream inputReader =  null;
    
    FileOutputStream fileOutputStream = null;
    try {
      inputReader = socket.getInputStream();
      System.out.println(socket.getRemoteSocketAddress());
      
      byte [] buff = new byte[2048];
      int read = -1;
      
      StringBuilder fileName = new StringBuilder();
      
      while ((read = inputReader.read(buff)) != -1) {
        int fileNameSeparator = -1;
        for(int i = 0; i < read; i++) {
          if(((char)buff[i]) != '/') continue;
          fileNameSeparator = i;
          break;
        }
        
        if(fileNameSeparator < 0) {
           fileName.append(new String(buff, 0, read));
        } else {
          fileName.append(new String(buff, 0, fileNameSeparator));
          File file = new File(folder, fileName.toString());
          
          if(file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
          }
         
          fileOutputStream =  new FileOutputStream(file);
          if(fileNameSeparator + 1 < read) {
            fileOutputStream.write(buff, fileNameSeparator+1, read);
          }
        }
      }
      
      if(fileOutputStream != null)  {
        while ((read = inputReader.read(buff)) != -1) {
          fileOutputStream.write(buff, 0, read);
        }
      }

      fileOutputStream.close();
      socket.getOutputStream().write("data save successfull".getBytes());
    } catch (IOException e) {
      e.printStackTrace();
      try {
        socket.getOutputStream().write(e.toString().getBytes());
      } catch (Exception e2) {
      }
    } finally {
      try {
        socket.getOutputStream().close();
      } catch (IOException e) {
      } 
      
      try {
        if(inputReader != null) inputReader.close();
      } catch (IOException e) {
      }
      
      try {
        if(fileOutputStream != null) fileOutputStream.close();
      } catch (IOException e) {
      }
    }

  }
  
  private void startServer() throws IOException {
    ServerSocket serverSocket = null;
    boolean listening = true;

    System.out.println("Server is running");
    try {
      serverSocket = new ServerSocket(8888);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }

    while (listening) {
      handle(serverSocket.accept());
    }

    serverSocket.close();
  }

  public static void main(String[] args) {
    Server server = new Server();
    try {
      server.startServer();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
}
