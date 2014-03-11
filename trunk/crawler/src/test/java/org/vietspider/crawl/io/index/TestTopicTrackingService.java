/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.index;



/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 18, 2009  
 */
public class TestTopicTrackingService {
  
 /* static {
    File file  = new File("D:\\Temp\\articles\\data");
    try {
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private static DataReader reader = RWData.getInstance();
  private static DocumentFactory documentFactory ;
  
  public static void index(File file) throws Exception  {
    File [] files = file.listFiles();
    for(int i = 0; i < files.length; i++) {
      TopicTracking topicTracking = createTopicTracking(files[i]);
      if(topicTracking == null) continue;
      TopicTrackingServiceBak.getInstance().put(topicTracking);
    }
//    System.out.println(" index okies");
  }
  
  private static TopicTracking createTopicTracking(File file) throws Exception {
//  System.out.println(" prepare index mining content "+ textContent.getContent().toString().hashCode());
    String text = new String(reader.load(file), "utf-8");
    String id = file.getName();
    if(id.indexOf('.') > -1) id = id.substring(0, id.indexOf('.'));
    
    //for mining indexing
    if(text.length() < 1)  return null;
    TopicTracking topicTracking = new TopicTracking(id, "ARTICLE", 10, 3);      

    SummarizeDocument summarize = documentFactory.create(topicTracking.getMetaId(), text);
    topicTracking.setSummarize(summarize);
    return topicTracking;
  }
  
  static void index() throws Exception {
    UtilFile.deleteFolder(new File("D:\\Temp\\articles\\data\\content\\"), false);
    documentFactory = new DocumentFactory();
//    TopicTrackingServiceBak.createInstance();
    index(new File("D:\\Temp\\articles\\text\\"));
  }
  
  static void searchIndex() throws Exception {
    documentFactory = new DocumentFactory();
//    TopicTrackingServiceBak.createInstance();

    File file = new File("D:\\Temp\\articles\\text\\200908181124442.txt");
    TopicTracking topic = createTopicTracking(file);
    
    String indexedDir = "D:\\Temp\\articles\\data\\content\\indexed\\ARTICLE\\2009_08_18\\";
    
//    List<Document> list = TopicTrackingService.getInstance().search(indexedDir, topic.getSummarize());
//    System.out.println("===           ============> " + list.size());
    
    TreeSet<SummarizeDocument> docs = 
      new TreeSet<SummarizeDocument>(new Comparator<SummarizeDocument>() {
        public int compare(SummarizeDocument sd1, SummarizeDocument sd2) {
          return sd1.getId().toString().compareTo(sd2.getId().toString());
        }
      });
    
    IndexerComputorBak computor = new IndexerComputorBak(documentFactory);
    
    computor.search(docs, indexedDir, topic.getSummarize());
    System.out.println(" =================  >"+ docs.size());
    Iterator<SummarizeDocument> iterator = docs.iterator();
    while(iterator.hasNext()) {
      System.out.println(iterator.next().getId());
    }
    
    System.exit(0);
  }
  
  static void searchMemory() throws Exception {
    documentFactory = new DocumentFactory();
    TopicTrackingServiceBak.createInstance();
    
    Queue<TopicTracking> queue = new ConcurrentLinkedQueue<TopicTracking>();
    
    File file = new File("D:\\Temp\\articles\\text\\");
    File [] files = file.listFiles();
    for(int i = 0; i < files.length; i++) {
      TopicTracking topicTracking = createTopicTracking(files[i]);
      if(topicTracking == null) continue;
      queue.add(topicTracking);
    }

    file = new File("D:\\Temp\\articles\\text\\200908181124442.txt");
    TopicTracking topic = createTopicTracking(file);
    
    TreeSet<SummarizeDocument> docs = 
      new TreeSet<SummarizeDocument>(new Comparator<SummarizeDocument>() {
        public int compare(SummarizeDocument sd1, SummarizeDocument sd2) {
          return sd1.getId().toString().compareTo(sd2.getId().toString());
        }
      });
    
    MemoryComputorBak computor = new MemoryComputorBak();
    computor.compute(docs, queue, topic);
    
    System.out.println(" =================  >"+ docs.size());
    Iterator<SummarizeDocument> iterator = docs.iterator();
    while(iterator.hasNext()) {
      System.out.println(iterator.next().getId());
    }
    
    System.exit(0);
  }
  
  public static void main(String[] args) throws Exception {
//    searchIndex();
//    searchMemory();
    index();
  }*/
  
}
