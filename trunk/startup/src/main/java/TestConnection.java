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
public class TestConnection {
  public static void main(String[] args) {
    try {
    String address = "http://192.168.1.3:9247/";
    URL url  = new URL(address);
    URLConnection connection = url.openConnection();
    Object object = connection.getContent();
    System.out.println(object);
    } catch (ConnectException e) {
      System.out.println(e.getMessage().equals("Connection refused: connect"));
    } catch (Exception e) {
      e.printStackTrace();
    } 
    
    
  }
}
