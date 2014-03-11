package org.vietspider.webui.cms.render;

import java.io.OutputStream;

import org.vietspider.webui.cms.BufferWriter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class RedirectRenderer extends BufferWriter  {
  
  /*public void write(OutputStream out, String viewer, String value, String...params) throws Exception {
    write(out, viewer, "5", value, params);
  }*/
  
  @SuppressWarnings("unused")
  public void write(OutputStream out, String viewer, String time, String value, String...params) throws Exception {
    output = out;
    append("<html>");
    append("<head>");
    if(value != null && value.trim().length() > 0 ) {
      append("<meta http-equiv=\"Refresh\" content=\""+time+";URL=");append(params[0]); append("\">");
      append("</head>");
      append("<body>");
      append("<p>");
      append(value);
      append("</p>");
      append("</body>");
    } else {
      append("<meta http-equiv=\"Refresh\" content=\"0;URL=");append(params[1]); append("\">");
      append("</head>");
    }
    append("</html>");
   
  }
  
}
