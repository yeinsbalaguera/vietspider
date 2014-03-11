/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.impl.AddressNormalization;
import org.vietspider.nlp.impl.Addresses;
import org.vietspider.nlp.impl.AreaNormalization;
import org.vietspider.nlp.impl.EmailExtractor;
import org.vietspider.nlp.impl.EmailFilter;
import org.vietspider.nlp.impl.PhoneExtractor;
import org.vietspider.nlp.impl.PhoneFilter;
import org.vietspider.nlp.impl.PriceExtractor;
import org.vietspider.nlp.impl.PriceFilter;
import org.vietspider.nlp.impl.PriceNormalization;
import org.vietspider.nlp.impl.ao.ActionExtractor;
import org.vietspider.nlp.impl.area.AreaExtractor;
import org.vietspider.nlp.impl.area.AreaFilter;
import org.vietspider.nlp.text.LineSplitter;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class NlpProcessor {

  private final static NlpProcessor PROCESSOR = new NlpProcessor();

  public final static NlpProcessor getProcessor() { return PROCESSOR; }

  public final static short DATA = 1;
  public final static short QUERY = 0;

  private INlpFilter [] filters;
  private INlpExtractor<?> [] extractors;
  private INlpNormalize [] normalizes;
  private LineSplitter lineSplitter = new LineSplitter();

  private NlpProcessor() {
    filters = new INlpFilter[] {
        new EmailFilter(), new PhoneFilter(), new AreaFilter(),
        new PriceFilter()/*, new ActionObjectFilter()*/
    };

    extractors = new INlpExtractor[] {
        new EmailExtractor(), new PhoneExtractor(), new AreaExtractor(),
        new PriceExtractor(), new ActionExtractor()
    };
    
    normalizes = new INlpNormalize[] {
        new AreaNormalization(), new AddressNormalization(), new PriceNormalization()
    };
  }

  public Map<Short, Collection<?>> process(String metaId, String text) {
    return process(metaId, text, DATA);
  }

  public Map<Short, Collection<?>> process(String metaId, final String text, short type) {
    HashMap<Short, Collection<?>> map = new HashMap<Short, Collection<?>>();
    if(text.trim().isEmpty()) return map;
    TextElement root = lineSplitter.split(text);
    TextElement element = root;
    while(element != null) {
      for(int i = 0; i < filters.length; i++) {
        filters[i].filter(metaId, element);
      }
      element = element.next();
    }

    element = root;
    while(element != null) {
      if(element.getLower().startsWith("tags") 
          || element.getLower().startsWith("tag")) {
        element = element.next();
        continue;
      }
      for(int i = 0; i < extractors.length; i++) {
        //        System.out.println(extractors[i].isExtract(element) + "=" + element.getValue()  );
        if(!extractors[i].isExtract(element)) continue;
        Collection<?> collection = map.get(extractors[i].type());
        if(collection == null) {
          collection = extractors[i].createCollection();
          map.put(extractors[i].type(), collection);
        }
        extractors[i].extract(metaId, collection, element);

        //        if(values == null) continue;
        //        List<String> list = map.get(extractors[i].type());
        //        if(list == null) {
        //          list = new ArrayList<String>();
        //          map.put(extractors[i].type(), list);
        //        }
        //        for(int z = 0; z < values.length; z++) {
        //          if(list.contains(values[z])) continue;
        //          list.add(values[z]);
        //        }
      }
      element = element.next();
    }

//    File file = UtilFile.getFile("/track/temp/", metaId + ".txt");
//    try {
//      org.vietspider.common.io.RWData.getInstance().save(file, text.getBytes(Application.CHARSET));
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(e);
//    }
    Addresses addresses = AddressDetector.getInstance().detectAddresses(root);
//    file.delete();
//    debug = true;
    if(addresses != null) {
      List<String> list = new ArrayList<String>();
      String [] values = addresses.toAddress(false, type == DATA);
      if(values.length > 0) {
        Collections.addAll(list, values);
        map.put(NLPData.ADDRESS, list);
      }

      values = addresses.toCities(type == DATA);
      list = new ArrayList<String>();
      if(values.length > 0) {
        Collections.addAll(list, values);
        map.put(NLPData.CITY, list);
      }
    }

    for(int i = 0; i < extractors.length; i++) {
      Collection<?> collection = map.get(extractors[i].type());
      if(collection == null) continue;
      extractors[i].closeCollection(collection);
    }
    
    for(int i = 0; i < normalizes.length; i++) {
      normalizes[i].normalize(metaId, map);
    }

    return map;

  }

}
