package org.vietspider.webui.search;

import java.io.OutputStream;

import org.vietspider.db.database.MetaList;
import org.vietspider.index.SearchQuery;
import org.vietspider.webui.search.page.FormChunk;
import org.vietspider.webui.search.page.PageChunk;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class PageRendererImpl {

  private LayoutRenderer layoutRenderer;
  
  private FormChunk pageForm = new FormChunk("page_form.xml", "form_action_data.txt");
  private FormChunk pageForm2 = new FormChunk("page_form2.xml", "form_action_data.txt");
  private PageChunk pageHeader = new PageChunk("page_header.xml");
  private PageChunk pageFooter = new PageChunk("page_footer.xml");
//  private PageChunk patternQuote = new PageChunk("pattern_quote.xml");
  
  public PageRendererImpl(boolean bot) {
    layoutRenderer = new LayoutRenderer(bot);
  }
  
  public void write(OutputStream output, MetaList list, SearchQuery query) throws Exception {
    String pattern = query != null ? query.getPattern() : "";
//    writeHeader(output, query);
    pageHeader.write(output, pattern);
    
    pageForm.write(output, query);

    if(query != null) list.setUrl(query.getPattern());
    layoutRenderer.write(output, list, query);

//    TopQueriesRenderer.getInstance().write(output);
//    if(pattern.length() < 20 && pattern.indexOf('\"') < 0) {
//      RefsEncoder encoder = new RefsEncoder();
//      StringBuilder builder = new StringBuilder();
//      builder.append(pattern).append('\"');
//      char[] chars = builder.toString().toCharArray();
//      patternQuote.write(output, new String(encoder.encode(chars)));
//    }
    
    pageForm2.write(output, query);
    pageFooter.write(output, pattern);
  }
}
