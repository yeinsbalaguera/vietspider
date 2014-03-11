/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.idm2.EIDFolder2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 27, 2008  
 */
public class EditContentHandler extends CMSHandler <String> {

  private final static String S_ID = "#VietSpider.Article.Id#";
  private final static String S_TITLE = "#VietSpider.Article.Title#";
  private final static String S_DESC = "#VietSpider.Article.Desc#";
  private final static String S_CONTENT = "#VietSpider.Article.Content#";

  private volatile static EditContentHandler handler;

  static {
    SystemProperties systemProperties = SystemProperties.getInstance();
    String application = "vietspider";
    try {
      application = systemProperties.getValue(Application.APPLICATION_NAME);
    } catch (Exception e) {
      application = "vietspider";
    }
    handler = new EditContentHandler(application);
  }

  public final static EditContentHandler getInstance() { return handler; }

  public EditContentHandler(String type) {
    super(type);
    name = "EDIT"; 
  }

  public boolean isEdit() {
    File file  = UtilFile.getFolder("system/cms/fckeditor.zip");
    if(file.exists()) return true;

    file  = new File(UtilFile.getFolder("system/cms/"), "fckeditor/");
    if(file.exists()) return true;

    return false;
  }

  @Override
  public String handle(HttpRequest request, HttpResponse response, HttpContext context, String... params) throws Exception {
    //    System.out.println(" da vao day roi ===  >"+ params[0]);
    try{
      if(params[0].equals("SAVE")) {

        BasicHttpEntityEnclosingRequest entityRequest = (BasicHttpEntityEnclosingRequest)request;
        ByteArrayOutputStream input = RWData.getInstance().loadInputStream(entityRequest.getEntity().getContent());
        String value = new String(input.toByteArray(), Application.CHARSET).trim();

        Map<String, String> dataValues = URLUtils.getParams(value);
        String metaId = dataValues.get("article_id");

        String title = dataValues.get("article_title");
        title  = title.replace('\'', '"');

        String desc  = dataValues.get("article_desc");
        desc = desc.replace('\'', '"');

        String newContent = dataValues.get("VSEditor");
        newContent  = newContent.replace('\'', '"');

        //      java.io.File cFile  = new java.io.File("F:\\Temp\\title_des\\a.txt");
        newContent = new String(new RefsDecoder().decode(newContent.toCharArray()));
        //      org.vietspider.common.io.DataWriter buffer = org.vietspider.common.io.RWData.getInstance();
        //      try {
        //        buffer.save(cFile, newContent.getBytes("utf-8"));
        //      } catch (Exception e) {
        //        e.printStackTrace();
        //      }

        DatabaseService.getSaver().save(metaId, title, desc, newContent);

        reindex(metaId);

        Article article = loadDatabase(metaId);
        //        System.out.println(" chuan bi editor "+ metaId);
        EIDFolder2.write(article.getDomain(), metaId, Article.EDITED);

        //        IDTracker.getInstance().update(metaId, Article.EDITED);

        //        String userName = 
        //        Header userHeader = request.getFirstHeader("username");
        //        Header fileHeader = request.getFirstHeader("file");
        //        if(userHeader == null  || fileHeader == null) throw new InvalidActionHandler();
        //
        //        String message = new NameConverter().decode(fileHeader.getValue());
        //        if(userHeader != null) {
        //        ActionUserLogService.getLog(metaId, userHeader.geth )
        //        }
        //        UserActionTracker.getInstance().append(new ActionUser("", ActionUser.EDIT, metaId));

        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<meta http-equiv=\"Refresh\" content=\"2;URL=")
        .append("../DETAIL/").append(metaId).append("/-4").append("\">");
        builder.append("</head>");
        //        builder.append("<body  onload=\"javascript:window.history.go(-2);\">");
        //        builder.append("<script language=javascript>setTimeout('window.location.reload(window.history.go(-2))', 2000);</script>");
        //        builder.append("<body onload=\"javascript:window.location.reload(history.go(-2));\">");
        builder.append("<body>");
        builder.append("Save Successfull!");
        builder.append("</body>");
        builder.append("</html>");
        setOutputData(request, response, "text/html", builder.toString().getBytes());
        return "text/html";
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    StringBuilder path = new StringBuilder();
    for(String param : params) {
      if(path.length() > 0) path.append('/');
      path.append(param);
    }

    return write(request, response, context, path.toString());
  }

  private void reindex(String metaId) {
    String startIndexValue = SystemProperties.getInstance().getValue(Application.START_INDEX_DATA_SERVICE);
    if(startIndexValue == null || startIndexValue.trim().isEmpty()) return;
    startIndexValue = startIndexValue != null ? startIndexValue.trim() : null;
    if(!"true".equals(startIndexValue) || Application.LICENSE == Install.PERSONAL) return;

    //    if(DbIndexerService.getInstance() == null) return;
    //    DbIndexerService.getInstance().deleleIndexedById(metaId);

    Article article = null;
    try {
      article = loadDatabase(metaId);      
    } catch (Exception e) {
    }
    if(article == null) return;

//    ContentIndex entry = new ContentIndex();
//    Meta meta = article.getMeta();
//    entry.setId(meta.getId());
//    entry.setTitle(meta.getTitle());
//    entry.setDescription(meta.getDesc());
//    if(article.getContent() != null) {
//      entry.setContent(article.getContent().getContent());
//    }
//
//    Calendar calendar = Calendar.getInstance();
//    entry.setDate(CalendarUtils.getDateFormat().format(calendar.getTime()));
//
//    ContentIndexers.getInstance().index(entry);

    //    DbIndexerService dbIndex = DbIndexerService.getInstance();
    //    if(dbIndex != null) dbIndex.getDbIndexEntry().save(entry);
  }

  private byte[] loadFile(File folder, String fileName) throws Exception {
    File file  = new File(folder, fileName);
    if(file.exists()) return RWData.getInstance().load(file); 
    return "No file".getBytes();
  }

  private byte[] loadZipEntry(File file, String fileName) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ZipInputStream zipInput = null;
    try {
      zipInput = new ZipInputStream(new FileInputStream(file));
      int read = -1;
      byte [] bytes = new byte[4*1024];  

      ZipEntry entry = null;
      while(true) {
        entry = zipInput.getNextEntry();
        if(entry == null) break;
        if(entry.getName().equalsIgnoreCase(fileName)) {
          while ((read = zipInput.read(bytes, 0, bytes.length)) != -1) {        
            outputStream.write(bytes, 0, read);
          }  
          break;
        }
      }
      zipInput.close();
    } finally {
      try {
        if(zipInput != null) zipInput.close();
      } catch (Exception e) {
      }
    }
    return outputStream.toByteArray();
  }


  @Override
  @SuppressWarnings("unused")
  public String render(OutputStream output, String t, String[] cookies, String... params) throws Exception {
    String contentType = "text/html";
    File file  = new File(UtilFile.getFolder("system/cms/"), "fckeditor/");
    boolean isFile = t.indexOf('.') > -1;
    if(t.indexOf('?') > -1) {
      t = t.substring(0, t.indexOf('?'));
    }

    if(file.exists()) {
      if(isFile) {
        if(t.endsWith(".css")) contentType = "text/css";
        output.write(loadFile(file, t));
      } else {
        writeFormData(output, loadFile(file, "editor.html"), t);
      }
    } else {
      file  = new File(UtilFile.getFolder("system/cms/"), "fckeditor.zip");
      if(file.exists()) {
        if(isFile) {
          if(t.endsWith(".css")) contentType = "text/css";
          output.write(loadZipEntry(file,  t));
        } else {
          writeFormData(output, loadZipEntry(file, "editor.html"), t);
        }
      }
    }
    return contentType;
  }

  private void writeFormData(OutputStream output, byte [] bytes, String metaId) throws Exception {
    Article article = null;
    try {
      article = loadDatabase(metaId);      
    } catch (Exception e) {
      throw e;
    }

    if(article == null) {
      output.write(getRedirect());
      LogService.getInstance().setThrowable(new Exception("Id: " + metaId));
      return;
    }

    String html  = new String(bytes, "utf-8");
    RefsEncoder encoder = new RefsEncoder();
    Meta meta = article.getMeta();
    Content content = article.getContent();

    String title  = meta.getTitle();
    char [] chars = encoder.encode(title.toCharArray());
    title = new String(chars) ; 

    html = html.replaceFirst(S_TITLE, title);
    html = html.replaceFirst(S_DESC, meta.getDesc());
    html = html.replaceFirst(S_ID, meta.getId());
    String contentValue = content.getContent();
    StringBuilder builder = new StringBuilder();
    encodeText(builder, contentValue);
    contentValue = builder.toString();
    int idx = html.indexOf(S_CONTENT);
    if(idx > 0) {
      html = html.substring(0, idx) + contentValue + html.substring(idx+S_CONTENT.length());
    }
    output.write(html.getBytes("utf-8"));
  }

  private void encodeText(StringBuilder builder, String value) {
    int index = 0;
    String toLower = value.toLowerCase();
    int start = toLower.indexOf("<iframe");
    int end = toLower.indexOf("</iframe>");
    if(start > -1 && end > -1) {
//    System.out.println(start + " : "+ end);
      value  = value.substring(0, start) + value.substring(end + 9); 
    }
    while(index < value.length()) {
      char c = value.charAt(index);
      if(c == '\n') {
        builder.append("\'+\n\'");
      } else if(c == '\r') {
        builder.append("\'+\r\'");
      } else {
        builder.append(c == '\'' ? '\"' : c);
      }

      index++;
    }
  }

  private Article loadDatabase(String metaId) {
    Article article = null;
    try {
      article = DatabaseService.getLoader().loadArticle(metaId);
    } catch (SQLException e) {
      return null;
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
      return null;
    }
    if(article == null) return null;

    if(article.getMeta() == null) return  null;

    return article;
  }

  private byte[] getRedirect() {
    StringBuilder builder = new StringBuilder();
    builder.append("<html>");
    builder.append("<head>");
    builder.append("<meta http-equiv=\"Refresh\" content=\"2;URL=/\">");
    builder.append("</head>");
    builder.append("<body>Invalid access or not found data </body>");
    builder.append("</html>");

    return builder.toString().getBytes();
  }

}
