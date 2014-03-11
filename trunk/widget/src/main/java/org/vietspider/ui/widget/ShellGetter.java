package org.vietspider.ui.widget;

import java.util.prefs.Preferences;

import org.eclipse.swt.widgets.Shell;

public class ShellGetter {
  
  public ShellGetter(Class<?> cl, Shell shell, int width, int height) {
    this(cl, shell, width, height, 50, 50);
  }
  
  public ShellGetter(Class<?> cl, Shell shell, int width, int height, int locX, int locY) {
    Preferences prefs = Preferences.userNodeForPackage(cl);
    String key = cl.getSimpleName();
    String location = prefs.get(key + "location", "");
    if(location.length() > 0) {
      String com[]=location.split(",");
      if(com.length>1){
        shell.setLocation(Integer.parseInt(com[0]), Integer.parseInt(com[1]));
      }
    } else {
      new ShellSetter(cl, shell);
      shell.setLocation(locX, locY);	       
    }
    
    String size = prefs.get(key + "size", "");
    if(size != null && size.length() > 0) {
      String com[] = size.split(",");
      if(com.length>1){
        shell.setSize(Integer.parseInt(com[0]), Integer.parseInt(com[1]));
      }
    }else{
      shell.setSize(width, height);
    }		    
    String isMax = prefs.get(key + "max", "");    
    if(isMax != null && isMax.trim().equals("true")) shell.setMaximized(true);
  }
  
}
