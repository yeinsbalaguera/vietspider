/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.content.tp.vn.comparator.TpDocumentMatcher;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class TestTpComputorCollection extends TestCase {

  private File folder;
  private PluginData2TpDocument pluginData2TpDocument;
  private TpDocumentMatcher matcher;

  @Override
  protected void setUp() throws Exception {
    File file  = new File("D:\\java\\test\\vsnews\\data\\");

    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");

    pluginData2TpDocument = new PluginData2TpDocument();
    matcher = new TpDocumentMatcher();

    folder = new File("D:\\Temp\\tp\\collection\\");
  }

  private TpWorkingData toTpData(File file) throws Exception {
    String value  = new String(RWData.getInstance().load(file), Application.CHARSET);
    value = value.trim();
    int idx = value.indexOf('\n');
    if(idx < 1) return null;

    String title = value.substring(0, idx);
    try {
      TpWorkingData tpData = pluginData2TpDocument.convert(null, file.getName(), value);
      tpData.setTitle(title);
      return tpData;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("=========================");
      System.out.println(value);
      System.exit(0);
      return null;
    }
  }

  //  private boolean compare(String name1, String name2, int rate) throws Exception {
  //    TpWorkingData data1 = toTpData(name1);
  //    TpWorkingData data2 = toTpData(name2);
  //    int _return = matcher.compare(data1, data2);
  //    if(matcher.isTest()) System.out.println("===========  > "+ _return + "%");
  //    return _return >= rate;
  //  }

  @Test
  public void test() throws Exception {
    File [] files = folder.listFiles();
    List<TpWorkingData> list = new ArrayList<TpWorkingData>();
    for(int i = 0; i < files.length; i++) {
      TpWorkingData data = toTpData(files[i]);
      if(data == null) continue;
      list.add(data);
    }


    for(int i = 0; i < list.size(); i++) {
      List<TpWorkingData> relations = new ArrayList<TpWorkingData>();
      List<Integer> ratios = new ArrayList<Integer>();
      for(int j = i+1; j < list.size(); j++) {
        int _return = matcher.compare(list.get(i), list.get(j));
        if(_return >= 10) {
          relations.add(list.get(j));
          ratios.add(_return);
        }
      }
      if(relations.size() < 1) continue;
      System.out.println("================> "+ list.get(i).getId() + " : " + list.get(i).getTitle());
      for(int k = 0; k < relations.size(); k++) {
      System.out.println("---> " + list.get(i).getId() + "," + relations.get(k).getId() 
          + " : " + ratios.get(k) + " : " + relations.get(k).getTitle());
      }
    }
  }


}
