package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.html.Name;
import org.vietspider.ui.widget.UIDATA;

public class TreeFactory {  
  
  private Color color0, color1, color2, color3, color4;
	
	public TreeItem get(TreeItem arg0, Name name, int order, int size) {
		 TreeItem item = new TreeItem(arg0, SWT.NONE);	
		 setStyle( item, name.toString(), order, size);
     item.setFont(UIDATA.FONT_10);
		 return item;
	}	
  
  public TreeItem get(Tree arg0, Name name, int order, int size) {
     TreeItem item = new TreeItem(arg0, SWT.NONE);  
     setStyle(item, name.toString(), order, size);
     item.setFont(UIDATA.FONT_10);
     return item;
  } 
  
	private void setStyle(TreeItem item, String name, int order, int s){
		item.setText(name+"-"+String.valueOf( order));
    if(s == -2) {
      if(color0 == null) color0 = new Color( item.getDisplay(), 119, 119, 0);
      item.setForeground(color0);   
      return;
    }
    if(s == -1) {
      if(color1 == null) color1 = new Color( item.getDisplay(), 250, 0, 0);
      item.setForeground(color1);   
      return;
    }
		if( s <= 200 ){
      if(color2 == null) color2 = new Color( item.getDisplay(), 0, 210, 210);
      item.setForeground(color2);  
			return;
		}		
		if( 200 < s && s <= 1000 ){
      if(color3 == null) color3 = new Color( item.getDisplay(), 0, 111, 111);
      item.setForeground(color3);  
			return;
		}	
		if( s>1000 ){
      if(color4 == null) color4 = new Color( item.getDisplay(), 0, 0, 0);
      item.setForeground(color4);  
			return;
		}	
	}	
}
