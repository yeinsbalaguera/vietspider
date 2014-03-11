/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client.proxy;

import java.io.IOException;
import java.net.ServerSocket;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 4, 2011  
 */
public class ProxyServer {
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = null;
    boolean listening = true;

    int port =  8081; //default

    try {
        serverSocket = new ServerSocket(port);
        System.out.println("Started on: " + port);
    } catch (IOException e) {
        System.err.println("Could not listen on port: " + port);
        System.exit(-1);
    }

    while (listening) {
        new ProxyThread(serverSocket.accept()).start();
    }
    serverSocket.close();
}
}
