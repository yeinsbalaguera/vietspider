package org.vietspider.ui.widget;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class ShellSetter {
  
  public ShellSetter(Class<?> cl, Shell shell) {
    Preferences prefs = Preferences.userNodeForPackage(cl);
    String key = cl.getSimpleName();
    boolean isMax = shell.getMaximized();    
    if(isMax){      
      shell.setMaximized(false);
      prefs.put(key + "max", "true");
    } else {
      prefs.put(key + "max", "false");
    }
    Point point = shell.getLocation();
    String location = String.valueOf( point.x) + "," + String.valueOf( point.y);
    prefs.put(key + "location", location);
    //setsize
    if( !shell.getMaximized()) {
      Point dimen= shell.getSize();
      String size = String.valueOf(dimen.x) + "," + String.valueOf(dimen.y);
      prefs.put(key +  "size", size);		
    }
  }
  
}
