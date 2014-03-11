package org.vietspider.webui.search;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.ContentMapper;
import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.Application;
import org.vietspider.content.index3.HighlightBuilder;
import org.vietspider.webui.search.ads.Advertise;
import org.vietspider.webui.search.ads.Advertises;
import org.vietspider.webui.search.page.ComplexChunk;
import org.vietspider.webui.search.page.FormChunk;
import org.vietspider.webui.search.page.PageChunk;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class CachedRendererImpl {
  
  private boolean bot = false;
  
  public CachedRendererImpl(boolean bot) {
    this.bot = bot;
  }
  
//  private PageChunk pageHeader = new PageChunk("detail_header.xml");
  private ComplexChunk pageHeader = new ComplexChunk("detail_header.xml");
  private PageChunk pageFooter = new PageChunk("detail_footer.xml");
  private FormChunk pageForm1 = new FormChunk("detail_form.1.xml");
  private FormChunk pageForm2 = new FormChunk("detail_form.2.xml");
  private ComplexChunk pageContent = new ComplexChunk("detail_content.xml");
  
  public void write(OutputStream output, Article article, String query) throws Exception {
    Meta meta = article.getMeta();
    NLPRecord record = article.getNlpRecord();
    
    StringBuilder title = new StringBuilder();
    
    Map<String, String> properties = new HashMap<String, String>();
    StringBuilder subBuilder = new StringBuilder();
    if(bot && record != null && meta.hasProperty()) {
      Collection<?> collection = record.getData(NLPData.ACTION_OBJECT);
      if(collection != null && collection.size() > 0) {
        Iterator<?> iterator = collection.iterator();
        int counter = 0;
        while(counter < 2 && iterator.hasNext()) {
          if(counter > 0) subBuilder.append(", ");
          String value = NLPData.action_object(iterator.next().toString());
          subBuilder.append(value);
          if(title.length() < 1) title.append(value);
          counter++;
        }
      }
      
      collection = record.getData(NLPData.ADDRESS);
      if(collection != null && collection.size() > 0) {
        Iterator<?> iterator = collection.iterator();
        while(iterator.hasNext()) {
          String value = MetaRenderer.uppercase(iterator.next().toString());
          if(subBuilder.length() > 0) subBuilder.append(',');    
          subBuilder.append(value);
          title.append(' ').append(value);
        }
      }
      
      List<String> phones = new ArrayList<String>();
      collection = record.getData(NLPData.MOBILE);
      if(collection != null && collection.size() > 0) {
        Iterator<?> iterator = collection.iterator();
        while(iterator.hasNext()) {
          phones.add(iterator.next().toString());
        }
      }
      collection = record.getData(NLPData.TELEPHONE);
      if(collection != null && collection.size() > 0) {
        Iterator<?> iterator = collection.iterator();
        while(iterator.hasNext()) {
          phones.add(iterator.next().toString());
        }
      }
      
      if(phones.size() > 0) {
        title.append(" - Điện thoại: ");
      }
      for(int i = 0; i < phones.size(); i++) {
        if(subBuilder.length() > 0) subBuilder.append(',');
        subBuilder.append(phones.get(i));
        title.append(' ').append(phones.get(i));
      }
    }
    
    if(title.length() > 0) {
      properties.put("pattern", title.toString());
    } else {
      properties.put("pattern", meta.getTitle());
    }
    properties.put("keywords", subBuilder.toString());
    pageHeader.write(output, properties);
    
    HighlightBuilder highlighter = null;
    if(query != null) {
      highlighter = new HighlightBuilder(query);
      highlighter.setHighlightTag(
          "<b style=\"color: black; background-color: rgb(255, 255, 102);\">", "</b>");
    }
    
    properties = new HashMap<String, String>();
    if(bot) {
      properties.put("url", "#");
    } else {
      properties.put("url", meta.getSource());
    }
    String shortURL = meta.getSource();
    if(shortURL.length() > 100) {
      shortURL = shortURL.substring(0, 100) + "...";
    }
    properties.put("shorturl", shortURL);
    properties.put("time", meta.getTime());
    
    Advertise advertise = Advertises.getInstance().next(Advertise.TOP);
    if(advertise != null) {
      properties.put("advtop", advertise.generateHTML());
    } else {
      properties.put("advtop", "DÀNH CHO QUẢNG CÁO");
    }
    
    advertise = Advertises.getInstance().next(Advertise.LEFT);
    if(advertise != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(advertise.generateHTML());
      for(int i = 0; i < 0; i++) {
        advertise = Advertises.getInstance().next(Advertise.LEFT);
        if(advertise == null) break;
        builder.append(advertise.generateHTML());
      }
      properties.put("advleft", builder.toString());
    } else {
      properties.put("advleft", "DÀNH CHO QUẢNG CÁO");
    }
    
    advertise = Advertises.getInstance().next(Advertise.RIGHT);
    if(advertise != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(advertise.generateHTML());
      for(int i = 0; i < 4; i++) {
        advertise = Advertises.getInstance().next(Advertise.RIGHT);
        if(advertise == null) break;
        builder.append(advertise.generateHTML());
      }
      properties.put("advright", builder.toString());
    } else {
      properties.put("advright", "DÀNH CHO QUẢNG CÁO");
    }
    
    StringBuilder builder = new StringBuilder(); 
    
    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    pageForm1.write(byteOutput, query);
    builder.append(new String(byteOutput.toByteArray(), Application.CHARSET));
    
    String text  = meta.getTitle();
    if(highlighter != null) text  = highlighter.buildTitle(text);
    builder.append("<h4>").append(text).append("</h4>");
    
    text  = meta.getDesc();
    if(highlighter != null) text  = highlighter.buildTitle(text);
    builder.append("<p>").append(text).append("</p>");
   
    Content content = article.getContent();
    
    text = content.getContent();
    if(highlighter != null) text  = highlighter.buildTitle(text);
    builder.append("<p>").append(text).append("</p>");

    CachedPropertiesRenderer.buildProperties(bot, builder, article);
    
    String other = meta.getPropertyValue("others");
    if(other != null && other.length() > 0) {
      buildOthers(builder, other);
    }
    
    properties.put("article", builder.toString());
    
    pageContent.write(output, properties);
    
    pageForm2.write(output, query);
    
    pageFooter.writeNoEncode(output, null);//SEOPageGenerator.getInstance().getKeywords()
  }
  
  private void buildOthers(StringBuilder builder, String text) {
    int idx = text.indexOf('\n');
    if(idx > 0) text = text.substring(idx + 1);
    builder.append("<br>");

    String [] elements = text.split(Article.RAW_TEXT_ARTICLE_SEPARATOR);
    for(int i = 0; i < elements.length; i++) {
      Meta meta = null;
      if(elements[i].indexOf(ContentMapper.SEPARATOR) > -1) {
        Article article = new Article();
        ContentMapper.text2MetaData(article, elements[i]);
        meta = article.getMeta();
      } else {
        meta = ContentMapper.text2Meta(elements[i]);
      }
      if(meta == null) continue;
      
      builder.append("<p class=\"meta\"><a href=\"/site/cached/");
      builder.append(meta.getId());
      builder.append("/").append(meta.getAlias());
      builder.append("\" target=\"_blank\" class=\"meta_title\">");
      builder.append(meta.getTitle());
      builder.append("</a></p>");
      //      System.out.println(" bebebe ===  >" + meta.getId() + " : "+ meta);
    }
  }
  
}
