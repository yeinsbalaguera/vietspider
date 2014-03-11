/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.content.db.tp.TpComputor.Documents;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.content.tp.vn.comparator.TpDocumentMatcher;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 19, 2011  
 */
public class TestTpIndex {
  
  private static File folder;
  private static File folder2;
  private static PluginData2TpDocument pluginData2TpDocument;
  private static TpDocumentMatcher matcher;
  
  private static TpWorkingData loadTpData(String name) throws Exception {
    File file  = new File(folder, name);
    if(!file.exists()) file  = new File(folder, name + ".txt");
    if(!file.exists()) {
      file = new File(folder2, name);
     org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
      writer.copy(file, new File(folder, name));
    }
    String value  = new String(RWData.getInstance().load(file), Application.CHARSET);
//    System.out.println(value.length());
    value = value.trim();
    TpWorkingData data = pluginData2TpDocument.convert(null, file.getName(), value);
    int idx = value.indexOf('\n');
    if(idx > 0) data.setTitle(value.substring(0, idx));
    return data;
  }
  
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\test\\vsnews\\data\\");

    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    pluginData2TpDocument = new PluginData2TpDocument();
    matcher = new TpDocumentMatcher();
    
    folder = new File("D:\\Temp\\tp\\compute\\");
    folder2 = new File("D:\\Temp\\tp\\collection\\");
    
    File confgFile  = UtilFile.getFolder("system/solr2");  
    System.setProperty("solr.solr.home", confgFile.getAbsolutePath());
    CoreContainer.Initializer initializer = new CoreContainer.Initializer();
    CoreContainer solrCoreContainer = initializer.initialize();
    EmbeddedSolrServer solrServer = new EmbeddedSolrServer(solrCoreContainer, "ctpt");
    TpIndexingReader indexingReader = new TpIndexingReader(solrServer/*, tpDatabases*/);
    
    indexingReader.setWordSplitter(pluginData2TpDocument.getWordSplitter());
    indexingReader.setWordStore(pluginData2TpDocument.getWordStore());
    
    TpWorkingData tpData = loadTpData("12a");
    SolrQuery query = indexingReader.createQuery(tpData);
    if(query == null) return;
    Documents documents = new Documents(tpData.getId(), query);
    indexingReader.search(documents);
    List<TpWorkingData> list = documents.list;
    System.out.println(" total "+ list.size());
    for(int i = 0; i < list.size(); i++) {
      TpWorkingData data = list.get(i); 
      System.out.println(data.getTitle() + " : " + data.getId() 
          + " : " + data.getScore() + " : "+ matcher.compare(tpData, data));
    }
    
    System.exit(0);
    
  }
}
