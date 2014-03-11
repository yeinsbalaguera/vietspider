package org.vietspider.webui.search;

import java.io.OutputStream;

import org.vietspider.index.SearchQuery;
import org.vietspider.webui.search.page.FormChunk;
import org.vietspider.webui.search.page.PageChunk;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class UserPostRendererImpl {

  private UserPostFormRenderer contentRenderer;
  
  private FormChunk pageForm = new FormChunk("page_form.xml", "form_action_data.txt");
  private FormChunk pageForm2 = new FormChunk("page_form2.xml", "form_action_data.txt");
  private PageChunk pageHeader = new PageChunk("page_header.xml");
  private PageChunk pageFooter = new PageChunk("page_footer.xml");
  
  public UserPostRendererImpl() {
    contentRenderer = new UserPostFormRenderer();
  }
  
  public void write(OutputStream output, String text, SearchQuery query) throws Exception {
    String pattern = query != null ? query.getPattern() : "";
    pageHeader.write(output, pattern);
    pageForm.write(output, query);

    contentRenderer.write(output, text, query);

    pageForm2.write(output, query);
    pageFooter.write(output, pattern);
  }
}
