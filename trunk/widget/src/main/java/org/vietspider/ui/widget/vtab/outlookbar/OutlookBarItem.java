package org.vietspider.ui.widget.vtab.outlookbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.UIDATA;

public class OutlookBarItem {
  
  private Label label;
  
  private ImageHyperlink image;
  
  private OutlookBarPane pane;  
  
  public OutlookBarItem(OutlookBarPane p, String lbl, Image img) {   
    pane = p;   
    image = new ImageHyperlink(pane.getControl(), SWT.CENTER);
    image.setBackground(pane.getControl().getBackground());
    image.addMouseTrackListener(new MouseTrackAdapter(){
      public void mouseEnter(MouseEvent e){
        if(label != null) label.setFont(UIDATA.FONT_9VB);
      }
      public void mouseExit(MouseEvent e){
        if(label != null) label.setFont(UIDATA.FONT_9);
      }
    });
    image.setImage(img);
    
    label = new Label(pane.getControl(), SWT.CENTER);
    label.setFont(UIDATA.FONT_10);
    label.setBackground(pane.getControl().getBackground());
    label.setForeground(new Color(label.getDisplay(), 0, 55, 111));
    label.setText(lbl);
    
    pane.addItem(this);
  }   
  
  public ImageHyperlink getImage() { return image; } 
  
  public Label getLabel(){ return label; } 
}
