package org.vietspider.index;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

public class TestRealtime /*extends LuceneTestCase*/ {
  public void testAddDocuments() throws Exception {
    //    ConcurrentMergeScheduler ramMergeScheduler = new ConcurrentMergeScheduler();
    //    Analyzer analyzer = new StandardAnalyzer();
    //    ramMergeScheduler.setMaxThreadCount(2);
    //    Directory dir = new MockRAMDirectory();
    //    IndexWriter diskWriter = new IndexWriter(dir, new StandardAnalyzer(),
    //        MaxFieldLength.UNLIMITED);
    //    RealtimeIndex rt = new RealtimeIndex(diskWriter, 2*1024, ramMergeScheduler);
    //    int num = 50;
    //    for (int x = 0; x < num; x++) {
    //      Document d = createDocument(x, "rt1", 5);
    //      rt.addDocument(d, analyzer);
    //      if (x % 10 == 0) rt.flush(true, true);
    //    }
    //    IndexReader reader = rt.getReader();
    //    assertEquals(reader.maxDoc(), num);
    //    reader.close();
    //    rt.close();
    //    diskWriter.close();
    //    dir.close();
  }

  public void testUpdateDocuments() throws Exception {
    //    ConcurrentMergeScheduler ramMergeScheduler = new ConcurrentMergeScheduler();
    //    Analyzer analyzer = new StandardAnalyzer();
    //    ramMergeScheduler.setMaxThreadCount(2);
    //    Directory dir = new MockRAMDirectory();
    //    IndexWriter diskWriter = new IndexWriter(dir, new StandardAnalyzer(),
    //        MaxFieldLength.UNLIMITED);
    //    RealtimeIndex rt = new RealtimeIndex(diskWriter, 2*1024, ramMergeScheduler);
    //    int num = 50;
    //    for (int x = 0; x < num; x++) {
    //      Document d = createDocument(x, "rt1", 5);
    //      rt.addDocument(d, analyzer);
    //    }
    //    IndexReader r = rt.getReader();
    //    for (int x=0; x < r.maxDoc(); x++) {
    //      Document doc = r.document(x);
    //      Field field = doc.getField("id");
    //      int id = Integer.parseInt(field.stringValue());
    //      int newId = id+100;
    //      Term term = new Term(field.name(), field.stringValue());
    //      Document upDoc = createDocument(newId, "v2", 4);
    //      rt.updateDocument(term, upDoc, analyzer);
    //    }
    //    IndexReader r2 = rt.getReader();
    //    assertEquals(r.numDocs(), count(new Term("indexname", "v2"), r2));
    //    assertEquals(r.numDocs(), r2.numDocs());
    //    diskWriter.close();
    //    dir.close();
  }

  private static int count(Term term, IndexReader r) throws IOException {
    int count = 0;
    TermDocs td = r.termDocs(term);
    while (td.next()) {
      count++;
    }
    return count;
  }

  public static Document createDocument(int n, String indexName, int numFields) {
    StringBuffer sb = new StringBuffer();
    Document doc = new Document();
    doc.add(new Field("id", Integer.toString(n), Store.YES, Index.NOT_ANALYZED));
    doc.add(new Field("indexname", indexName, Store.YES, Index.NOT_ANALYZED));
    
    sb.append("a");
    sb.append(n);
    doc.add(new Field("field1", sb.toString(), Store.YES, Index.ANALYZED));
    sb.append(" b");
    sb.append(n);
    for (int i = 1; i < numFields; i++) {
      doc.add(new Field("field" + (i + 1), sb.toString(), Store.YES, Index.ANALYZED));
    }
    return doc;
  }

  public static void  main(String[] args) throws Exception {
//    ConcurrentMergeScheduler ramMergeScheduler = new ConcurrentMergeScheduler();
//    Analyzer analyzer = new StandardAnalyzer();
//    ramMergeScheduler.setMaxThreadCount(2);
//    File file  = new File("D:\\Temp\\tmp\\index\\");
//    Directory dir  = FSDirectory.getDirectory(file.getAbsolutePath());
//    IndexWriter diskWriter = new IndexWriter(dir, new WhitespaceAnalyzer(), MaxFieldLength.UNLIMITED);
//    RealtimeIndex rt = new RealtimeIndex(diskWriter, 5*1024, ramMergeScheduler);
//    int num = 50;
////    for (int x = 0; x < num; x++) {
////      Document d = createDocument(x, "rt1", 5);
////      rt.addDocument(d, analyzer);
////      if (x % 10 == 0) rt.flush(true, true);
////    }
//    IndexReader reader = rt.getReader();
//    System.out.println(reader.maxDoc());
//    
//    rt.deleteDocuments(new Term[]{new Term("id", Integer.toString(10))});
//    rt.deleteDocuments(new Term[]{new Term("id", Integer.toString(2))});
//    rt.deleteDocuments(new Term[]{new Term("id", Integer.toString(132))});
//    rt.deleteDocuments(new Term[]{new Term("id", Integer.toString(1))});
//    
//    reader.deleteDocuments(new Term("id", Integer.toString(10)));
//    reader.reopen();
//    System.out.println(reader.hasDeletions());
//    rt.flush(true, true);
//    
//    System.out.println(reader.maxDoc());
//    
////    reader.close();
//    rt.close();
//    System.exit(0);
//    diskWriter.close();
//    dir.close();
  }
}
