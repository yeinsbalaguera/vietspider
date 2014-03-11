package org.vietspider.notifier.notifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.vietspider.ui.widget.UIDATA;

//@author Emil

public class NotifierDialog extends NotifierShell {

  protected CLabel closeLabel;
  
  protected NotificationListener listener;
  protected String id;

  public String getId() { return id;  }
  
  NotifierDialog() {
    
  }

  public NotifierDialog(Notifier notifier) {
    super(notifier);
    createGUI(notifier);
  }
  
  public void createGUI(Notifier notifier) {
    GC gc = new GC(shell);

    final String message = notifier.getMessage();
    String lines[] = message.split("\n");
    Point longest = null;
    int typicalHeight = gc.stringExtent("X").y;

    for (String line : lines) {
      Point extent = gc.stringExtent(line);
      if (longest == null) {
        longest = extent;
        continue;
      }

      if (extent.x > longest.x) {
        longest = extent;
      }
    }
    gc.dispose();
    
    this.id = notifier.getId();
    this.listener = notifier.getListener();

    int minHeight = typicalHeight * lines.length;
    
    final Composite inner = new Composite(shell, SWT.NONE);

    GridLayout gl = new GridLayout(3, false);
    gl.marginLeft = 5;
    gl.marginTop = 0;
    gl.marginRight = 5;
    gl.marginBottom = 5;

    inner.setLayout(gl);

    Image icon = notifier.getIcon();
    if(icon != null) {
      CLabel imgLabel = new CLabel(inner, SWT.NONE);
      imgLabel.setLayoutData(new GridData(
          GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));
      imgLabel.setImage(icon);
    }

    final String title = notifier.getTitle();
    CLabel titleLabel = new CLabel(inner, SWT.NONE);
    titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
    titleLabel.setCursor(new Cursor(titleLabel.getDisplay(), SWT.CURSOR_HAND));
    titleLabel.setText(title);
    titleLabel.setForeground(titleFgColor);
    titleLabel.setFont(titleFont);
    titleLabel.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
       NotificationEvent event = new NotificationEvent(id);
       event.setComponent(NotificationEvent.TITLE);
       event.setTitle(title);
       event.setMessage(message);
       if(listener != null) listener.clicked(event);
       
       close();
      }
    });
    
    closeLabel = new CLabel(inner, SWT.NONE);
    closeLabel.setCursor(new Cursor(titleLabel.getDisplay(), SWT.CURSOR_HAND));
    closeLabel.setLayoutData(new GridData(
        GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END));
    closeLabel.setImage(closeIcon);
    closeLabel.addMouseTrackListener(new MouseTrackListener() {
      @SuppressWarnings("unused")
      public void mouseHover(MouseEvent arg0) {
      }
      
      @SuppressWarnings("unused")
      public void mouseExit(MouseEvent arg0) {
        closeLabel.setImage(closeIcon);
        closeLabel.redraw();
      }
      
      @SuppressWarnings("unused")
      public void mouseEnter(MouseEvent arg0) {
        closeLabel.setImage(closeIcon1);
        closeLabel.redraw();
      }
    });
    closeLabel.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        NotificationEvent event = new NotificationEvent(id);
        event.setComponent(NotificationEvent.CLOSE);
        event.setTitle(title);
        event.setMessage(message);
        if(listener != null) listener.clicked(event);
        
        closeLabel.setImage(closeIcon);
        closeLabel.redraw();
        
        close();
      }
    
    });

    Label text = new Label(inner, SWT.WRAP);
    text.setCursor(new Cursor(text.getDisplay(), SWT.CURSOR_HAND));
    text.setFont(UIDATA.FONT_9);
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.horizontalSpan = 3;
    text.setLayoutData(gd);
    text.setForeground(textFgColor);
    text.setText(message);
    text.setFont(textFont);
    text.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        NotificationEvent event = new NotificationEvent(id);
        event.setComponent(NotificationEvent.MESSAGE);
        event.setTitle(title);
        event.setMessage(message);
        if(listener != null) listener.clicked(event);
        
        close();
      }
    });
    minHeight = 100;
    shell.setSize(width, minHeight);
  }

  public void remove() {
    NotifierDialogs.remove(NotifierDialog.this);
  }
  
  protected void close() {
    shell.setAlpha(255);
    if (oldImage != null) oldImage.dispose();
    NotifierDialogs.remove(NotifierDialog.this);
    shell.dispose();
  }

}
