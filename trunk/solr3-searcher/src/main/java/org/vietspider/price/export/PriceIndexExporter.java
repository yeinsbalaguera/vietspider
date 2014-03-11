/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.export;

import java.io.File;
import java.util.Iterator;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.content.CommonDatabase;
import org.vietspider.locale.vn.VietnameseConverter;
import org.vietspider.price.index.PriceIndex;
import org.vietspider.price.index.PriceIndexDatabases;
import org.vietspider.price.index.PriceIndexMapper;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 22, 2011  
 */
public class PriceIndexExporter {
  
  public void exportToFile(String city, String action) throws Throwable {
    String name = VietnameseConverter.toAlias(city);
    name += "." + action.replace(',', '_') + ".csv";
    
    File file = UtilFile.getFile("content/solr2/price_index/", name);
    String text = export(city, action);
    RWData.getInstance().save(file, text.getBytes(Application.CHARSET));
  }
  
  public String export(String city, String action) throws Throwable {
    CommonDatabase database = PriceIndexDatabases.getInstance().getDatabase(city, action);
    Iterator<Long> iterator = database.getMap().keySet().iterator();
    PriceIndexMapper mapper = new PriceIndexMapper();
    
    AddressPrices aps = new AddressPrices(city, action);
    while(iterator.hasNext()) {
      byte [] bytes = database.load(iterator.next());
      String text = new String(bytes, Application.CHARSET);
      PriceIndex index = mapper.toIndex(text);
      aps.addAddressPrice(index);
    }
    
    StringBuilder builder = new StringBuilder();
    aps.build(builder);
    
    return builder.toString();
  }

}
