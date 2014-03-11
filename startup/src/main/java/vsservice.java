import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 17, 2008  
 */
public class vsservice implements Runnable {
  
  private ProcessBuilder processBuilder = null;
  private Process process;
  
  private int counter = 0;
  private String address = "http://192.168.1.3:9245/";
  
  public vsservice(String address) {
    this();
    this.address = address;
  }
  
  public vsservice() {
    processBuilder  = new ProcessBuilder("sh", "./vietspider.sh");      
  }
  
  public void run() {
    while(true) {
      check();
      if(counter > 5) {
        startService();
      }
      try {
        Thread.sleep(10*1000);
      } catch (Exception e) {
      }
    }
  }
  
  private void startService() {
    if(process != null ) {
      process.destroy();
      process = null;
    }
    
    try {
      process = processBuilder.start();
    } catch (Exception e) {
      throw new RuntimeException("There was an error executing HandBrakeCLI.", e);
    }
  }

  private void check() {
    try {
      URL url  = new URL(address);
      URLConnection connection = url.openConnection();
      connection.getContent();
      counter = 0;
    } catch (ConnectException e) {
      if(e.getMessage().indexOf("Connection refused") > -1) counter++;
    } catch (Exception e) {
      counter=0;
    }
  }
  
  public static void main(String[] args) {
    if(args.length < 1) {
      new Thread(new vsservice()).start();
    } else {
      new Thread(new vsservice(args[0])).start();
    }
  }
  
}
