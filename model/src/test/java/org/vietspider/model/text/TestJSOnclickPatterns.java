/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.text;

import java.util.Properties;

import org.vietspider.js.JsUtils;
import org.vietspider.link.pattern.JSOnclickPatternUtils;
import org.vietspider.link.pattern.JSOnclickPatterns;
import org.vietspider.link.pattern.LinkPatternFactory;
import org.vietspider.link.pattern.model.JSOnclickPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 17, 2008  
 */
public class TestJSOnclickPatterns {
  
  public static void testOnlick() throws Exception {
    String [] elements = new String[] {
        "javascript:__doPostBack('ctl00$ContentPlaceHolder1$gvSearchResults','Page$*')",
        "__EVENTTARGET={1}&__EVENTARGUMENT={2}",
//        "ctl00$ContentPlaceHolder1$ddlState=[state]",
        "javascript:__doPostBack('ctl00$ContentPlaceHolder1$gvSearchResults','detail$*') #data",
        "__EVENTTARGET={1}&__EVENTARGUMENT={2}"
    };
    
    JSOnclickPatterns jsPatterns =
      LinkPatternFactory.createMultiPatterns(JSOnclickPatterns.class, elements);

    String link = "javascript:__doPostBack('ctl00$ContentPlaceHolder1$gvSearchResults','detail$493')";
    
//    DataReader reader = RWData.getInstance();
    
     JSOnclickPattern [] patterns = jsPatterns.getPatterns();
     System.out.println(" === > tong so "+ patterns.length);
    if(patterns != null) {
      for(JSOnclickPattern pattern : patterns) {
        if(pattern == null || !pattern.match(link)) continue;
        String url = pattern.create(link);
        System.out.println(url + "  is " + pattern.getType() );
      }
    }
//    link = new String(bytes);
//    System.out.println(jsPatterns.create(link));
    
    elements = new String[]{
        "document.location.href=acb/*",
        "__EVENTTARGET={1}"
        };
    jsPatterns = LinkPatternFactory.createMultiPatterns(JSOnclickPatterns.class, elements);

    link = "document.location.href=acb/true;";
    System.out.println(jsPatterns.create(link));
  }

  public static void testTemplate() {
    StringBuilder builder = new StringBuilder();
    builder.append("viewNameUI(event,'*','','*','');");
    builder.append("\n");
    builder.append("#comments>");

    String key = "LINK_GENERATOR";

    Properties properties = new Properties();
    properties.setProperty(key, builder.toString());

    JSOnclickPatterns exchangePatterns =
      LinkPatternFactory.createMultiPatterns(JSOnclickPatterns.class, properties, key);

    String link = "http://ddhsonline.com/diendan/showthread.php?t=131";
    String value ;
    //value = exchangePatterns.create(link).get(0);
//  System.err.println(" thay "+ value);

//  link = "http://ddhsonline.com/diendan/showthread.php?t=1dsfds31";
//  value = exchangePatterns.create(link);
//  System.err.println(" thay "+ value);

//  link = "http://blog.360.yahoo.com/blog-ZmaTjZwweLLUDj7I8qzJ0wXHttZaAh7QTyAC?p=97#comments";
//  value = exchangePatterns.create(link).get(0);
//  System.err.println(" thay "+ value);

//  link = "http://wwww.vietnamnet.com/t=123";
//  value = exchangePatterns.create(link);
//  System.err.println(" thay "+ value);
  }

  public static void testParams() {
    String jsFunction = "viewNameUI(event  , ' 12\"000\\'16\\,9763' , \"huhu\" ,'','�?nh starf','v ' );";
//    testParams(jsFunction);
//    System.out.println(" ====== =================  =================");
//
//    jsFunction = "viewNameUI(event  , param );";
//    testParams(jsFunction);
//
//    jsFunction = "viewNameUI(\"event  \", \"param\" );";
//    testParams(jsFunction);
//
//    jsFunction = "viewNameUI('event  ', \"param\" );";
//    testParams(jsFunction);
    
//    jsFunction = "window.location='doanhnghiep_info.asp?id=123'";
    jsFunction = "javascript:window.open('http://www.bbb.org/indianapolis/business-reviews/mattresses/may-and-co-inc-in-indianapolis-in-6723');";
    testParams(jsFunction);
  }

  public static void testParams(String jsFunction) {
    String [] params = JsUtils.getParams(jsFunction);

    for(String param : params) {
      System.out.print("|" + param);
    }
    System.out.println();
  }

  public static void testTemplateJSFunction() {
    String jsFunction = "viewNameUI(event  , ' 12\"000\\'16\\,9763' , \"huhu\" ,'','�?nh starf','v ' );";
    System.out.println(JSOnclickPatternUtils.jsFunctionToTemplate(jsFunction));

    jsFunction = "viewNameUI(event, 12);";
    System.out.println(JSOnclickPatternUtils.jsFunctionToTemplate(jsFunction));

    jsFunction = "viewNameUI(\"event\", \"12\");";
    System.out.println(JSOnclickPatternUtils.jsFunctionToTemplate(jsFunction));
    
    jsFunction = "window.location='doanhnghiep_info.asp?id=123'";
    System.out.println(JSOnclickPatternUtils.jsFunctionToTemplate(jsFunction));
    
    jsFunction = "window.location='/data/beta/showthread.php?t=181333&page=25'";
    System.out.println(JSOnclickPatternUtils.jsFunctionToTemplate(jsFunction));
  }

  public static void testToTemplate() {
//    String jsFunction = "viewNameUI(event  , ' 12\"000\\'16\\,9763' , \"huhu\" ,'','�?nh starf','v ' );";
//    jsFunction += "\n http://www.ddth.com/index.php?value1= 12\"000\\'16\\,9763&value2=v";
//
//    System.out.println(JSOnclickPatternUtils.toTemplates(jsFunction));
//
//    jsFunction = "viewNameUI(event  , ' 12\"000\\'16\\,9763' , \"huhu\" ,'','�?nh starf','v ' );";
//    jsFunction += "\n http://www.ddth.com/index.php?value1= 12\"000\\'16\\,9763&value2=v&value=4";
//
//    jsFunction = "viewNameUI(event  , ' 12\"000\\'16\\,9763' , \"huhu\" ,'','�?nh starf','v ' );";
//    jsFunction += "\n http://www.ddth.com/huhu/index.php?value1= 12\"000\\'16\\,9763&value2=v";
//    
    String jsFunction = "ajaxload('1bcruu8kiu3745o5qi8vnabiu4','bodyactionnew.php',loading1,'idbody','vn','','','','true','tt','105');";
    jsFunction += "\nhttp://www.vietnamenterprises.com.vn/bodyactionnew.php?ssid=1bcruu8kiu3745o5qi8vnabiu4&lang=vn&param1=tt&param2=105";

    System.out.println(JSOnclickPatternUtils.toTemplates(jsFunction));
  }


  public static void main(String[] args) throws Exception  {
//  testParams();

//  testTemplateJSFunction();

//    testToTemplate();
    testOnlick();
  }


}
