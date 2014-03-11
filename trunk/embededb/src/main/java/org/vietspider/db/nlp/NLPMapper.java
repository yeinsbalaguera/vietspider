/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.nlp;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.vietspider.bean.CommonMapper;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class NLPMapper extends CommonMapper  {

  private  DecimalFormat currencyFormat = new DecimalFormat();

  public NLPMapper() {
    Currency currency = Currency.getInstance(Locale.FRANCE);
    currencyFormat.setCurrency(currency);
  }

/*  public String record2Text(NLPRecord record) {
    StringBuilder builder = new StringBuilder();
    builder.append('[').append(record.getMetaId()).append(']');
    List<NLPRecordItem> items = record.getItems();
    for(int i = 0; i < items.size(); i++) {
      NLPRecordItem item = items.get(i);  
      builder.append('[');  
      encode(builder, item.getType());
      builder.append(',');
      encode(builder, item.getName());
      Iterator<Map.Entry<String, String>> iterator = item.getValues().entrySet().iterator();
      while(iterator.hasNext()) {
        Map.Entry<String, String> entry = iterator.next();
        builder.append(',');
        encode(builder, entry.getKey());
        builder.append(':');
        encode(builder, entry.getValue());
      }
      builder.append(']');
    }
    //    System.out.println(" mapper duoc "+ builder);
    return builder.toString();
  }*/

  /*public void setNLPValue(Article article, NLPRecord record) {
    Properties properties = article.getMeta().getProperties();
    String postdate = record.getData("WebPageAnalysis", "postdate");
    if(postdate != null) properties.setProperty("nlp.postdate", postdate);

    StringBuilder builder = new StringBuilder();
    String name = record.getData("Product", "name");
    if(name != null) builder.append(name);
    String price = record.getData("Product", "price");
    if(price != null) {
      if(builder.length() > 0) builder.append(", ");
      builder.append("giá: ").append(price);
    }
    if(builder.length() > 0) properties.setProperty("nlp.product", builder.toString());

    builder.setLength(0);
    String tel = record.getData("Contact", "tel");
    if(tel != null) builder.append("Tel: ").append(tel);
    String mobile = record.getData("Contact", "mobile");
    if(mobile != null) {
      if(builder.length() > 0) builder.append(", ");
      builder.append("Mobile: ").append(mobile);
    }
    String fax = record.getData("Contact", "fax");
    if(fax != null) {
      if(builder.length() > 0) builder.append(", ");
      builder.append("Fax: ").append(fax);
    }
    if(builder.length() > 0) properties.setProperty("nlp.phone", builder.toString());


    builder.setLength(0);
    String number = record.getData("Contact", "addressNumber");
    if(number != null) builder.append("Tel: ").append(tel);
    String mobile = record.getData("Contact", "mobile");
    if(mobile != null) {
      if(builder.length() > 0) builder.append(", ");
      builder.append("Mobile: ").append(mobile);
    }
    String fax = record.getData("Contact", "fax");
    if(fax != null) {
      if(builder.length() > 0) builder.append(", ");
      builder.append("Fax: ").append(fax);
    }
    if(builder.length() > 0) properties.setProperty("nlp.phone", builder.toString());
  }*/

//  private String buildProduct(NLPRecord record) {
//    StringBuilder builder = new StringBuilder();
//    String name = record.getData("Product", "name");
//    if(name != null) builder.append(name);
//    String price = record.getData("Product", "price");
//    if(price != null) {
//      if(builder.length() > 0) builder.append(", ");
//      int idx = price.indexOf(' ');
//      String donvi = "";
//      if(idx > 0) {
//        donvi = price.substring(idx);
//        price = price.substring(0, idx);
//      }
//      double priceValue = Double.valueOf(price);
//      builder.append("giá: ").append(currencyFormat.format(priceValue)).append(donvi);
//    }
//    return builder.toString();
//  }

  public String buildPhone(NLPRecord record) {
    StringBuilder builder = new StringBuilder();
    String tel = record.getOneData(NLPData.TELEPHONE);
    if(tel != null) builder.append("Tel: ").append(tel);
    
    String mobile = record.getOneData(NLPData.MOBILE);
    if(mobile != null) {
      if(builder.length() > 0) builder.append(", ");
      builder.append("Mobile: ").append(mobile);
    }
    
//    String tel = record.getData("Contact", "tel");
//    if(tel != null) builder.append("Tel: ").append(tel);
//    String mobile = record.getData("Contact", "mobile");
//    if(mobile != null) {
//      if(builder.length() > 0) builder.append(", ");
//      builder.append("Mobile: ").append(mobile);
//    }
    
//    String fax = record.getData("Contact", "fax");
//    if(fax != null) {
//      if(builder.length() > 0) builder.append(", ");
//      builder.append("Fax: ").append(fax);
//    }
    
//    String email = record.getData("Contact", "email");
//    if(email != null) {
//      if(builder.length() > 0) builder.append(", ");
//      builder.append("Email: ").append(email);
//    }
    return builder.toString();
  }
  
  public String buildCommon(List<String> list) {
    if(list == null || list.size() < 1) return null;
    if(list.size() > 3) return list.get(0);
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < list.size(); i++) {
      if(builder.length() > 0) builder.append('-');
      builder.append(list.get(i));
    }
    return builder.toString();
  }
  
//  public String buildAddress(NLPRecord record) {
//    List<String> list = record.getOneData(NLPData.TELEPHONE);
//    
//    StringBuilder builder = new StringBuilder();
//    String number = record.getData("Contact", "addressNumber");
//    if(number != null && !"unknown".equalsIgnoreCase(number)) {
//      builder.append("Số ").append(number);
//    }
//
//    String street = record.getData("Contact", "street");
//    if(street != null && !"unknown".equalsIgnoreCase(street)) {
//      if(builder.length() > 0) builder.append(", ");
//      builder.append(street);
//    }
//    
//    String district = record.getData("Contact", "district");
//    if(district != null && !"unknown".equalsIgnoreCase(district)) {
//      if(builder.length() > 0) builder.append(", ");
//      builder.append(district);
//    }
//
//    String city = record.getData("Contact", "city");
//    if(city != null && !"unknown".equalsIgnoreCase(city)) {
//      if(builder.length() > 0) builder.append(", ");
//      builder.append(city);
//    }
//    return builder.toString();
//  }

  /*public NLPRecord text2Record(String text) {
    NLPRecord record = new NLPRecord();
    int from = 0;
    int start = text.indexOf('[', from);
    int end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return null;
    record.setMetaId(text.substring(start+1, end));

    List<NLPRecordItem> items = new ArrayList<NLPRecordItem>();
    while(true) {
      from = end + 1;
      start = text.indexOf('[', from);
      end = text.indexOf(']', from);
      if(start < 0 || end  < 0) break;
      String value = text.substring(start+1, end);
      if(value.length() == 1 && value.charAt(0) == '~') continue;
      NLPRecordItem item = new NLPRecordItem();
      int s = 0;
      int idx = value.indexOf(',');
      if(idx < 0) continue;
      item.setType(value.substring(s, idx));
      s = idx + 1;

      idx = value.indexOf(',', s);
      if(idx < 0) continue;
      item.setName(value.substring(s, idx));
      s = idx + 1;

      Map<String, String> itemValues = new HashMap<String, String>();
      while(s < value.length()) {
        idx = value.indexOf(',', s);
        if(idx < 0) idx = value.length();
        String itemValue = value.substring(s, idx);
        int seIdx = itemValue.indexOf(':');
        if(seIdx > 0)  {
          itemValues.put(itemValue.substring(0, seIdx), itemValue.substring(seIdx+1));
        }
        s = idx + 1;
      }

      item.setValues(itemValues);
      items.add(item);
    }

    record.setValues(items);

    return record;
  }*/
  
  public String toPresentation(String text) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < text.length(); i++) {
      if(i == 0 || Character.isWhitespace(text.charAt(i - 1))) {
        builder.append(Character.toUpperCase(text.charAt(i)));
      } else {
        builder.append(text.charAt(i));
      }
    }
    return builder.toString();
  }


}
