package org.vietspider.webui.cms.vietspider;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.vietspider.article.index.ArticleIndexStorage;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleIndex;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.webapp.Translator2;
import org.vietspider.webui.cms.BufferWriter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
public class TranslatorRendererImpl extends BufferWriter {

  private Translator2 translator;
  private boolean renderDesc = false;
  
  private Map<String, QueueElement > queueData = new HashMap<String, QueueElement >();

  public TranslatorRendererImpl() {
    translator = new Translator2();
    SystemProperties system = SystemProperties.getInstance();
    renderDesc = "true".equals(system.getValue("desc.extractor.remove"));
  }

  public String write(OutputStream output_, Article article,
      final String from, final String to, String uri) throws Exception {
    this.output = output_;
    
    StringBuilder builder =  new StringBuilder(article.getId());
    builder.append('_').append(from).append('_').append(to);

    String queueId = builder.toString();

    Content content = article.getContent();
    final Meta meta = article.getMeta();
    String contentValue = "";
    if(content != null && content.getContent() != null) {
      contentValue += content.getContent();
    }
    
    final String translateContent = contentValue;
    
    QueueElement element = queueData.get(queueId);
    if(element == null) {
      element = new QueueElement();
      queueData.put(queueId, element);
      new Thread() {
        public void run() {
          QueueElement _element = new QueueElement();
          _element.index = new StringBuilder();
          _element.translated = translator.translate(
              meta.getTitle(), meta.getDesc(), translateContent, from, to, _element.index);
          StringBuilder _builder =  new StringBuilder(meta.getId());
          _builder.append('_').append(from).append('_').append(to);
          queueData.put(_builder.toString(), _element);
        }
      }.start();
    }
    
    if(element.isEmpty()) {
      renderWaiting(uri, meta.getId());
      return "text/html";
    }
    
    try {
      String html = CommonResources.INSTANCE.getDetailTemplate();

      html = html.replaceFirst("width=\"761\"", ""); 
      html = html.replaceFirst("class=\"tintop_line\"", "");

      String pattern = "$header";
      int idx = html.indexOf(pattern);
      int start = 0;
      if(idx > -1){
        append(html.substring(start, idx));
        new HeaderRenderer().write(output, article.getMeta().getTitle());    
        start = idx + pattern.length();
      }

      pattern = "$application";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        start = idx + pattern.length();
      }

      pattern = "$menu";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        start = idx + pattern.length();
      }      

      pattern = "$breadcumbs";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        start = idx + pattern.length();
      }   

      pattern = "$content";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        // index translated content
        if(element.index.length() > 0) {
          ArticleIndex articleIndex = ArticleIndexStorage.getInstance().loadArticleIndex(meta.getId());
          if(articleIndex != null) {
//            System.out.println(" ==============  > "+ indexBuilder );
            articleIndex.setTextNoMark(element.index.toString());
            ArticleIndexStorage.getInstance().delete(meta.getId());
            ArticleIndexStorage.getInstance().add(articleIndex);
          }
        }
        // render the content
        if(element.translated != null) {
          append("<h4>"); append(element.translated[0]); append("</h4>"); 
          if(renderDesc) {
            append("<p>"); append(element.translated[1]); append("</p>");
          }
          append(element.translated[2]);
        } else {
          append("<h4>"); append(meta.getTitle()); append("</h4>"); 
          if(renderDesc) {
            append("<p>"); append(meta.getDesc()); append("</p>");
          }
          append(contentValue);
        }
        start = idx + pattern.length();
      } 

      pattern = "$right";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        start = idx + pattern.length();
      }

      pattern = "$copyright";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        start = idx + pattern.length();
      }

      if(start < html.length()) append(html.substring(start));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return "text/html";
  }

  void createImage(String html, String img) throws Exception {
    String pattern = "$image";
    int idx = html.indexOf(pattern);
    int start = 0;
    if(idx > -1){
      append(html.substring(start, idx));append(img);
      start = idx + pattern.length();
    }
    if(start < html.length()) append(html.substring(start));
  }
  
  private class QueueElement {
    
    private StringBuilder index;
    private String[] translated;
    
    private QueueElement() {
      
    }
    
    private QueueElement(StringBuilder index, String[] translated) {
      this.index = index;
      this.translated = translated;
    }
    
    private boolean isEmpty() { return translated == null; }
  }
  
  private void renderWaiting(String uri, String id) {
    StringBuilder builder = new StringBuilder();
    
    builder.append("<HTML><title>Tình trạng dịch bài</title><HEAD>");
    builder.append("<META http-equiv=Content-Type content=\"text/html; charset=utf-8\">");
    builder.append("<meta http-equiv=\"Refresh\" content=\"5;URL=").append(uri).append("\">");
    builder.append("<BODY>");
    builder.append("<p><a href=\"").append(uri).append("\">Đang dịch bài, xin hãy đợi trong giây lát...</a></p>");
    builder.append("<p><a href=\"/vietspider/DETAIL/").append(id).append("\">Trở lại nội dung đầu</a></p>");
    builder.append("</BODY></HTML>");
    
    try {
      output.write(builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

 

}
