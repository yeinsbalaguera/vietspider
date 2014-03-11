/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.export;

import java.io.File;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.chars.TextSpliter;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.io.RWData;
import org.vietspider.export.ExportDataDialog;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.Object2XML;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public class CSVExportDataDialog extends ExportDataDialog {

  public void exportMore(File file, String data) {
    String [] elements = data.split("\n");
    int index = 0;
    TextSpliter spliter = new TextSpliter();
    for(int i = 0; i < elements.length; i+= 2) {
      File newFile = new File(file.getParentFile(), String.valueOf(index) + "." + file.getName());
      try{
        if(!newFile.exists()) newFile.createNewFile();
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
      export(elements[i], newFile, spliter.toArray(elements[i+1], ','));
      index++;
    }
  }

  public CSVExportDataDialog(Shell parent, String value_) {
    super(parent, value_, ".csv", "CVS Plugin");
  }

  public void export(String domainId, File file, String[] dataHeaders) {  
    String plugin = "cvs.export.data.plugin";
    if(stop) return;
    ClientConnector2 connector = ClientConnector2.currentInstance();
    HttpData httpData = null;
    try {
      PluginClientHandler handler = new PluginClientHandler();
      String pageValue = handler.send(plugin, "compute.export", domainId);
      int totalPage  = -1;
      try {
        totalPage = Integer.parseInt(pageValue);
      } catch (Exception e) {
        return;
      }

      if(totalPage < 1) return;
      int page = 1;

      StringBuilder buildHeader = new StringBuilder();
      for(int i = 0; i < dataHeaders.length; i++) {
        if(buildHeader.length() > 0) buildHeader.append(',');
        buildHeader.append(dataHeaders[i]);
      }
      if(!file.exists() || file.length() < 1) {
        RWData.getInstance().save(file, buildHeader.toString().getBytes(Application.CHARSET));
      }

      CSVModel model = new CSVModel();
      model.setDomainId(domainId);
      model.setHeaders(dataHeaders);

      File licenseFile = LicenseVerifier.loadLicenseFile();
      boolean license = LicenseVerifier.verify("export", licenseFile);
      if(!license && totalPage > 10) totalPage = 10;

      while(page <= totalPage) {
        Header [] headers = new Header[] {
            new BasicHeader("action", "export"),
            new BasicHeader("plugin.name", plugin)
            //                new BasicHeader("page", String.valueOf(page))
        };
        model.setPage(page);
        String xml = Object2XML.getInstance().toXMLDocument(model).getTextValue();
        byte [] bytes = xml.getBytes(Application.CHARSET);

        httpData = connector.loadResponse(URLPath.DATA_PLUGIN_HANDLER, bytes, headers);
        InputStream inputStream = httpData.getStream();

        try {
          if(file.length() > 0) RWData.getInstance().append(file, "\n".getBytes());
          RWData.getInstance().save(file, inputStream, true);
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

  public String loadHeaderFromServer(String domainId) throws Exception {
    PluginClientHandler handler = new PluginClientHandler();
    return handler.send("cvs.export.data.plugin", "load.fields", domainId);
  }


}
