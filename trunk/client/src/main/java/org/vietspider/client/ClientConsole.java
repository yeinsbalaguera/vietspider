/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicHeader;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.net.server.CrawlerStatus;
import org.vietspider.net.server.URLPath;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 26, 2008  
 */
public class ClientConsole {

  private Console console;
  private ClientConnector2 connector;
  private boolean connected = false;

  private final static int MIN_SIZE = 98;

  public ClientConsole() {
    console = System.console();
  }

//  private void summary(String cmd) throws Throwable {
//    String [] elements = cmd.split(" ");
//    if(elements.length < 3) return;
//    console.printf("Please wait...");
//    String date = elements[2];
//    new CrawlReporterHandler(new String[]{date}).download(console);
//  }

  private void downloadLog(String cmd) throws Exception {
    String [] elements = cmd.split(" ");
    if(elements.length < 3) return;
    console.printf("Please wait...");
    String date = elements[2];
    File folder;
    if(elements.length > 3) {
      folder = new File(elements[3]);
    } else {
      folder = UtilFile.getFolder("client");
    }
    Date instanceDate = CalendarUtils.getDateFormat().parse(date);
    date = CalendarUtils.getFolderFormat().format(instanceDate);

    Header [] headers = new Header[] {
        new BasicHeader("action", "load.file"),
        new BasicHeader("file", "track/logs/"+date+".log")
    };

    HttpData httpData = connector.loadResponse(URLPath.FILE_HANDLER, new byte[0], headers);
    //    Header header = httpResponse.getFirstHeader("Content-Length");
    File file  = new File(folder, date+".log");
    InputStream contentInput = null;
    try {
      FileOutputStream fileOuput = new FileOutputStream(file);
      contentInput = httpData.getStream();
      RWData.getInstance().save(fileOuput, contentInput);
      contentInput.close();
    } finally {
      connector.release(httpData);
      if(contentInput != null) contentInput.close();
    }

    console.printf("\nlog path: "+file.getAbsolutePath()+"\n");
  }

  private void handleCommand(String cmd) throws Throwable {
    if(cmd.equals("crawler s")) {
      CrawlerClientHandler crawlerClientHandler = new CrawlerClientHandler();
      int status = crawlerClientHandler.mornitorCrawler(CrawlerStatus.START_OR_STOP);
      if(status == -1) {
        console.printf("Crawler stopped.\n");
      } else if(status == 0) {
        console.printf("Crawler is executing.\n");
      }
    } else if(cmd.equals("crawler stop")) {
      CrawlerClientHandler crawlerClientHandler = new CrawlerClientHandler();
      int status = crawlerClientHandler.mornitorCrawler(CrawlerStatus.RUNNING);
      if(status == 0) {
        status = crawlerClientHandler.mornitorCrawler(CrawlerStatus.START_OR_STOP);
      }
      if(status == -1) console.printf("Crawler stopped.\n");
    } else if(cmd.equals("crawler execute") || cmd.equals("crawler start")) {
      CrawlerClientHandler crawlerClientHandler = new CrawlerClientHandler();
      int status = crawlerClientHandler.mornitorCrawler(CrawlerStatus.RUNNING);
      if(status == -1) {
        status = crawlerClientHandler.mornitorCrawler(CrawlerStatus.START_OR_STOP);
      }
      if(status == 0) console.printf("Crawler is executing.\n");
    } else if (cmd.equals("crawler status")) {
      CrawlerStatus status = new CrawlerClientHandler().getCrawlerStatus(CrawlerStatus.RUNNING);
      if(status.getStatus() == -1) {
        console.printf("Crawler stopped.\n");
      } else if(status.getStatus() == 0) {
        console.printf("Crawler is executing.\n");
      }
      console.printf("Total thread is " + status.getTotalThread()+"\n");
      String [] threadStatus = status.getThreadStatus();
      if(threadStatus != null) {
        console.printf("------------------------------------------------------------------\n");
        for(String ele : threadStatus) {
          if(ele.length() > MIN_SIZE) ele = ele.substring(0, MIN_SIZE-3) + "...";  
          console.writer().println(ele);
        }
        console.printf("------------------------------------------------------------------\n");
      }
      console.writer().flush();
    } else if (cmd.equals("crawler info")) {
      CrawlerClientHandler crawlerClientHandler = new CrawlerClientHandler();
      printCrawler(crawlerClientHandler.viewPool());
    } else if (cmd.startsWith("crawler view executor")) {
      int index = 0;
      try {
        index = Integer.parseInt(cmd.substring(cmd.indexOf("executor")+8).trim()); 
      } catch (Exception e) {
        e.printStackTrace();
        index = 0;
      }
      CrawlerClientHandler crawlerClientHandler = new CrawlerClientHandler();
      printCrawler(crawlerClientHandler.viewExecutor(index));
    } else if(cmd.startsWith("download log ")) {
      downloadLog(cmd);
//    } else if(cmd.startsWith("crawler report ")) {
//      summary(cmd);
    } else if(cmd.equals("help")) {
      console.printf("Help\t\t\t\t\t Show help\n");
      console.printf("\n");
      console.printf("Crawler s\t\t\t\t Start, stop or execute crawler service\n");
      console.printf("Crawler start\t\t\t\t Start or execute crawler\n");
      console.printf("Crawler stop\t\t\t\t Stop crawler\n");
      console.printf("Crawler execute\t\t\t\t Start or execute crawler\n");
      console.printf("\n");
      console.printf("Crawler info\t\t\t\t Show crawler information\n");
      console.printf("Crawler status\t\t\t\t Show crawler status with all executor\n");
      console.printf("Crawler view executor [index]\t\t Show executor information by index\n");
      console.printf("\n");
      console.printf("Crawler status\t\t\t\t Show crawler status with all executor\n");
      console.printf("Crawler view executor [index]\t\t Show executor information by index\n");
//      console.printf("Crawler report $date \t\t\t Report crawl data\n");
      console.printf("\n");
      console.printf("Download log $date $folder\t\t Download log file\n");
      console.printf("\n");
      console.printf("Server shutdown\t\t\t\t Shutdown server application\n");
    } else if(cmd == null || cmd.trim().isEmpty()) { 
      return;
    } else {
      console.printf("Unknown command\n");
      console.printf("Type 'help' for help.\n");
    }
  }

  private void printCrawler(String value) {
    if(value.equals("-1")) {
      console.printf("Crawler stopped.\n");
    } else {
      console.printf(value);
      console.printf("\n");
    }
    console.flush();
  }

  private String readLine(String label) {
    String value = console.readLine(label);
    if(value == null) readLine("vietspider>");
    value = value.toLowerCase().trim();
    if(value.isEmpty()) readLine("vietspider>");
    if(value.equals("exit")) System.exit(0);
    if(value.startsWith("connect to ")) {
      try {
        connect(value.substring(value.indexOf("to") + 2).trim(), null, null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if(label.indexOf('?') > -1) return value;
    if(connector == null) {
      connect(readLine("Connect to(host:port)? "), null, null);
    } else if(!connected) {
      login(null, null);
    }

    if (value.equals("server shutdown")) {
      String sure  = readLine("Are you sure shutdown server?");
      if(sure.equals("yes") || sure.equals("y")) {
        try {
          DataClientHandler.getInstance().exitApplication();
        } catch (HttpHostConnectException e) {
          console.printf("Server shutdown successfull!\n");
        } catch (ConnectException e) {
          console.printf("Server shutdown successfull!\n");
        } catch (Exception e) {
          e.printStackTrace();
        }
        return value;
      }
    } else {
      try {
        handleCommand(value);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    try {
      Thread.sleep(300);
    } catch (Exception e) {
    }
    readLine("vietspider>");
    return value;
  }

  private void connect(String value, String username, String [] args) {
    value = value.trim();
    String [] elements = value.split(":");
    if(elements.length < 2) {
      console.printf("Incorrect input. Try again.\n");
      connect(readLine("Connect to(host:port)? "), null, null);
    }
    try {
      connector = ClientConnector2.currentInstance(elements[0].trim(), elements[1].trim());
    } catch (Exception e) {
      e.printStackTrace();
    }
    connected = false;
    try {
      Thread.sleep(800);
    } catch (Exception e) {
    }
    login(username, args);
  }

  private void login(String username, String [] args) {
    while(username == null || username.trim().isEmpty()) {
      username = readLine("Username? ");
    }

    String password = new String(console.readPassword("Password? "));
    while(password == null || password.trim().isEmpty()) {
      password = new String(console.readPassword("Password? "));
    }

    String message = "";
    try {
      int ping  = connector.ping(username, password);

      connected = ping > -1;
      if(ping < -2) {
        message =  "Cann't connect to "+connector.getRemoteURL();
        console.printf(message);
        console.printf("\n"); 
        connect(readLine("Connect to(host:port)? "), null, null);
        return;
      } else if (ping == -2) {
        message =  "Incorrect username!";
        console.printf(message);
        console.printf("\n"); 
        login(null, args);
        return;
      } else if (ping == -1) {
        message =  "Incorrect password!";
        login(username, args);
        return;
      } else  if(ping == 0) {
        message =  "incorrect mode!";
      } 
    } catch (Exception e) {
      //    e.printStackTrace();
      if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
        message = e.getMessage();
      } else {
        message = e.toString();
      }
    }

    if( (message != null && message.trim().endsWith("refused")) 
        || (connector.getPermission() != User.ROLE_ADMIN)) {
      console.printf(message);
      console.printf("\n"); 
      connect(readLine("Connect to(host:port)? "), null, null);
      return;
    }

    if(connected) {
      console.printf("Login successfull!\n");
      handleCommand(args);
      readLine("vietspider> ");
      return;
    }
    console.printf(message);
    console.printf("\n");
    login(null, null);
  }

  private void handleCommand(String [] args) {
    if(args == null) return;
    for(String cmd : args) {
      cmd = cmd.trim();
      if(!cmd.toLowerCase().startsWith("--cmd")) continue;
      if(cmd.indexOf('=') < 0) continue;
      cmd = cmd.substring(cmd.indexOf('=')+1).trim();
      if(cmd.charAt(0) == '\"') cmd = cmd.substring(1);
      if(cmd.charAt(cmd.length() - 1) == '\"') {
        cmd = cmd.substring(0, cmd.length() - 1);
      }
      try {
        handleCommand(cmd);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  public static void start(Object [] args) {
    System.setProperty("file.encoding", "utf-8");
    ClientConsole clientConsole = new ClientConsole();
    String [] values = (String[])args;
    while(true) {
      if(values == null || values.length < 1) {
        clientConsole.connect(clientConsole.readLine("Connect to(host:port)? "), null, null);  
      } else {
        String url  = values[0];
        String username = null;
        if(url.indexOf('@') > -1) {
          username = url.substring(0, url.indexOf('@')).trim();
          url  = url.substring(url.indexOf('@') + 1).trim();
        }

        String [] cmd = new String[values.length-1];
        for(int i = 1; i < values.length; i++) {
          cmd[i-1] = values[i];
        }
        clientConsole.connect(url, username, cmd);
        args = null;
      }
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }
    }
  }


  public static final void main(String... aArgs){
    ClientConsole clientConsole = new ClientConsole();
    clientConsole.connect(clientConsole.readLine("Connect to(host:port)? "), null, null);
  }

}
