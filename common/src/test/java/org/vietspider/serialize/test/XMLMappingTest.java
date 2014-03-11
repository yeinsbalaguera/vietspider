/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.vietspider.common.io.DataWriter;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
public class XMLMappingTest {
  
  static Article createBean() {
    Article article = new Article();
    article.setTitle("this is title");
    article.setContent("this is content");
    
    Meta meta = new Meta();
    ArrayList<String> list = new ArrayList<String>();
    list.add("thuan");
    meta.setItem(list);
    
    meta.putProperty("key1", "this is a");
    meta.putProperty("key2", "this is b");
    
    CDataAttrBean [] cbeans = {
        new CDataAttrBean((short)1, (short)21, 
            "<a class=\"comment-link\" href=\"http://nhuthuan\">aaa</a>"),
        new CDataAttrBean((short)8, (short)12,
            "<span width=\"18\" height=\"18\">test</span>")
    };
    meta.setCbeans(cbeans);
    
    article.setMeta(meta);
    
    PrimitiveData data = new PrimitiveData();
    data.setAge(10);
    data.setTime((long)1231231232);
    data.setTrust(true);
    data.setCharacter('H');
    
    CDataAttrBean bean = new CDataAttrBean((short)127, (short)17,
        "<p class=\"comment-link\">aaa</p>");
    data.getBeans().add(bean);
    bean = new CDataAttrBean((short)4, (short)40, "<div height=\"45\">test</div>");
    data.getBeans().add(bean);
    data.setPrices(new double[]{1.5, 100.7, 19.8});
    data.setCreate(Calendar.getInstance().getTime());
    article.setData(data);
    
    bean = new CDataAttrBean((short)45, (short)-45,
        "<td><b>Total Pages <sup><small>1</small></sup></b></td>");
    article.getMap().put("tag 1", bean);
    bean = new CDataAttrBean((short)105, (short)15, "<td>07&nbsp;May&nbsp;2011</td>");
    article.getMap().put("tag 2", bean);
    bean = new CDataAttrBean((short)10, (short)7, "<td><b>Tracker opened (closed)</b></td>");
    article.getMap().put("tag 3", bean);
    
    article.getProperties().put(new Integer(1), "hehe");
    article.getProperties().put("tag", new Double(34));
    
    article.getUmap().put("tag", bean);
    article.getUmap().put(new Integer(12), "hello");
    
    article.getLinked().add("hello");
    article.getLinked().add(new Long(23123));
    article.getLinked().add(bean);
    
    article.setRanges(new Object[]{
       new Integer(17), "test", new Character('r') 
    });
    
    article.getTree().add(new Float(23.3));
    article.getTree().add(new Float(13.7));
    
    article.getFolder().add("folder 1");
    article.getFolder().add("file2 1");
    
    return article;
  }

  @Test
  public void test() throws Exception {
    Article article = createBean();
    
    String xml = Object2XML.getInstance().toXMLDocument(article).getTextValue();
    
    File file = new File("D:\\java\\test\\article.xml");
    org.vietspider.common.io.RWData.getInstance().save(file, xml.getBytes("utf-8"));
//    System.out.println("====================================================");
//    System.out.println(xml);
//    System.out.println("====================================================");
    
    Article article2 = XML2Object.getInstance().toObject(Article.class, xml);
    
    Assert.assertEquals(article.getTitle(), article2.getTitle());
    Assert.assertEquals(article.getContent(), article2.getContent());
    
    Meta meta = article.getMeta();
    
    Meta meta2 = article2.getMeta();
    Assert.assertEquals(meta.getItem().size(), meta2.getItem().size());
    Assert.assertEquals(meta.getItem().get(0), meta2.getItem().get(0));
    
    Assert.assertEquals(meta.getProperties().size(), meta2.getProperties().size());
    
    Assert.assertEquals(meta.getPropertyValue("key1"), meta2.getPropertyValue("key1"));
    Assert.assertEquals(meta.getPropertyValue("key2"), meta2.getPropertyValue("key2"));
    
    PrimitiveData data = article.getData();
    PrimitiveData data2 = article2.getData();
//    System.out.println(data2);
    Assert.assertEquals(data.getAge(), data2.getAge());
    Assert.assertEquals(data.getCharacter(), data2.getCharacter());
    Assert.assertEquals(data.getTime(), data2.getTime());
    Assert.assertEquals(data.isTrust(), data2.isTrust());
    
    SimpleDateFormat dateFormat = XML2Object.getInstance().getDateFormat();
    Assert.assertEquals(dateFormat.format(data.getCreate()), dateFormat.format(data2.getCreate()));
    
    Assert.assertEquals(data.getPrices().length, data2.getPrices().length);
    Assert.assertEquals(data.getPrices()[2], data2.getPrices()[2]);
    
    CDataAttrBean [] cbeans = meta.getCbeans();
    CDataAttrBean [] cbeans2 = meta2.getCbeans();
    Assert.assertEquals(cbeans[0].getPage(), cbeans2[0].getPage());
    Assert.assertEquals(cbeans[1].getNumber(), cbeans2[1].getNumber());
    
    
    Assert.assertEquals(data.getBeans().size(), data2.getBeans().size());
    Assert.assertEquals(data.getBeans().get(0).getNumber(), data2.getBeans().get(0).getNumber());
    Assert.assertEquals(data.getBeans().get(1).getPage(), data2.getBeans().get(1).getPage());
    Assert.assertEquals(data.getBeans().get(1).getNumber2(), data2.getBeans().get(1).getNumber2());
    
    Map<String, CDataAttrBean> map1 = article.getMap();
    Map<String, CDataAttrBean> map2 = article2.getMap();
    Assert.assertEquals(map1.size(), map2.size());
    Assert.assertEquals(map1.get("tag 1").getNumber(), map2.get("tag 1").getNumber());
    Assert.assertEquals(map1.get("tag 3").getPage(), map2.get("tag 3").getPage());
    
    Properties properties1 = article.getProperties();
    Properties properties2 = article2.getProperties();
    
    
    Assert.assertEquals(properties1.keySet().iterator().next(), 
        properties2.keySet().iterator().next());
    Assert.assertEquals(properties1.size(), properties2.size());
    Assert.assertEquals(properties1.getProperty("tag"), properties2.getProperty("tag"));
    
    Map umap1 = article.getMap();
    Map umap2 = article2.getMap();
    Assert.assertEquals(umap1.size(), umap2.size());
    Assert.assertEquals(umap1.get("tag"), umap2.get("tag"));
    Assert.assertEquals(umap1.get(12), umap2.get(12));
    
    List linked1 = article.getLinked();
    List linked2 = article2.getLinked();
    Assert.assertEquals(linked1.size(), linked2.size());
//    System.out.println(linked1.get(1).getClass());
//    System.out.println(linked2.get(1).getClass());
    Assert.assertEquals(linked1.get(1), linked2.get(1));
    
    Object [] ranges1 = article.getRanges();
    Object [] ranges2 = article2.getRanges();
    Assert.assertEquals(ranges1.length, ranges2.length);
    Assert.assertEquals(ranges1[1], ranges2[1]);
    Assert.assertEquals(ranges1[2], ranges2[2]);
    
    Set tree1 = article.getTree();
    Set tree2 = article2.getTree();
    Assert.assertEquals(tree1.size(), tree2.size());
    Assert.assertEquals(tree1.iterator().next(), tree2.iterator().next());
    
    Set<String> folder1 = article.getFolder();
    Set<String> folder2 = article2.getFolder();
    Assert.assertEquals(folder1.size(), folder2.size());
    Assert.assertEquals(folder1.iterator().next(), folder2.iterator().next());
  }
}
