package org.vietspider.webui.cms.render;

import java.io.OutputStream;

import org.vietspider.webui.cms.BufferWriter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class LoginRendererImpl extends BufferWriter {
  
  @SuppressWarnings("unused")
  public void write(OutputStream out, String viewer, String value, String...params) throws Exception {
    output = out;
    
    append("<html>");
    append("<head>");
    if(value != null && value.trim().length() > 0 ) {
      append("<meta http-equiv=\"Refresh\" content=\"3;URL=/site/FILE/Login.html\">");
      append("</head>");
      append("<body>");
      append("<p>");
      append(value);
      append("</p>");
      append("</body>");
    } else {
      append("<meta http-equiv=\"Refresh\" content=\"0;URL=/\">");
      append("</head>");
    }
    append("</html>");
    
   
  }
  
}
