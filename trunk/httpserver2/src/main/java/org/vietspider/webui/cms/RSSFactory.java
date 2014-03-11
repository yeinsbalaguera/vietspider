package org.vietspider.webui.cms;


import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.webui.cms.render.RSSRenderer;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 12, 2007
 */
public class RSSFactory extends BufferWriter implements RSSRenderer {

  //  http://216.67.241.22:9245/vietspider/RSS/3/19.11.2009?pageSize=15
  //    http://moom.vn:4528/vietspider/RSS/3/19.11.2009?pageSize=15
  //    http://moom.vn:4528/vietspider/FILE/custom.rdf
  @SuppressWarnings("unused")
  public void write(OutputStream output_, String viewer, MetaList metas) throws Exception {
    this.output = output_;    
    CMSService cms = CMSService.INSTANCE;
    SystemProperties systemProperties = SystemProperties.getInstance();

    StringBuilder buildHost = new StringBuilder(SWProtocol.HTTP);
    buildHost.append(CMSService.INSTANCE.getHost()).append(':').append(cms.getWebPort());
    String host = buildHost.toString();

    append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    append("<rss version=\"2.0\" xmlns:vietspider=\"");
    append(host);
    append("/vietspider/FILE/custom.rdf\">\n<channel>\n<title>");

    append(systemProperties.getValue(Application.APPLICATION_NAME));append("</title>");
    append("\n<link>");append(host);append("/RSS</link>");
    append("\n<description>Description</description>");
    append("\n<language>vi</language>");
    append("\n<copyright>");append(systemProperties.getValue(Application.APPLICATION_NAME));append("</copyright>");
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'");
    append("\n<pubDate>");append(formatter.format(Calendar.getInstance().getTime()));append("</pubDate>");

//    RefsDecoder decoder = new RefsDecoder();
    List<Article> list = metas.getData();
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    for(int i = 0; i < list.size(); i++){
      Article ele  = list.get(i);
      if(ele == null) continue;
      Domain domain = ele.getDomain();
      Meta meta = ele.getMeta();
      Content content = ele.getContent();
      XMLDocument document = null;
      if(content == null) {
        DatabaseReader getter = DatabaseService.getLoader();
        content = getter.loadContent(meta.getId());
      }
      
      try {
        document = XMLParser.createDocument(content.getContent(), null);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      if(document == null) continue;

      append("\n<item>");
      append("\n<guid isPermaLink=\"false\">");append(meta.getId());
      append("_");append(String.valueOf(i));append("</guid>");
      append("\n<title>");append(meta.getTitle());append("</title>");
      append("\n<link><![CDATA[");
      append(meta.getSource());
      //      append(host);append("/");append("site");append("/DETAIL/");append(meta.getId());
      append("]]></link>");
//      append(buildImage(decoder, document.getRoot()));
      append("\n<description><![CDATA[");
      try {
        append(buildDesc(/*meta, */document.getRoot()));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
//      append(new String(decode(decoder, meta.getDesc())));
      append("]]></description>");
      //      append("\n<content>");
      append(buildContent(document.getRoot()));
      //      append("</content>");
      //      append("</description>");
      Date date =  Calendar.getInstance().getTime();
      try {
        date = dateFormat1.parse(meta.getTime());
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      append("\n<pubDate>");append(formatter.format(date));append("</pubDate>");
      append("\n<category>");
      append(domain.getCategory());append("/");append(domain.getName());
      append("</category>");
      append(buildCustomTag(document.getRoot()));
      append("\n</item>");      
    }
    append("\n</channel>\n</rss>");
  }

  private String buildContent(/*Meta meta,*/ XMLNode root)  {
    StringBuilder builder = new StringBuilder();
    //    builder.append("<p>").append(meta.getDesc()).append("</p>");
    build(builder, root);
    return builder.toString();
  }

  private void build(StringBuilder builder, XMLNode node) {
    if(node == null) return;
    List<XMLNode> children = node.getChildren();
    if(children == null || children.size() < 1) return ;
    //    System.out.println(" name "+ node.getName());
    if(node.getName() != null && node.getName().startsWith("paragraph")) {
      XMLNode n = children.get(0);
      //      if(n != null) builder.append('\n').append("<p>").append(n.getValue()).append("</p>");
      if(n != null) {
        builder.append('\n').append("<vietspider:").append(node.getName()).append('>');
        //        System.out.println(" ha aha "+ new String(n.getValue()));
        String text = new String(cleanData(n.getValue()));
        if(text.startsWith("<![CDATA[")) {
          builder.append(cleanData(n.getValue()));
        } else {
          builder.append("<![CDATA[").append(cleanData(n.getValue())).append("]]>");
        }
//        builder.append("<![CDATA[").append(n.getValue());
//        builder.append("<![CDATA[").append(decode(decoder, new String(n.getValue())));
        builder.append("</vietspider:").append(node.getName()).append('>');
      }
      return;
    }

    for(int i = 0; i < children.size(); i++) {
      build(builder, children.get(i));
    }
  }

  /*
   * <image> 
      <url>http://youtube.com/img/pic_youtubelogo_123x63.gif</url>
      <link>http://youtube.com</link> 
      <title>YouTube</title> 
      <height>63</height>
      <width>123</width>
    </image>
   */
  
  private String buildDesc(XMLNode node)  {
    StringBuilder builder = new StringBuilder();
//    builder.append("<p>").append(decode(decoder, meta.getDesc())).append("<p>\n");
//    builder.append("<p>").append(meta.getDesc()).append("<p>\n");
    String image = buildDesc(builder, node);
    if(image == null) return builder.toString();
    return image + builder.toString();
//    return image+ "<p>" + new String(decode(decoder, meta.getDesc())) + "<p>\n" + builder.toString();
  }
  
  private String buildDesc(StringBuilder builder, XMLNode node)  {
    String image = null;
    List<XMLNode> children = node.getChildren();
    if(children == null || children.size() < 1) return null;
    //    System.out.println(" name "+ node.getName());
    if(node.getName() != null && node.getName().startsWith("paragraph")) {
      XMLNode n = children.get(0);
      if(n != null) {
        String text = new String(cleanData(n.getValue()));
        if(text.startsWith("<![CDATA[")) {
          text = text.substring(9, text.length() - 3);
        }
        builder.append('\n').append("<p>").append(text).append("</p>");
//        builder.append(decode(decoder, new String(n.getValue()))).append("</p>");
      }
      return image;
    } else if(node.getName() != null && node.getName().equals("image")) {
      image = buildImage2(node.getChildren());
    }

    for(int i = 0; i < children.size(); i++) {
      String value = buildDesc(builder, children.get(i));
      if(value != null) image = value;
    }
    return image; 
  }
  
  private String buildImage2(List<XMLNode> nodes) {
    if(nodes == null || nodes.size() < 1) return null;
    StringBuilder builder = new StringBuilder();
    builder.append("\n<table width=\"100\" align=\"left\"><TBODY>");
    //      if(n != null) builder.append('\n').append("<p>").append(n.getValue()).append("</p>");
    for(int i = 0; i < nodes.size(); i++) {
      XMLNode child = nodes.get(i);
      if(child.isNode("link")) {
        if(child.getChildren() != null && child.getChildren().size() > 0) {
          String url = child.getChild(0).getTextValue();
          url = url.substring(9, url.length() - 3);
          builder.append("\n<tr><td valign=\"top\" align=\"center\"><img src=\"");
          builder.append(url).append("\" width=\"100\" height=\"100\" border=\"0\"></TD></TR>");
        }
      } else if(child.isNode("caption")) {
        if(child.getChildren() != null && child.getChildren().size() > 0) {
          String caption = child.getChild(0).getTextValue();
          builder.append("\n<tr><td valign=\"top\" align=\"center\">");
          builder.append(caption).append("</TD></TR>");
        }
      }
    }
    builder.append("\n</tbody></table>");
    return  builder.toString();
  }
  
  @Override()
  protected void append(String text) throws Exception { 
    if(text == null) return;
    StringBuilder builder = new StringBuilder();
    int index = 0;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(!Character.isIdentifierIgnorable(c)) {
        builder.append(c);
      }
      index++;
    }
    
    try {
      output.write(builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }
  
  private char[] cleanData(char [] chars) {
    if(chars == null) return new char[0];
    for(int i = 0; i < chars.length; i++) {
      if(chars[i] == '“' || chars[i] == '”') {
        chars[i] = '"';
      } 
    }
    return chars;
  }
  
  private String buildCustomTag(XMLNode node) {
    StringBuilder builder = new StringBuilder();
    List<XMLNode> children = node.getChildren();
    if(children == null || children.size() < 1) return builder.toString();
    children = children.get(0).getChildren();
    if(children == null || children.size() < 1) return builder.toString();
    //    System.out.println(" name "+ node.getName());
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if(isNormalTag(n)) continue;
      builder.append('\n').append(n.getTextValue());
    }
    return builder.toString();
  }
  
  private boolean isNormalTag(XMLNode node) {
    if(node.isNode("title")) return true;
    if(node.isNode("subhead")) return true;
    if(node.isNode("time")) return true;
    if(node.isNode("image")) return true;
    if(node.getName().startsWith("paragraph")) return true;
    if(node.isNode("src")) return true;
    return false;
  }
  
}
