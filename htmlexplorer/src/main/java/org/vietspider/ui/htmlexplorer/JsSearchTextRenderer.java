/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import java.util.List;

import org.eclipse.swt.browser.Browser;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2009  
 */
public class JsSearchTextRenderer {
  
  private JsSearchText viewer;
  
  private JsSearchTextFunction searcher;
  
  public JsSearchTextRenderer(JsSearchText viewer) {
    this.viewer = viewer;
  }
  
  public void viewDocument(HTMLNode header, HTMLNode body, String url) {
    Browser browser = viewer.getBrowser();
    
    if(body.isNode(Name.CONTENT) 
        || body.isNode(Name.COMMENT) 
        || body.isNode(Name.UNKNOWN)) {
      browser.setText(body.getTextValue());
      return;
    }
    
    try {
      StringBuilder builder = new StringBuilder();
      builder.append("<html>").append("<head>");
      if(header != null && header.getChildren() != null) {
        for(HTMLNode ele : header.getChildren()){
          builder.append(ele.getTextValue()).append('\n');
        }
      }
//      String baseHref = UtilFile.getFolder("client/htmlexplorer/").toURI().toString();
      //.getAbsolutePath();//HTMLExplorer.class.getResource("").toString();
//      builder.append("<base href=\""+baseHref+"\">").append("</head>");
      builder.append ("<script language=\"JavaScript\">\n");
//      builder.append ("function makeArray() {");
//      builder.append ("   for (i = 0; i<makeArray.arguments.length; i++)");
//      builder.append ("    this[i + 1] = makeArray.arguments[i];");
//      builder.append ("}");
      builder.append ("function gotoSelectedText() {\n");
      builder.append ("    var value;\n");
      builder.append ("    try {\n");
      builder.append ("        window.stop();\n");
      builder.append ("    } catch(e) {\n");
//      builder.append ("        alert('1111: ' + e.message);\n");
      builder.append ("       try {\n");
      builder.append ("          document.execCommand(\"Stop\");\n");
      builder.append ("       } catch(e) {\n");
      builder.append ("        alert('222: ' + e.message);\n");
      builder.append ("       }\n");
      builder.append ("    }\n");
      
      builder.append ("    try {\n");
      builder.append ("        value = window.getSelection();\n");
      builder.append ("    } catch(e) {\n");
      builder.append ("        value = document.selection.createRange().text;\n");
      builder.append ("    }\n");
      builder.append ("    try {\n");
//      builder.append ("        alert(value);\n");
      builder.append ("        result = theJavaFunction(' ' + value + ' ');\n");
      builder.append ("    } catch (e) {\n");
//      builder.append ("       try {\n");      
//      builder.append ("           result = theJavaFunction('value');\n");
//      builder.append ("       } catch (e) {\n");
//  //      String os_name = System.getProperty("os.name").toLowerCase();
//  //      if(os_name.indexOf("windows") > -1 
//  //              || os_name.indexOf("win") > -1)  {
////        builder.append ("         alert(e + ':' + arr); ");
//        builder.append ("     }\n");
//    	  builder.append ("        alert('select node error occurred: ' + e.message + '! Select again.');\n");
//      }
//      builder.append ("         alert(e + ':' + value); ");
      builder.append ("        return;\n");
      builder.append ("    }\n");
      builder.append ("}\n");
      builder.append ("</script>\n");
      builder.append("</head>");
      builder.append("<body onmouseup=\"gotoSelectedText();\">\n");
      builder.append(buildBodyText(body)).append("</body></html>");
//      System.out.println(builder.toString());
      
      browser.setText(builder.toString());
//      System.out.println("bebeb " + browser.getText());
//      System.out.println("===================  " + builder);
      
      if(searcher != null) searcher.dispose();
      searcher = new JsSearchTextFunction(viewer);
    } catch (Exception e) {
      if(url != null) viewer.getBrowser().setUrl(url);
      ClientLog.getInstance().setMessage(viewer.getBrowser().getShell(), e);
    }
    return;
  }
  
  private String buildBodyText(HTMLNode body) {
    List<HTMLNode> children = body.getChildren();
    StringBuilder builder = new StringBuilder();
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        children.get(i).buildValue(builder);
      }
    }
    return builder.toString();
  }
}
