package org.vietspider.webui.cms.vietspider;

import java.io.OutputStream;

import org.vietspider.common.io.LogService;
import org.vietspider.webui.cms.BufferWriter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jan 11, 2007
 */
public class EventRenderer extends BufferWriter {
  
  public void write(OutputStream out, String viewer) throws Exception { 
    output = out;
    try {
      String html = CommonResources.INSTANCE.getEventTemplate();
      
      String pattern = "$event";
      int idx = html.indexOf(pattern);
      int start = 0;
      if(idx > -1){
        append(html.substring(start, idx));
        EventCacher eventCacher = EventCacher.INSTANCE;
        if(eventCacher.getData() == null) eventCacher.render(viewer);
        append(eventCacher.getData().toString());    
        start = idx + pattern.length();
      }
      
      if(start < html.length()) append(html.substring(start));
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}
