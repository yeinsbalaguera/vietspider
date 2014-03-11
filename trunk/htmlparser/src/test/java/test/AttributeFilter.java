package test;


import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.util.HyperLinkUtil;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 9, 2006
 */
public class AttributeFilter {
  
  private static HyperLinkUtil handler  = new HyperLinkUtil();

  private static void testGetLink(HTMLNode node){
    List<String> list  = handler.scanSiteLink(node);
    for(String ele : list)
      System.out.println(ele);
  }

  private static void testCreateFullLink(HTMLNode node, URL home){
    handler.createFullNormalLink(node, home);
    List<String> list  = handler.scanSiteLink(node);
    for(String ele : list)
      System.out.println(ele);
  }

  private static void testCreateImageLink(HTMLNode node, URL home){
    handler.createFullImageLink(node, home);
    Map<String, String> map = new HashMap<String, String>(1);
    map.put("img", "src");
//    List<String> list  = handler.getAttributes(node, map);
//    for(String ele : list)
//      System.out.println(ele);
  }


  public static void main(String[] args) {
    try{
      URL url = new URL("http://www.java.net");
      HTMLDocument document = HTMLParser.createDocument(url.openStream(), "utf-8");
      testGetLink(document.getRoot());
      System.out.println("\n\n\n\n*********************************************************************\n\n\n\n");
      testCreateFullLink(document.getRoot(), url);
      System.out.println("\n\n\n\n*********************************************************************\n\n\n\n");
      testCreateImageLink(document.getRoot(), url);
    }catch(Exception exp){
      exp.printStackTrace();
    }
  }
}
