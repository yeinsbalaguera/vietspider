/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 22, 2011  
 */
class CachedPropertiesRenderer {

  static void buildProperties(boolean bot, StringBuilder builder, Article article) {
    Meta meta = article.getMeta();

    NLPRecord record = article.getNlpRecord();

    StringBuilder subBuilder = new StringBuilder();
    if(meta.hasProperty() && record != null) {
      //      String value = meta.getPropertyValue("nlp.product");
      //      if(value != null && value.length() > 0) {
      //        subBuilder.append(Character.toUpperCase(value.charAt(0)));
      //        subBuilder.append(value, 1, value.length());
      //      }

      Collection<?> collection = record.getData(NLPData.ACTION_OBJECT);

      //      String value = meta.getPropertyValue("action_object");
      if(collection != null && collection.size() > 0) {
        Iterator<?> iterator = collection.iterator();
        int counter = 0;
        while(iterator.hasNext()) {
          if(counter > 0) subBuilder.append(", ");
          subBuilder.append(NLPData.action_object(iterator.next().toString()));
          counter++;
        }
      }

      collection = record.getData(NLPData.ADDRESS);
      if(collection != null && collection.size() > 0) {
        Iterator<?> iterator = collection.iterator();
        while(iterator.hasNext()) {
          Object obj = iterator.next();
          if(obj == null) continue;
          String value = MetaPropertiesRenderer.uppercase(obj.toString());
          if(subBuilder.length() > 0) subBuilder.append(" - ");

          if(!bot && value.length() > 0) {
            subBuilder.append("<a  href=\"/site/search/1/?query=field:address:");
            try {
              subBuilder.append(URLEncoder.encode(value, Application.CHARSET));
            } catch (Exception e) {
              subBuilder.append('#');
            }
            subBuilder.append("\" class=\"properties\">");
            subBuilder.append(value);
            subBuilder.append("</a>");
          } else {
            subBuilder.append(value);  
          }
        }
      }

      String value = meta.getPropertyValue("owner");
      if("true".equals(value)) {
        if(subBuilder.length() > 0) subBuilder.append(" - ");
        subBuilder.append("Chính chủ");
      }

      collection = record.getData(NLPData.AREA);
      if(collection != null && collection.size() > 0) {
        if(subBuilder.length() > 0) subBuilder.append(" - ");
        subBuilder.append("Diện tích: ");
        Iterator<?> iterator = collection.iterator();
        int counter = 0;
        while(iterator.hasNext()) {
//          Unit unit = (Unit)iterator.next();
//          while(unit != null) {
          if(counter > 0) subBuilder.append(", ");
          subBuilder.append(iterator.next()).append("m2");
          counter++;
//            unit = unit.getNext();
//          }
        }
      }

      collection = record.getData(NLPData.PRICE);
      if(collection != null && collection.size() > 0) {
        if(subBuilder.length() > 0) subBuilder.append(" - ");
        subBuilder.append("Giá: ");
        Iterator<?> iterator = collection.iterator();
        int counter = 0;
        while(iterator.hasNext()) {
          if(counter > 0) subBuilder.append(", ");
          subBuilder.append(iterator.next().toString());
          counter++;
        }
      }

      //      value = meta.getPropertyValue("nlp.price");
      //      if(value != null && value.length() > 0) {
      //        if(subBuilder.length() > 0) subBuilder.append(" - ");
      //        if(value.length() > 30) value = value.substring(0, 30);
      //        subBuilder.append("Giá: ").append(value);
      //      }

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

      if(phones.size() > 0 
          && subBuilder.length() > 0) subBuilder.append(" - Điện thoại: ");
      int max = phones.size();
      for(int i = 0; i < max; i++) {
        if(i > 0) subBuilder.append(", ");
        if(!bot) {
          subBuilder.append("<a  href=\"/site/search/1/?query=");
          subBuilder.append("field:phone:").append(phones.get(i));
          //        subBuilder.append("&search=Tìm+kiếm\" class=\"properties\">");
          subBuilder.append("\" class=\"properties\">");
        }
        subBuilder.append(phones.get(i));
        if(!bot) subBuilder.append("</a>");
      }
    }

    builder.append("<div class=\"properties\">");
    builder.append(subBuilder);
    builder.append("</div>");
  }
}
