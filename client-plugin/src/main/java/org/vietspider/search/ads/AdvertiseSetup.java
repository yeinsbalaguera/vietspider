/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.search.ads;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.prefs.Preferences;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.util.Worker;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
class AdvertiseSetup {

  private Shell shell;
  
  private List lstAdv;
  private Text txtName;
  private Text txtContent;
  private Text txtImage;
  private Text txtLink;
  private DateTime dtStart;
  private DateTime dtEnd;
  private Combo cboType;
  private AdvPreview preview;

  AdvertiseSetup(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    shell.setText("Quan ly quang cao");
    shell.setLayout(new GridLayout(2, false));
    
    ClientRM resources = new ClientRM(getClass(), "Advertise");
    
    ApplicationFactory factory = new ApplicationFactory(shell, resources, getClass().getName());
    lstAdv = factory.createList(SWT.BORDER, new String[0]);
    GridData gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.widthHint = 250;
    lstAdv.setLayoutData(gridData);
    lstAdv.addSelectionListener(new SelectionAdapter() {
      
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent arg0) {
        loadData();
      }
      
    });
    
    Composite composite = new Composite(shell, SWT.NONE);
    composite.setLayout(new GridLayout(3, false));
    factory.setComposite(composite);
    gridData = new GridData(GridData.FILL_BOTH);
    composite.setLayoutData(gridData);

    factory.createLabel("lblName");
    txtName = factory.createText();
    txtName.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 200;
    gridData.horizontalSpan = 2;
    txtName.setLayoutData(gridData);  

    factory.createLabel("txtContent");
    txtContent = factory.createText(SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    txtContent.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 500;
    gridData.heightHint = 70;
    gridData.horizontalSpan = 2;
    txtContent.setLayoutData(gridData);
    
    factory.createLabel("lblImage");
    txtImage = factory.createText();
    txtImage.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 400;
    txtImage.setLayoutData(gridData);
    txtImage.setEditable(false);
    factory.createButton("butBrowse", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        File file  = getFile();
        if(file  == null) return;
        txtImage.setText(file.getAbsolutePath());
        if(file.getName().endsWith(".swf")) return;
        Image image = new Image (shell.getDisplay(), file.getAbsolutePath());
        preview.setImage(image);
      }
    });
    
    preview = new AdvPreview(factory.getComposite());
    
    
    factory.createLabel("lblLink");
    txtLink = factory.createText();
    txtLink.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 350;
    gridData.horizontalSpan = 2;
    txtLink.setLayoutData(gridData);
    
    factory.createLabel("lblType");
    cboType = factory.createCombo(SWT.BORDER);
    cboType.setItems(new String[]{"TOP", "LEFT", "RIGHT"});
    cboType.select(0);
    
    
    Composite compDate = new Composite(composite, SWT.NONE);
    compDate.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    compDate.setLayoutData(gridData);
    factory.setComposite(compDate);
    
    Composite compStartDate = new Composite(compDate, SWT.NONE);
    compStartDate.setLayout(new GridLayout(1, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    compStartDate.setLayoutData(gridData);
    factory.setComposite(compStartDate);
    
    factory.createLabel("lblStart");
    dtStart = new DateTime(compStartDate, SWT.CALENDAR);
    gridData = new GridData();
//    gridData.widthHint = 350;
    gridData.horizontalSpan = 2;
    dtStart.setLayoutData(gridData);
    
    Composite compEndDate = new Composite(compDate, SWT.NONE);
    compEndDate.setLayout(new GridLayout(1, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    compEndDate.setLayoutData(gridData);
    factory.setComposite(compEndDate);
    
    factory.createLabel("lblEnd");
    dtEnd = new DateTime(compEndDate, SWT.CALENDAR);
    gridData = new GridData();
//    gridData.widthHint = 350;
    gridData.horizontalSpan = 2;
    dtEnd.setLayoutData(gridData);
    
    shell.addShellListener(new ShellAdapter() {
      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent e) {
        new ShellSetter(AdvertiseSetup.this.getClass(), shell);
      }
    });
    
    Composite bottomComposite = new Composite(composite, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    bottomComposite.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottomComposite.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(bottomComposite);
    
    factory.createButton("butSave", SWT.PUSH,  new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        save();
      }   
    }); 
    
    factory.createButton("butReset", SWT.PUSH,  new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        reset();
      }   
    }); 
    
    loadAdvs();
    reset();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 650) / 2;
    int y = (displayRect.height - 500)/ 2 - 150;
    shell.setImage(parent.getImage());
    new ShellGetter(AdvertiseSetup.class, shell, 650, 550, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }
  
  private void reset() {
    txtName.setText("");
    txtContent.setText("");
    txtImage.setText("");
    txtLink.setText("");
    cboType.select(0);
    
    Calendar calendar = Calendar.getInstance();
    int date = calendar.get(Calendar.DATE);
    int month = calendar.get(Calendar.MONTH);
    int year = calendar.get(Calendar.YEAR);
    dtStart.setDate(year, month, date);
    
    calendar = Calendar.getInstance();
    calendar.set(Calendar.DATE, date+30);
    date = calendar.get(Calendar.DATE);
    month = calendar.get(Calendar.MONTH);
    year = calendar.get(Calendar.YEAR);
    
    dtEnd.setDate(year, month, date);
  }

  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openImage", "");
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setFilterExtensions(new String[]{"*.jpg;*.png;*.jpeg;*.bmp;*.gif;*.swf", "*"});
    if(p != null) {
      File file = new File(p);
      if(file.isDirectory()) {
        dialog.setFilterPath(p);
      } else {
        try {
          dialog.setFilterPath(file.getParentFile().getAbsolutePath());
        } catch (Exception e) {
        }
      }
    }
    p = dialog.open();
    if( p != null) prefs.put("openImage", p);
    if( p == null || p.trim().isEmpty()) return null;    
    return new File(p);
  }
  
  private void save() {
    Worker excutor = new Worker() {

      private String error = null;
      private String xml  = null;
      private String name = null;
      private File imageFile;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        Advertise advertise = new Advertise();
        advertise.setName(txtName.getText());
        advertise.setText(txtContent.getText());
        imageFile  = new File(txtImage.getText());
        advertise.setImage(imageFile.getName());
        advertise.setLink(txtLink.getText());
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        calendar.set(Calendar.DATE, dtStart.getDay());
        calendar.set(Calendar.MONTH, dtStart.getMonth());
        calendar.set(Calendar.YEAR, dtStart.getYear());
        advertise.setStart(calendar.getTimeInMillis());
        
        calendar.set(Calendar.DATE, dtEnd.getDay());
        calendar.set(Calendar.MONTH, dtEnd.getMonth());
        calendar.set(Calendar.YEAR, dtEnd.getYear());
        advertise.setEnd(calendar.getTimeInMillis());
        
        advertise.setType(cboType.getSelectionIndex());
        try {
          xml = Object2XML.getInstance().toXMLDocument(advertise).getTextValue();
          name = NameConverter.encode(advertise.getName());
        } catch (Exception e) {
          ClientLog.getInstance().setThrowable(null, e);
          error = e.toString();
        }
      }

      public void execute() {
        if(xml == null) return;
        try {
          
          if(imageFile.exists())  deleteOldImage(name);
          
          Header [] headers =  new Header[] {
              new BasicHeader("action", "save"),
              new BasicHeader("file", "system/cms/search/ads/"+name+".adv")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = xml.getBytes(Application.CHARSET);
          connector.post(URLPath.FILE_HANDLER, bytes, headers);
          
          if(!imageFile.exists()) return;
          
          headers =  new Header[] {
              new BasicHeader("action", "save"),
              new BasicHeader("file", "system/cms/search/ads/"+imageFile.getName())
          };
          bytes = RWData.getInstance().load(imageFile);
          connector.post(URLPath.FILE_HANDLER, bytes, headers);
        } catch (Exception e) {
          ClientLog.getInstance().setThrowable(null, e);
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        loadAdvs();
      }
    };
    new ThreadExecutor(excutor, txtName).start();
  }
  
  private void loadAdvs() {
    Worker excutor = new Worker() {

      private String error = null;
      private String [] items  = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers =  new Header[] {
              new BasicHeader("action", "list.folder"),
              new BasicHeader("file", "system/cms/search/ads/")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          
          ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
          ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
          objectOutputStream.writeObject(new AdvFileFilter());
          objectOutputStream.close();
          
          byte [] bytes = connector.post(URLPath.FILE_HANDLER, bytesOutput.toByteArray(), headers);
          String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
          java.util.List<String> list = new ArrayList<String>();
          for(int i = 0;  i < elements.length; i++) {
            int idx = elements [i].lastIndexOf('.');
            if(idx < 0) continue;
            String ext = elements[i].substring(idx+1);
            if(!"adv".equals(ext)) continue;
            list.add(NameConverter.decode(elements[i].substring(0, idx)));
          }
          items = list.toArray(new String[list.size()]);
        } catch (Exception e) {
          ClientLog.getInstance().setThrowable(null, e);
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(items == null) return;
        lstAdv.setItems(items);
      }
    };
    new ThreadExecutor(excutor, txtName).start();
  }
  
  private void loadData() {
    Worker excutor = new Worker() {

      private String error = null;
      private Advertise advertise  = null;
      private String name;
      private byte[] imageData;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        try {
          String []items = lstAdv.getSelection();
          if(items == null || items.length < 1) return;
          name = NameConverter.encode(items[0]);
        } catch (Exception e) {
          ClientLog.getInstance().setThrowable(null, e);
        }
      }

      public void execute() {
        if(name == null) return;
        try {
          Header [] headers =  new Header[] {
              new BasicHeader("action", "load.file"),
              new BasicHeader("file", "system/cms/search/ads/"+name+".adv")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          
          byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
          advertise = XML2Object.getInstance().toObject(Advertise.class, bytes);
          
          headers =  new Header[] {
              new BasicHeader("action", "load.file"),
              new BasicHeader("file", "system/cms/search/ads/"+advertise.getImage())
          };
          imageData = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
         
        } catch (Exception e) {
          ClientLog.getInstance().setThrowable(null, e);
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(advertise == null) return;
        if(advertise.getName() != null) {
          txtName.setText(advertise.getName());
        }
        if(advertise.getText()!= null) {
          txtContent.setText(advertise.getText());
        }
        if(advertise.getLink() != null) {
          txtLink.setText(advertise.getLink());
        }
        if(advertise.getImage() != null) {
          txtImage.setText(advertise.getImage());
        }
        cboType.select(advertise.getType());
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(advertise.getStart());
        dtStart.setDate(calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(advertise.getEnd());
        dtEnd.setDate(calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        
        
        if(imageData == null) return;
        if(advertise.getImage() == null || 
            advertise.getImage().endsWith(".swf")) return;
        Image image = new Image(shell.getDisplay(), new ImageData(new ByteArrayInputStream(imageData)));
        preview.setImage(image);
      }
    };
    WaitLoading loading = new WaitLoading(txtName, excutor);
    loading.open();
  }
  
  private void deleteOldImage(String name) throws Exception  {
    Header[] headers =  new Header[] {
        new BasicHeader("action", "load.file"),
        new BasicHeader("file", "system/cms/search/ads/"+name+".adv")
    };

    ClientConnector2 connector = ClientConnector2.currentInstance();
    
    byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
    if(bytes == null || bytes.length < 2) return;
    Advertise advertise = XML2Object.getInstance().toObject(Advertise.class, bytes);
    if(advertise == null || advertise.getImage() == null) return;
    
    headers =  new Header[] {
        new BasicHeader("action", "delete"),
        new BasicHeader("file", "system/cms/search/ads/"+advertise.getImage())
    };
    connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
  }

}
