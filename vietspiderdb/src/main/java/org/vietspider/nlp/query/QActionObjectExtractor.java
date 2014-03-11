/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
/***************************************************************************
 * //{"chung cư cao cấp", "1,4"},  {"cccc", "1,4"},
{"bàn giao căn hộ", "1,4"},  {"căn hộ", "giao nhà", "1,4"}, 
//{"căn hộ cao cấp", "1,4"}, {"dự án cao ốc căn hộ", "1,4"}, {"quy hoạch", "dự án căn hộ", "1,4"},
//{"căn hộ giá", "1,4"}, {"thanh toán", "căn hộ", "1,4"}, 
//{"dự án", "chung cư", "1,4"}, 
//{"căn hộ", "giá rẻ", "1,4"}, {"căn hộ", "giá sốc", "1,4"},  {"căn hộ", "giá gốc", "1,4"},
{"ch cao cấp", "1,4"}, 
 **************************************************************************/
package org.vietspider.nlp.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpExtractor;
import org.vietspider.nlp.impl.ao.ActionCommon1Plugin;
import org.vietspider.nlp.impl.ao.ActionCommon2Plugin;
import org.vietspider.nlp.impl.ao.ActionObject;
import org.vietspider.nlp.impl.ao.ActionObjectPlugin;
import org.vietspider.nlp.impl.ao.ActionTop1Plugin;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 18, 2010  
 */
public class QActionObjectExtractor implements INlpExtractor<ActionObject> {
  
  private ActionObjectPlugin [] plugins = {
    new ActionTop1Plugin(), new QActionTop2Plugin(),
    
    new ActionCommon1Plugin(), new ActionCommon2Plugin(),
    
//    new ActionObjectBottom4Plugin(),
//    new ActionObjectBottom0Plugin(), 
//    new ActionObjectBottom1Plugin(),
//    new ActionObjectBottom2Plugin(), new ActionObjectBottom3Plugin()
   
  };

  @SuppressWarnings("all")
  public void extract(String id, Collection<?> values, TextElement element) {
    //    System.out.println(lower);
    ArrayList<ActionObject> list = (ArrayList<ActionObject>) values;

    if(list.size() == 1 && list.get(0).getData().charAt(0) == '>') return;
    
    for(int i = 0; i < plugins.length; i++) {
      if(plugins[i].type() != ActionObjectPlugin.TOP) continue;
      if(plugins[i].process(element, list) == ActionObjectPlugin._BREAK) return;
    }
    

    int size = list.size();
//    System.out.println(size);
    for(int i = 0; i < plugins.length; i++) {
      if(plugins[i].type() != ActionObjectPlugin.COMMON) continue;
      if(plugins[i].process(element, list) == ActionObjectPlugin._BREAK) return;
    }
//    System.out.println(list.size());
    if(list.size() != size) return;

    for(int i = 0; i < plugins.length; i++) {
      if(plugins[i].type() != ActionObjectPlugin.BOTTOM) continue;
      if(plugins[i].process(element, list) == ActionObjectPlugin._BREAK) return;
    }
  }

  public boolean isExtract(TextElement element) {
    if(element.getLower().startsWith("tag:")) return false;
    return element.getValue().length() > 0;
    //    return element.getPoint(type()) != null;
  }

  public short type() { return NLPData.ACTION_OBJECT; }

  public Collection<ActionObject> createCollection() { 
    return new ArrayList<ActionObject>();
  }

  @SuppressWarnings("unchecked")
  public void closeCollection(Collection<?> collection) {
    List<ActionObject> list = (List<ActionObject>) collection;

    boolean remove = false;
    for (int i = 0; i < list.size(); i++) {
      if("9,7".equals(list.get(i).getData()) 
          || list.get(i).getData().charAt(0) == '<') continue;
      remove = true;
      break;
    }

    if(!remove) {
      cleanItem(list);
      return;
    }

    List<ActionObject> removes = new ArrayList<ActionObject>();

    for (int i = 0; i < list.size(); i++) {
      if("9,7".equals(list.get(i).getData())
          || list.get(i).getData().charAt(0) == '<') removes.add(list.get(i));
    }

    for(int i = 0; i < removes.size(); i++) {
      list.remove(removes.get(i));
    }

    cleanItem(list);

  }

  private void cleanItem(List<ActionObject> list) {
    for (int i = 0; i < list.size(); i++) {
      String value = list.get(i).getData();
      char c = value.charAt(0);
      //      System.out.println(value + " : "+ c);
      if(Character.isLetterOrDigit(c)) continue;
      list.get(i).setData(value.substring(1));
//      list.set(i, value);
    }
  }

 /* private String cleanItem(String value) {
    char c = value.charAt(0);
    //      System.out.println(value + " : "+ c);
    if(Character.isLetterOrDigit(c)) return value;
    return value.substring(1);
  }*/
  
 

}
