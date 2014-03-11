/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.renderer.NLPRenderer;
import org.vietspider.nlp.impl.PhoneUtils;
import org.vietspider.nlp.impl.Unit;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2011  
 */
public class NlpHandler {

  public static NLPRecord  process(Meta meta, HTMLNode root) throws Exception  {
    NlpProcessor processor = NlpProcessor.getProcessor();

    StringBuilder builder = new StringBuilder();
    builder.append(meta.getTitle());
    builder.append('\n').append('\n').append(meta.getDesc());
    NLPRenderer textRenderer = new NLPRenderer(root);
    builder.append('\n').append('\n').append(textRenderer.getTextValue());

    //    if(builder.length() < 10) {
//    File file  = new File("D:\\Temp\\", meta.getId() + ".txt");
    //      File file  = new File("D:\\Temp\\renderer\\", meta.getId() + ".html");
//    org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    //    }

    return process(meta, processor.process(meta.getId(), builder.toString()));
  }
  
  @SuppressWarnings("unchecked")
  public static NLPRecord  process(Meta meta, Map<Short, Collection<?>> map) throws Exception  {
    NLPRecord record = new NLPRecord(meta.getId());
    Collection<?> _values = map.get(NLPData.EMAIL);
    if(_values != null && _values.size() > 0) {
      record.setData(NLPData.EMAIL, _values.toArray(new String[0]));
      //      StringBuilder mailBuilder = new StringBuilder();
      //      Iterator<?> iterator = _values.iterator();
      //      while(iterator.hasNext()) {
      //        if(mailBuilder.length() > 0) mailBuilder.append(", ");
      //        mailBuilder.append(iterator.next());
      //      }
      //      setData(nlpRecord, "Contact", "email", mailBuilder.toString());
      //      System.out.println(" email "+ builder.toString());
    }

    phone(record, map);
    area(record, map);

    _values = map.get(NLPData.PRICE);
    if(_values != null) record.setData(NLPData.PRICE, _values);
    
    _values = map.get(NLPData.PRICE_TOTAL);
    if(_values != null) record.setData(NLPData.PRICE_TOTAL, _values);
    
    _values = map.get(NLPData.PRICE_MONTH);
    if(_values != null) record.setData(NLPData.PRICE_MONTH, _values);
    
    _values = map.get(NLPData.PRICE_UNIT_M2);
    if(_values != null) record.setData(NLPData.PRICE_UNIT_M2, _values);


    _values = map.get(NLPData.ADDRESS);
    if(_values != null) record.setData(NLPData.ADDRESS, _values);

    _values = map.get(NLPData.CITY);
    if(_values != null && _values.size() > 0) {
      record.setData(NLPData.CITY, _values);
      meta.putProperty("region", ((List<String>)_values).get(0));
    }
    
    _values = map.get(NLPData.ACTION_OBJECT);
    if(_values != null && _values.size() > 0) {
      List<String> list = new ArrayList<String>();
      Iterator<?> iterator = _values.iterator();
      while(iterator.hasNext()) {
        list.add(iterator.next().toString());
      }
      record.setData(NLPData.ACTION_OBJECT, list);
      meta.putProperty("action_object", list.get(0));
    }
    
    return record;
  }

  private static boolean hasValidData(Collection<?> values) {
    Iterator<?> iterator = values.iterator();
    while(iterator.hasNext()) {
      Unit unit = (Unit)iterator.next();
      if(unit.getValue() < 1000) return true;
    }
    return false;
  }

  private static void phone(NLPRecord nlpRecord, Map<Short, Collection<?>> map) {
    Collection<?> _values = map.get(NLPData.PHONE);
    if(_values == null) return;
    //    if(_values != null && _values.size() > 0) {
    //      StringBuilder mobileBuilder = new StringBuilder();
    //      StringBuilder telBuilder = new StringBuilder();
    List<String> mobiles = new ArrayList<String>();
    List<String> telephones = new ArrayList<String>();

    Iterator<?> iterator = _values.iterator();
    while(iterator.hasNext()) {
      String number = iterator.next().toString();
      if(PhoneUtils.isMobile(number)) {
        mobiles.add(number);
      } else {
        telephones.add(number);
        //          if(telBuilder.length() > 0) telBuilder.append(", ");
        //          telBuilder.append(number);
      }
      //        System.out.println(" tel "+ telBuilder.toString());
      //        System.out.println(" mobile "+ mobileBuilder.toString());
      if(telephones.size() > 0) {
        nlpRecord.setData(NLPData.TELEPHONE, telephones);
        //        setData(nlpRecord, "Contact", "tel", telBuilder.toString());
      }

      if(mobiles.size() > 0) {
        nlpRecord.setData(NLPData.MOBILE, mobiles);
        //        setData(nlpRecord, "Contact", "mobile", mobileBuilder.toString());
      }
    }
    //    } else {
    //      File file  = new File("D:\\Temp\\", meta.getId() + ".txt");
    //      org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    //    }
  }

  private static void area(NLPRecord nlpRecord, Map<Short, Collection<?>> map) {
    Collection<?> _values = map.get(NLPData.AREA);
    if(_values == null) return;
    //  if(_values.size() < 1) {
    //    File file  = new File("D:\\Temp\\", meta.getId() + ".txt");
    //    org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    //  }

    if(hasValidData(_values)) {
      Iterator<?> iterator = _values.iterator();
      while(iterator.hasNext()) {
        Unit unit = (Unit)iterator.next();
        if(unit.getValue() >= 1000) iterator.remove();
      } 
    }

    if(_values.size() > 0) {
//      List<String> list = new ArrayList<String>();
      Iterator<?> iterator = _values.iterator();
      Unit unit = (Unit)iterator.next();
//      list.add(String.valueOf(unit.getValue()));
      while(iterator.hasNext()) {
        Unit temp = (Unit)iterator.next();
//        list.add(String.valueOf(temp.getValue()));
        unit.merge(temp);
      }
      nlpRecord.setData(NLPData.AREA_SHORT, unit.toShortString());
      nlpRecord.setData(NLPData.AREA, unit.toString());//list.toArray(new String[0]));
      //    setData(nlpRecord, "Data", "area", area.toShortString());
//      System.out.println("  ===  area >" + unit.toString());
    }
  }

  //  private static void setData(NLPRecord nlpRecord, 
  //      String type, String item, String value) {
  //    String data = nlpRecord.getData(type, item);
  //    if(data != null) {
  //      nlpRecord.setData(type, item, value);
  //      return;
  //    }
  //    NLPRecordItem recordItem = new NLPRecordItem();
  //    recordItem.setType(type);
  //    recordItem.setName(item);
  //    Map<String, String> map = new java.util.HashMap<String, String>();
  //    map.put(item, value);
  //    recordItem.setValues(map);
  //    nlpRecord.getItems().add(recordItem);
  //  }


}
