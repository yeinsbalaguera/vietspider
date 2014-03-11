/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.crawl.io.index.plugin;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 26, 2006
 */
public class TestSearchDocumentPlugin {

  public void test() throws Exception {
//    SearchDocumentPlugin search = new SearchDocumentPlugin();
//    File folder = UtilFile.getFolder("content/indexed/2006_8_25/");
//    
//    String [] indexedDir = new String[] {folder.getCanonicalPath()};
//    search.setIndexDirs(indexedDir);  
//    
//    SummarizeDocument summarize = createDocument();
//    System.out.println(summarize.getNouns().getWord().length);
//    StringBuilder builder = new StringBuilder();    
//    search.search(summarize, folder.getCanonicalPath(), builder);
//    System.out.println(builder);
  }
  
 /* private SummarizeDocument createDocument() throws Exception {
    String path = "web\\2006_8_25\\C-u00F4ng ngh-u1EC7\\Vnexpress\\1156500561281.htm";  
    if(path.startsWith("../../")) path = path.substring(6);
    File file  = UtilFile.getFile("content/", path);    
    DataReader buffer = RWData.getInstance();
    String text = new String(buffer.load(file), Application.CHARSET);
   
    HTMLDocument document = new HTMLParser2().createDocument(text);    
    HTMLNode node = document.getRoot();
//    ContentBuilder builder = new ContentBuilder();
    List<char[]> values = new ArrayList<char[]>();
//    builder.build(node, values);   
    return new DocumentFactory().create(path, values);
  }
  */
   public static void main(String[] args) throws Exception {
     TestSearchDocumentPlugin plugin = new TestSearchDocumentPlugin();
     plugin.test();
  }
}
