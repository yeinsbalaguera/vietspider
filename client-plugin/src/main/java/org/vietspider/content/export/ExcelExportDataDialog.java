/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.export;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.bean.xml.XArticle;
import org.vietspider.bean.xml.XArticles;
import org.vietspider.chars.TextSpliter;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.io.RWData;
import org.vietspider.export.ExportDataDialog;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.net.server.URLPath;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public class ExcelExportDataDialog extends ExportDataDialog {
  

  public ExcelExportDataDialog(Shell parent, String value_) {
    super(parent, value_, ".xls", "Excel 2003 Plugin");
  }
  
  public void exportMore(File file, String data) {
    String [] elements = data.split("\n");
    WritableWorkbook workbook = null;

    try {
      WorkbookSettings ws = new WorkbookSettings();
      ws.setLocale(new Locale("en", "EN"));
      workbook = Workbook.createWorkbook(file, ws);
      int sheetIndex = 0;
      TextSpliter spliter = new TextSpliter();
      for(int i = 0; i < elements.length-1; i+= 2) {
        export(workbook, sheetIndex, elements[i], spliter.toArray(elements[i+1], ','));
        sheetIndex++;
      }
      workbook.write();
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    } finally {
      try {

        if(workbook != null) workbook.close();
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
    }
  }

  public void export(String domainId, File file, String[] dataHeaders) {
    if(stop) return;
    WritableWorkbook workbook = null;

    try {
      WorkbookSettings ws = new WorkbookSettings();
      ws.setLocale(new Locale("en", "EN"));
      workbook = Workbook.createWorkbook(file, ws);
      
      export(workbook, 0, domainId, dataHeaders);
      
      workbook.write();
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    } finally {
      try {

        if(workbook != null) workbook.close();
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
    }
  }

  public void export(WritableWorkbook workbook, 
      int sheetIndex, String domainId, String[] dataHeaders) {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    HttpData httpData = null;
    try {
      PluginClientHandler handler = new PluginClientHandler();

      //      String xml = handler.send("export.office.data.plugin", "load.domain", domainId);
      //      Domain dm = XML2Object.getInstance().toObject(Domain.class, xml);
      String sheetName = domainId;
      sheetName = sheetName.replace('.', '-');
      sheetName = sheetName.replace('/', '.');

      WritableSheet sheet = workbook.createSheet(sheetName, sheetIndex);
//      System.out.println(sheetName +  " : "+ sheetIndex);

      String pageValue = handler.send(
          "export.office.data.plugin", "compute.export.to.excel", domainId);
      int totalPage  = -1;
      try {
        totalPage = Integer.parseInt(pageValue);
      } catch (Exception e) {
        return;
      }

      if(totalPage < 1) return;
      int page = 1;

      ExcelModel model = new ExcelModel();
      model.setDomainId(domainId);
      model.setHeaders(dataHeaders);

      File licenseFile = LicenseVerifier.loadLicenseFile();
      boolean license = LicenseVerifier.verify("export", licenseFile);
      if(!license && totalPage > 10) totalPage = 10;

      for(int i = 0; i < dataHeaders.length; i++) {
        WritableFont arial12pt = new WritableFont(WritableFont.ARIAL, 12);
        arial12pt.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat arial12format = new WritableCellFormat(arial12pt);
        arial12format.setWrap(false);

        Label lr = new Label(i, 0, dataHeaders[i], arial12format);
        sheet.addCell(lr);
      }

      int row = 1;

      while(page <= totalPage) {
        Header [] headers = new Header[] {
            new BasicHeader("action", "export.to.excel"),
            new BasicHeader("plugin.name", "export.office.data.plugin")
        };
        model.setPage(page);
        String xml = Object2XML.getInstance().toXMLDocument(model).getTextValue();
        byte [] bytes = xml.getBytes(Application.CHARSET);

        httpData = connector.loadResponse(URLPath.DATA_PLUGIN_HANDLER, bytes, headers);
        InputStream inputStream = httpData.getStream();

        try {
          bytes = RWData.getInstance().loadInputStream(inputStream).toByteArray();
          
//          System.out.println(" new string "+ new String(bytes));

          XArticles collection = XML2Object.getInstance().toObject(XArticles.class, bytes);
          List<XArticle> articles = collection.get();
          for(int i = 0; i < articles.size(); i++) {
            toXArticle(sheet, row, dataHeaders, articles.get(i));
            row++;
          }

          //          System.out.println(new String(bytes, Application.CHARSET));
          //          writer.save(file, inputStream, true);
          inputStream.close();
        } finally {
          connector.release(httpData);
          inputStream.close();
        }
        page++;
      }

    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    } finally {
      connector.release(httpData);
    }
  }

  private void toXArticle (WritableSheet sheet, int row, String[] fields, XArticle article) {
    //    StringBuilder builder = new StringBuilder("<Row ss:Height=\"12.8376\">");
    WritableFont arial12pt = new WritableFont(WritableFont.ARIAL, 12);
    try {
      if(article.getContent() == null) return;
      
//      System.out.println("=============================================================");
      String tcont = article.getContent();
//      System.out.println(" truoc " + tcont);
      RefsDecoder decoder = new RefsDecoder();
      char [] chars = decoder.decode(tcont.toCharArray());
      tcont = new String(chars);
//      artic
//      article.getContent().setContent(tcont);
      
//      System.out.println("---------------------------------------------------------");
//      System.out.println(tcont);
      
      XMLDocument document  = XMLParser.createDocument(tcont, null);
      List<XMLNode> list = document.getRoot().getChild(0).getChildren();

      WritableCellFormat arial12format = new WritableCellFormat(arial12pt);
      arial12format.setWrap(false);
      
      for(int i = 0 ; i < fields.length; i++) {
        Label lr = new Label(i, row, searchContent(list, fields[i]), arial12format);
        sheet.addCell(lr);
      }

     /* for(int i = 0 ; i < list.size(); i++) {
        XMLNode node = list.get(i);
//        if("src".equals(node.getName())) continue;
        if(!isExportField(fields, node.getName())) continue;
        //        builder.append("<Cell><Data ss:Type=\"String\">");
        if(node.getChildren() ==  null 
            || node.getChildren().size() < 1) {
          //          builder.append("");
        } else {
        Label lr = new Label(i, row, getValue(node), arial12format);
        sheet.addCell(lr);
          //          System.out.println(getValue(node));
          //          builder.append(getValue(node));
//        }
        //        builder.append("</Data></Cell>");
      }*/
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
      //      return "";
    }
  }

  private boolean isExportField(String[] headers, String field) {
    for(int i = 0; i < headers.length; i++) {
      if(headers[i].equals(field)) return true;
    }
    return false;
  }

  /*private String getValue(XMLNode node) {
    List<XMLNode> children  = node.getChildren();
    if(children == null) return "";

    if(node.getChildren().size() == 3) {
      for(int i = 0; i < children.size(); i++) {
        if(children.get(i).getName().equals("name")) {
          node = children.get(i);
          break;
        }
      }
    }

    if(node.getChildren() != null && node.getChildren().size() > 0) {
      return node.getChild(0).getTextValue();
    }


    return "";
  }
*/
  
  private String searchContent(List<XMLNode> nodes, String header) {
    List<XMLNode> list = new ArrayList<XMLNode>();
    for(int i = 0; i < nodes.size(); i++) {
      XMLNode node = nodes.get(i);
      if(node.getName().equals(header)) list.add(node);
    }        
    if(list.size() == 0) return "";
    if(list.size() == 1) {
      return getValue(list.get(0));
    }
    
    StringBuilder builder = new StringBuilder("{");
    for(int i = 0; i < list.size(); i++) {
      XMLNode node = list.get(i);
      String text = getValue(node);
      if(builder.length() > 1) {
        builder.append("} {");
      }
      builder.append(text);
    }
    builder.append('}');
    return builder.toString();
  }
  
  private String getValue(XMLNode node) {
    List<XMLNode> children  = node.getChildren();
    if(children == null || node.getChildren().size() < 1) return "";

//    System.out.println(node.getName() + " : "+ node.getChildren().size());
    if(node.getChildren().size() == 1) {
      String value = node.getChild(0).getTextValue().trim();
      if(value.indexOf("<![CDATA[") == 0) {
        value = value.substring(9, value.length()-3);
      }
//      System.out.println(value);
      return value;
    }
    
    StringBuilder builder = new StringBuilder();
    
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if(n == null || n.getName() == null) continue;
      builder.append('[');
      builder.append(n.getName());
      builder.append(':');
      if(n.getChildren() != null && n.getChildren().size() > 0) {
        String value = n.getChild(0).getTextValue().trim();
        if(value.indexOf("<![CDATA[") == 0) {
          value = value.substring(9, value.length()-3);
        }
        builder.append(value);
      }
      builder.append(']');
    }
//    System.out.println(" cai 2 "+ builder);
    return builder.toString();
  }

  @Override
  public String loadHeaderFromServer(String domainId) throws Exception {
    PluginClientHandler handler = new PluginClientHandler();
    return handler.send("export.office.data.plugin", "load.fields", domainId);
  }


}
