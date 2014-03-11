/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.text;

import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestSourceMapping {
  public static void main(String[] args) throws Exception {
    Source source = new Source();
    source.setCategory("Tin tuc");
    source.setName("vietnamnet");
    source.setGroup("ARTICLE");
    source.setDepth(2);
    source.setEncoding("utf-8");
    source.setHome(new String[]{"http//vietnamnet.vn"});
    
    Region Region = new Region();
    Region.setName("update");
    Region.setPaths(new String[]{"TABLE[0].TR[1].TD[2]"});
    source.setUpdateRegion(Region);

    source.setPattern("http://vietnamnet.vn/vanhoa/abd/23223");
    
    Region Region1 = new Region();
    Region1.setName("extract");
    Region1.setPaths(new String[]{"DIV[5].TABLE[0].TR[1].TD[2]"});
    
    Region Region2 = new Region();
    Region2.setName("remove");
    Region2.setPaths(new String[]{"DIV[4].TABLE[2].TR[9].TD[1].P[0].FONT[6]"});
    
    source.setExtractRegion(new Region[]{Region1, Region2});
    XMLDocument document = Object2XML.getInstance().toXMLDocument(source);
//    System.out.println(document.getTextValue());
    Source source2 = XML2Object.getInstance().toObject(Source.class, document);
    System.out.println(source2.getUpdateRegion().getPaths()[0]);
    
  }
}
