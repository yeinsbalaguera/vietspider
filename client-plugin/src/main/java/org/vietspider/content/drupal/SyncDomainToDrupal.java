/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.drupal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Control;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.attribute.Attributes;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Sep 8, 2008
 */
public class SyncDomainToDrupal extends ClientPlugin {

  private String label;

  public SyncDomainToDrupal() {
    try {
      ClientRM resources = new ClientRM("Plugin");
      label = resources.getLabel(getClass().getName() + ".viewUserAction");
    } catch (Exception e) {
      label = "User Action Viewer";
      ClientLog.getInstance().setException(null, e);
    }
  }

  @Override
  public boolean isValidType(int type) { return type == DOMAIN; }

  public String getLabel() {
    return label;
  }

  @Override
  public void invoke(Object... objects) {
    final Control link = (Control) objects[0];
    final Browser browser = (Browser) objects[1];
    final String bb = browser.getText();

    Worker excutor = new Worker() {

      private String error = null;
      private String str ;
      private Map<String, String> maps;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {

      }

      public void execute() {
        maps = new HashMap<String, String>();
        str = bb;
        char[] chars = str.toCharArray();
        List<NodeImpl> tokens = null;
        try {
          tokens = new HTMLParser2().createTokens(chars);
        } catch (Exception e) {
          error = e.getMessage();
          return;
        }
        if(tokens == null) {
          error = "Can't parse tokens!";
          return ;
        }

        for(int i = 0; i < tokens.size(); i++) {
          NodeImpl node = tokens.get(i);
          if(!node.isNode(Name.A)) continue;
          String value = getAttribute(node, "class");
          if(value == null) continue;
          if(!value.startsWith("meta_title")) continue ;
          String href = getAttribute(node, "href");
          String inerText = href;
          maps.put(href, inerText);
        }
        str = buildHTML(maps);
      }
      
      private String buildHTML(Map<String, String> maps2) {
        StringBuilder builder = new StringBuilder();
        StringBuilder idList = new StringBuilder();
        String remoteURL = ClientConnector2.currentInstance().getRemoteURL();
        builder.append("<html>");
        builder.append("  <head>");
        builder.append("    <meta http-equiv='content-type' content='text/head; charset=utf-8'>");
        builder.append("  </head>");
        builder.append("  <body>");
        builder.append("    <h3> Select Articles for sent to Drupal System </h3>");
        builder.append("    <form action='" + remoteURL+ "/data_plugin/handler/' method='post'>" );
        builder.append("      <table ");
        builder.append("        style=\"border-collapse: collapse; width = '100%' font-family: Tahoma; font-size: 11px;\"");
        builder.append("        border=\"2\" cellpadding=\"1\" cellspacing=\"1\">\n");
 
        builder.append("        <tr>");
        builder.append("          <th style=\"background-color: #ffffff; font-family: Verdana; font-weight: bold; font-size: 15px; color: #ffffff; border-bottom: 1px solid #E6EEFA; background-color: rgb(0, 102, 153); height: 22px;\">");
        builder.append("            Title");
        builder.append("          </th>");
        builder.append("          <th  style=\"background-color: #ffffff; font-family: Verdana; font-weight: bold; font-size: 15px; color: #ffffff; border-bottom: 1px solid #E6EEFA; background-color: rgb(0, 102, 153); height: 22px;\">");
        builder.append("            Id");
        builder.append("          </th>");
        builder.append("          <th  style=\"background-color: #ffffff; font-family: Verdana; font-weight: bold; font-size: 15px; color: #ffffff; border-bottom: 1px solid #E6EEFA; background-color: rgb(0, 102, 153); height: 22px;\">");
        builder.append("            Select");
        builder.append("          </th>");
        builder.append("  </tr>");
          
        for(Entry<String, String> entry: maps2.entrySet()){
            String id = entry.getKey();
            int index = id.lastIndexOf("/");
            id =  id.substring(index +1 );
            idList.append(id).append("||");
            builder.append("    <tr><td>").append(entry.getValue()).append("</td>");
            builder.append("        <td>").append(id).append("</td>");
            builder.append("        <td><input  type='checkbox' checked='checked' value='1' name='" + id + "'/> </td></tr>");;
            
          }
        builder.append("      </table>").
          append("      <br/><br/>" ).
          append("      <input type='hidden' value='"+idList+"' name='idList'/>").
          append("      <input type='hidden' value='drupal.sync.article.plugin' name='plugin.name'/>").
          append("      <input type='hidden' value='send.domain' name='action'/>").
          append("      <input type='submit' value='Submit' />").
          append("    </form>").
          append("  </body>").
          append("</html>");
        return builder.toString();
      }

      public String getAttribute(HTMLNode node, String name) {
        Attributes attrs = node.getAttributes(); 
        int idx = attrs.indexOf(name);
        if(idx < 0) return null; 
        return attrs.get(idx).getValue();
      }

      public void after() {
        if (error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(link.getShell(), new Exception(error));
          return;
        }
//         new ShowArticleEditor(browser, str);
      }
    };
    new ThreadExecutor(excutor, link).start();
  }

}