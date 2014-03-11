package org.vietspider.webui.cms.vietspider;

import java.io.OutputStream;

import org.vietspider.common.io.LogService;
import org.vietspider.webui.cms.BufferWriter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
public class HeaderRenderer extends BufferWriter {
  
  public void write(OutputStream out, String title) {
    output = out;
    try{
      String header = CommonResources.INSTANCE.getHeaderTemplate();
      int idx = header.indexOf("$title");
      if(idx < 0 ){
        append(header);
        return;
      }
      append(header.substring(0, idx)); append(title); append(header.substring(idx+6, header.length()));
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
 }
}
