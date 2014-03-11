/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
public class TestSolr2 {

  public static void main(String[] args) throws Exception{
    System.setProperty("vietspider.data.path", "D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    File file  = UtilFile.getFolder("system/solr");  
    System.setProperty("solr.solr.home", file.getAbsolutePath());
    file  = UtilFile.getFolder("content/solr/data");  
    System.setProperty("solr.data.dir", file.getAbsolutePath());
    CoreContainer.Initializer initializer = new CoreContainer.Initializer();
    CoreContainer coreContainer = initializer.initialize();
    EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");

    server.deleteByQuery( "*:*" );
    
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

    SolrInputDocument doc1 = new SolrInputDocument();
    doc1.addField("id", "200912281451470010", 1.0f );
    doc1.addField("title", "Nghị lực của người thầy không chân", 1.0f );
    doc1.addField("desc","(Dân trí) - Một người thầy không thể đi lại được bằng đôi chân vẫn nhiệt tình dạy dỗ học sinh là một điển hình ở Trung Quốc.", 1.0f);
    doc1.addField("content", "<p align=\"justify\">Thời gian hoàn thiện Bảo tàng Hà Nội chỉ còn tính bằng ngày, đến nay vẫn còn rất nhiều phần việc phải hoàn tất. Ông Nguyễn Thế Thảo, Chủ tịch UBND thành phố Hà Nội, sau khi kiểm tra tiến độ công trình đã nhấn mạnh: Đây là công trình trọng điểm, có ý nghĩa lớn; chúng ta phải tập trung toàn tâm, toàn lực đẩy nhanh hơn nữa tiến độ của công trình. Bằng mọi giá, các đơn vị thi công phải hoàn thiện phần xây dựng và trưng bày đúng tiến độ để đúng ngày kỷ niệm Đại lễ 1.000 năm, Bảo tàng Hà Nội sẽ ra mắt công chúng.</p>");
    doc1.addField("price", 10);
    doc1.addField("time", System.currentTimeMillis());
    doc1.addField("text", "Dù mới ở tuổi thứ 8 nhưng con bò Charolais đã nặng tới 1,67 tấn. Cân nặng và chiều cao của nó vẫn tiếp tục phát triển. ", 1.0f);

    SolrInputDocument doc2 = new SolrInputDocument();
    doc2.addField("id", "200912281451420011", 1.0f );
    doc2.addField("title", "5 quyển sách kinh tế đáng đọc nhất 2009", 1.0f);
    doc2.addField("desc","(Dân trí) - Một người thầy không thể đi lại được bằng đôi chân vẫn nhiệt tình dạy dỗ học sinh là một điển hình ở Trung Quốc", 1.0f);
    doc2.addField("price", 20);
    doc2.addField("time", System.currentTimeMillis());
    doc2.addField("content", "<h4>5 quyển sách kinh tế đáng đọc nhất 2009</h4><p>Từ trùm lừa đảo Bernie Madoff đến nữ triết học Ayn Rand, từ lý thuyết kinh tế sai lầm đến sự sụp đổ của những đại gia Phố Wall ngạo mạn, đó là những câu chuyện thú vị nằm trong 5 cuốn sách kinh tế hay nhất do  Time đề cử.</p><div>");
    doc2.addField("text", "Tiêu thụ quá nhiều thực phẩm và đồ uống chứa a xít, chải răng quá mạnh và ngay cả một số loại thuốc, một số bệnh… đều khiến men răng bị bào mòn dần.", 1.0f);
    
    SolrInputDocument doc3 = new SolrInputDocument();
    doc3.addField("id", "200912281451470010", 1.0f );
    doc3.addField("title", "Nghị lực của người thầy không chân", 1.0f );
    doc3.addField("desc","(Dân trí) - Một người thầy không thể đi lại được bằng đôi chân vẫn nhiệt tình dạy dỗ học sinh là một điển hình ở Trung Quốc.", 1.0f);
    doc3.addField("content", "<p align=\"justify\">Thời gian hoàn thiện Bảo tàng Hà Nội chỉ còn tính bằng ngày, đến nay vẫn còn rất nhiều phần việc phải hoàn tất. Ông Nguyễn Thế Thảo, Chủ tịch UBND thành phố Hà Nội, sau khi kiểm tra tiến độ công trình đã nhấn mạnh: Đây là công trình trọng điểm, có ý nghĩa lớn; chúng ta phải tập trung toàn tâm, toàn lực đẩy nhanh hơn nữa tiến độ của công trình. Bằng mọi giá, các đơn vị thi công phải hoàn thiện phần xây dựng và trưng bày đúng tiến độ để đúng ngày kỷ niệm Đại lễ 1.000 năm, Bảo tàng Hà Nội sẽ ra mắt công chúng.</p>");
    doc3.addField("price", 10);
    doc3.addField("time", System.currentTimeMillis());
    doc3.addField("text", "Dù mới ở tuổi thứ 8 nhưng con bò Charolais đã nặng tới 1,67 tấn. Cân nặng và chiều cao của nó vẫn tiếp tục phát triển. ", 1.0f);

    docs.add(doc1);
    docs.add(doc2);
   

    try {
      server.add(docs);

      server.commit();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    try {
      server.add(doc3);
      server.commit();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    

    try {
      SolrQuery query = new  SolrQuery();
      //    query.setParam("id", "id2");
//      query.setQuery( "content:và thực" );
      query.setQuery("id:200912281451470010" );
      query.addSortField("price", SolrQuery.ORDER.asc );

      QueryResponse response = server.query(query);
      System.out.println(response.getResults().size());

      Iterator<SolrDocument> iter = response.getResults().iterator();

      while (iter.hasNext()) {
        SolrDocument resultDoc = iter.next();

        String content = (String) resultDoc.getFieldValue("content");
        long id = (Long)resultDoc.getFieldValue("id"); //id is the uniqueKey field
        System.out.println(id);

        //      if (response.getHighlighting().get(id) != null) {
        //        List<String> highlightSnippets = response.getHighlighting().get(id).get("content");
        //      }

        System.out.println("====>" + content);
      }
      
//      SolrQuery query2 = new  SolrQuery();
//      query2.setQuery("id:200912281451470010");
      server.deleteById("200912281451470010");
      server.commit();
      
      response = server.query(query);
      System.out.println(response.getResults().size());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }
}
