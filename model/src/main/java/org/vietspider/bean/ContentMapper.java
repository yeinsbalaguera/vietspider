/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vietspider.chars.TextSpliter;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class ContentMapper extends CommonMapper {
  
  public final static String SEPARATOR = "[vspider][#line][/vspider]";
  
  public static String metaData2Text(Article article) {
    StringBuilder builder = new StringBuilder();
    String meta = meta2Text(article.getMeta());
    if(meta == null) meta = "";
    builder.append(meta).append(SEPARATOR);
    
    Domain _domain = article.getDomain();
    String domain = _domain != null ? domain2Text(_domain) : "";
    if(domain == null) domain = "";
    builder.append(domain).append(SEPARATOR);
    
    builder.append("[image]");
    List<Image> images = article.getImages();
    if(images != null) {
      for(int i = 0; i < images.size(); i++) {
        if(images.get(i) == null) continue;
        builder.append(image2Text(images.get(i))).append('\n');
      }
    }
    builder.append("[/image]");
    builder.append(SEPARATOR);

    NLPRecord nlpRecord = article.getNlpRecord();
    String xmlNlp = ""; 
    if(nlpRecord != null) {
      try {
        xmlNlp = Object2XML.getInstance().toXMLDocument(nlpRecord).getTextValue();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    builder.append(xmlNlp).append(SEPARATOR);
    
    List<Relation> listRels = article.getRelations();
    
    if(listRels != null) {
      Relations relations = new Relations(article.getId());
      for(int i = 0; i < listRels.size(); i++) {
        relations.addRelation(listRels.get(i));
      }
      builder.append(relations2Text(relations));
    }
    
    return builder.toString();
  }
  
  public static void text2MetaData(Article article, String value) {
    try {
      List<String> temp = new ArrayList<String>();
      int start = 0;
      int end = value.indexOf(SEPARATOR);
      while(end > -1) {
        temp.add(value.substring(start, end));
        start = end + SEPARATOR.length();
        end = value.indexOf(SEPARATOR, start);
      }
      temp.add(value.substring(start, value.length()));
      String [] elements = temp.toArray(new String[temp.size()]);//value.split(SEPARATOR);

      //    System.out.println("=============== >"+ elements[2]);

      //    value.indexOf("--");
      if((elements[0] = elements[0].trim()).length() > 0) {
        article.setMeta(text2Meta(elements[0]));
      }

      if(elements.length > 1 
          && (elements[1] = elements[1].trim()).length() > 0) {
        article.setDomain(text2Domain(elements[1]));
      }

      if(elements.length > 2 && (elements[2] = elements[2].trim()).length() > 0 && elements[2].length()  > 7) {
        //      System.out.println(" peope "+ elements[2]);
        //      System.out.println("[image]".length() + " : " + "[/image]".length());
        elements[2] = elements[2].substring(7, elements[2].length() - 8);
        //      System.out.println(" ===  > "+ elements[2]);
        TextSpliter spliter = new TextSpliter();
        List<String> list = spliter.toList(elements[2], '\n');
        for(int i = 0; i < list.size(); i++) {
          if(list.get(i).length() < 1) continue;
          Image image = text2Image(list.get(i));
          //        if(image == null) System.out.println(" ====  >" + list.get(i));
          if(image != null) article.addImage(image);
        }
      }

      if(elements.length > 3 && (elements[3] = elements[3].trim()).length() > 0) {
        try {
          article.setNlpRecord(XML2Object.getInstance().toObject(NLPRecord.class, elements[3]));
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }

      if(elements.length > 4 && (elements[4] = elements[4].trim()).length() > 0) {
        Relations relations = text2Relations(elements[4]);
        if(relations != null) {
          article.setRelations(relations.getRelations());
        }
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      //      LogService.getInstance().setMessage(e, e.toString());
//      LogService.getInstance().setMessage(null, value);
      //      e.printStackTrace();
      //      System.out.println("=============>" + value);
    }
  }
  
  public static String meta2Text(Meta meta) {
    StringBuilder builder = new StringBuilder();
    builder.append('[').append(meta.getId()).append(']');
    builder.append('['); encode(builder, meta.getTitle()); builder.append(']');
    builder.append('['); encode(builder, meta.getDesc()); builder.append(']');
    builder.append('['); encode(builder, meta.getImage()); builder.append(']');
    builder.append('[').append(meta.getTime() == null ? '~' : meta.getTime()).append(']');
    builder.append('[').append(meta.getSourceTime() == null ? '~' : meta.getSourceTime()).append(']');
    builder.append('[').append(meta.getDomain() == null ? '~' : meta.getDomain()).append(']');
    builder.append('['); encode(builder, meta.getSource()); builder.append(']');
    
    Iterator<String> iterator = meta.iteratorPropertyKey();
    while(iterator.hasNext()) {
//      Map.Entry<String, Object> entry = iterator.next();
      String keyProperty  =  iterator.next();//entry.getKey();
      if(keyProperty.startsWith("temp.")) continue;
      Object valueProperty  = meta.getPropertyValue(keyProperty);
      if(!(valueProperty instanceof String)) continue;
      builder.append('['); encode(builder, keyProperty); builder.append(']');
      builder.append('['); encode(builder, (String)valueProperty); builder.append(']');
    }
    return builder.toString();
  }
  
  public static Meta text2Meta(String text) {
    Meta meta = new Meta();
    int from = 0;
    int start = text.indexOf('[', from);
    int end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return null;
    meta.setId(text.substring(start+1, end));
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return meta;
    String value = text.substring(start+1, end);
    if(value.length() == 1 && value.charAt(0) == '~') value = null;
    meta.setTitle(decode(value));
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return meta;
    value = text.substring(start+1, end);
    if(value.length() == 1 && value.charAt(0) == '~') value = null;
    meta.setDesc(decode(value));
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return meta;
    value = text.substring(start+1, end);
    if(value.length() == 1 && value.charAt(0) == '~') value = null;
    meta.setImage(decode(value));
    
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return meta;
    value = text.substring(start+1, end);
    if(value.length() == 1 && value.charAt(0) == '~') value = null;
    meta.setTime(value);
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return meta;
    value = text.substring(start+1, end);
    if(value.length() == 1 && value.charAt(0) == '~') value = null;
    meta.setSourceTime(value);
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return meta;
    value = text.substring(start+1, end);
    if(value.length() == 1 && value.charAt(0) == '~') value = null;
    meta.setDomain(value);
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return meta;
    value = text.substring(start+1, end);
    if(value.length() == 1 && value.charAt(0) == '~') value = null;
    meta.setSource(decode(value));
    
    while(true) {
      from = end + 1;
      start = text.indexOf('[', from);
      end = text.indexOf(']', from);
      if(start < 0 || end  < 0) return meta;
      String keyProperty = text.substring(start+1, end);
      if(keyProperty.length() == 1 && keyProperty.charAt(0) == '~') return meta;
      
      from = end + 1;
      start = text.indexOf('[', from);
      end = text.indexOf(']', from);
      if(start < 0 || end  < 0) return meta;
      String valueProperty = text.substring(start+1, end);
      if(valueProperty.length() == 1 && valueProperty.charAt(0) == '~') value = "";
      
      meta.putProperty(keyProperty, decode(valueProperty));
    }
  }
  
  public static String relations2Text(Relations relations) {
    StringBuilder builder = new StringBuilder();
    builder.append('[').append(relations.getMetaId()).append(']');
    List<Relation> list = relations.getRelations();
    for(int i = 0; i < list.size(); i++) {
      Relation relation = list.get(i);
      builder.append('[').append(relation.getRelation()).
              append(',').append(relation.getPercent()).append(']');
    }
    return builder.toString();
  }
  
  public static Relations text2Relations(String text) {
    int from = 0;
    int start = text.indexOf('[', from);
    int end = text.indexOf(']', from);
    if(start < 0 || end  < 0 || start + 1 >= end) return null;
    String metaId = text.substring(start+1, end);
    Relations relations = new Relations(metaId);
    
    while(true) {
      from = end + 1;
      start = text.indexOf('[', from);
      end = text.indexOf(',', from);
      if(start < 0 || end  < 0 || start + 1 >= end) return relations;
      String relId = text.substring(start+1, end);
      start = end+1;
      end = text.indexOf(']', from);
      if(start < 0 || end  < 0) return relations;
      try {
        int percent = Integer.parseInt(text.substring(start, end));
        Relation relation = new Relation();
        relation.setMeta(metaId);
        relation.setRelation(relId);
        relation.setPercent(percent);
        relations.getRelations().add(relation);
      } catch (Exception e) {
        return relations;
      }
    }
  }
  
  public static String image2Text(Image image) {
    StringBuilder builder = new StringBuilder();
    builder.append('[').append(image.getId()).append(']');
    builder.append('[').append(image.getMeta()).append(']');
    builder.append('[').append(image.getType() == null ? '~' : image.getType()).append(']');
    builder.append('[').append(image.getName() == null ? '~' : image.getName()).append(']');
    return builder.toString();
  }
  
  public static Image text2Image(String text) {
    Image image = new Image();
    int from = 0;
    int start = text.indexOf('[', from);
    int end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return null;
    image.setId(text.substring(start+1, end));
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return null;
    image.setMeta(text.substring(start+1, end));
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start > 0 && end  > 0) {
      String value = text.substring(start+1, end);
      if(value.length() == 1 && value.charAt(0) == '~') value = null;
      image.setType(value);
    }
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start > 0 && end  > 0) {
      String value = text.substring(start+1, end);
      if(value.length() == 1 && value.charAt(0) == '~') value = null;
      image.setName(value);
    }
    
    return image;
  }
  
  public static String domain2Text(Domain domain) {
    StringBuilder builder = new StringBuilder();
    builder.append('[').append(domain.getDate()).append(']');
    builder.append('[').append(domain.getGroup()).append(']');
    builder.append('[').append(domain.getCategory()).append(']');
    builder.append('[').append(domain.getName()).append(']');
    return builder.toString();
  }
  
  public static Domain text2Domain(String text) {
    int from = 0;
    int start = text.indexOf('[', from);
    int end = text.indexOf(']', from);
//    System.out.println(" ===========  >"+ text);
    if(start < 0 || end  < 0) return null;
    String date = text.substring(start+1, end);
    if(date.length() == 1 && date.charAt(0) == '~') date = null;
    
    String group = null;
    String category = null;
    String name  = null;
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start > 0 && end  > 0) {
      group = text.substring(start+1, end);
      if(group.length() == 1 && group.charAt(0) == '~') group = null;
    }
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start > 0 && end  > 0) {
      category = text.substring(start+1, end);
      if(category.length() == 1 && category.charAt(0) == '~') category = null;
    }

    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start > 0 && end  > 0) {
      name = text.substring(start+1, end);
      if(name.length() == 1 && name.charAt(0) == '~') name = null;
    }
    
    return new Domain(date, group, category, name);
  }
  
  public static Article toArticle(String text) throws Exception {
    String [] elements = text.split(Article.RAW_TEXT_ELEMENT_SEPARATOR);
    Article article = new Article();
    
//    System.out.println("\n\n\n\n");
//    for(int i = 0; i < elements.length; i++) {
//      System.out.println(elements[i]);
//      System.out.println("=======================");
//    }
    
    Meta meta = null;
    int idx = elements[0].indexOf(ContentMapper.SEPARATOR);
    if(idx > -1) {
      text2MetaData(article, text);
      meta = article.getMeta();
    } else {
      meta = text2Meta(elements[0]);
      if(meta == null) return null;
      article.setMeta(meta);
    }
    
    Content content = new Content();
    content.setMeta(meta.getId());
    content.setContent(elements[1]);
    try {
      SimpleDateFormat dateTimeFormat = CalendarUtils.getDateTimeFormat();
      SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
      if(meta.getTime() != null) {
        content.setDate(dateFormat.format(dateTimeFormat.parse(meta.getTime())));
      }
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    article.setContent(content);
    
    if(idx > -1) return article;
    
    if("domain is null".equals(elements[2].trim())) {
      try {
        URL url = new URL(meta.getSource());
        Domain domain = new Domain();
        domain.setName(url.getHost());
        article.setDomain(domain);
      } catch (Exception e) {
      }
    } else {
      Domain domain = text2Domain(elements[2]);
      if(domain != null) article.setDomain(domain);
    }
    
    if(!"relations is null".equals(elements[3].trim())) {
      Relations relations = text2Relations(elements[3]);
      if(relations != null) article.setRelations(relations.getRelations());
    }
    
    
    if(elements.length > 4 && !"nlp is null".equals(elements[4])) {
      try {
        NLPRecord nlpRecord = XML2Object.getInstance().toObject(NLPRecord.class, elements[4]);
        if(nlpRecord != null) article.setNlpRecord(nlpRecord);
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    }
    
    return article;
  }
  
  public static void main(String[] args) {
    Article article = new Article();
    String text =  "[201109041245550007][Các ngân hàng lớn đồng thuận hạ lãi suất cho vay][Lãnh đạo 12 ngân hàng thương mại lớn tại Hà Nội đã đồng thuận cao với chủ trương điều chỉnh lãi suất cho vay xuống còn từ 17-19% từ đầu tháng 9 tới theo như chủ trương của Ngân hàng Nhà nước.][~][04/09/2011 12:45:55][26/08/2011 21:08:55][-679919315][http&#58;//hanoimoi.com.vn/newsdetail/Kinh-te/521634/cac-ngan-hang-lon-dong-thuan-ha-lai-suat-cho-vay.htm][desc.extractor.remove][true][vspider][#line][/vspider][04/09/2011][ARTICLE][Tài Chính][Hà Nội Mới][vspider][#line][/vspider][image][/image][vspider][#line][/vspider][vspider][#line][/vspider][201109041245550007][201109041122250048,35][201109041243570030,31][201109041239480032,31][201108231632450009,29][201109041121520006,27][201109041240090020,27][201108231651010031,26][201109041117280028,25][201109041243470039,25][201109041245000014,25]";
    text2MetaData(article, text);
  }
  
}
