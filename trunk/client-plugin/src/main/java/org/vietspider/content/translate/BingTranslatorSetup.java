/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.translate;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.util.Worker;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008
 * login vbulletin admin:admin123  
 */
public class BingTranslatorSetup {

  private Shell shell;

  private Text txtApplicationId;
  private Combo txtFrom;
  private Combo txtTo;
  private Button []butMode = new Button[3];
  
  private String [] codes = {"ar","bg","ca","zh-CHS","zh-CHT","cs","da","nl","en","et","fi","fr","de","el","ht","he","hi","hu","id","it","ja","ko","lv","lt","no","pl","pt","ro","ru","sk","sl","es","sv","th","tr","uk","vi"};
  private String [] names = {"Arabic","Bulgarian","Catalan","Chinese Simplified","Chinese Traditional","Czech","Danish","Dutch","English","Estonian","Finnish","French","German","Greek","Haitian Creole","Hebrew","Hindi","Hungarian","Indonesian","Italian","Japanese","Korean","Latvian","Lithuanian","Norwegian","Polish","Portuguese","Romanian","Russian","Slovak","Slovenian","Spanish","Swedish","Thai","Turkish","Ukrainian","Vietnamese"};
  
  private Button butOk ;

  public BingTranslatorSetup(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    
    ApplicationFactory factory = new ApplicationFactory(shell,
        TranslationPlugin.getResources(), getClass().getName());
    
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(1, false));

    GridData gridData;

    Composite appIDComposite = new Composite(shell, SWT.NONE);
    appIDComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    appIDComposite.setLayoutData(gridData);  
    factory.setComposite(appIDComposite);

    factory.createLabel("lblAppID");  
    txtApplicationId = factory.createText();
    txtApplicationId.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtApplicationId.setLayoutData(gridData);  
    txtApplicationId.setText("A06CAD095F5CD226B6437F62669DBC9F42966F99");
    
    Composite languageComposite = new Composite(shell, SWT.NONE);
    languageComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    languageComposite.setLayoutData(gridData);  
    factory.setComposite(languageComposite);
    
    Composite fromComposite = new Composite(languageComposite, SWT.NONE);
    fromComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    fromComposite.setLayoutData(gridData);  
    factory.setComposite(fromComposite);

    factory.createLabel("lblFrom");  
    txtFrom = factory.createCombo(SWT.BORDER);
    txtFrom.setItems(names);
    txtFrom.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        int idx = txtFrom.getSelectionIndex();
        if(idx < 0) return;
        txtFrom.setText(codes[idx]);
      }
      
    });
    txtFrom.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtFrom.setLayoutData(gridData);  
    
    
    Composite toComposite = new Composite(languageComposite, SWT.NONE);
    toComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    toComposite.setLayoutData(gridData);  
    factory.setComposite(toComposite);

    factory.createLabel("lblTo");  
    txtTo = factory.createCombo(SWT.BORDER);
    txtTo.setFont(UIDATA.FONT_10);
    txtTo.setItems(names);
    txtTo.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        int idx = txtTo.getSelectionIndex();
        if(idx < 0) return;
        txtTo.setText(codes[idx]);
      }
      
    });
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtTo.setLayoutData(gridData); 
    txtTo.setText("vi");
    
    Group group = factory.createGroup(shell, SWT.NONE);
    group.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    group.setLayoutData(gridData);  
    group.setText(factory.getLabel("translationType"));
    
    Listener listener = new Listener () {
      public void handleEvent (Event e) {
        for (int i=0; i < butMode.length; i++) {
          Button child = butMode [i];
          if (e.widget != child && 
              (child.getStyle() & SWT.RADIO) != 0) {
            child.setSelection (false);
          }
        }
        ((Button) e.widget).setSelection(true);
      }
    };
    
    butMode[0] = factory.createButton(group, SWT.RADIO);
    butMode[0].setText ("Đơn ngữ");
    butMode[0].addListener(SWT.Selection, listener);
    
    butMode[1] = factory.createButton(group, SWT.RADIO);
    butMode[1].setText ("Song ngữ");
    butMode[1].addListener(SWT.Selection, listener);
    
    butMode[2] = factory.createButton(group, SWT.RADIO);
    butMode[2].setText ("Song ngữ theo đoạn");
    butMode[2].addListener(SWT.Selection, listener);
  
    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(bottom);
    
    factory.createButton("butClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        clear() ;
      }   
    }); 

    butOk = factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        saveConfig() ;
        new ShellSetter(BingTranslatorSetup.this.getClass(), shell);
      }   
    }); 
    
    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(BingTranslatorSetup.this.getClass(), shell);
        shell.close();
      }   
    });

    loadConfig();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    
//    shell.setSize(350, 250);
//    shell.setLocation(x, y);
    new ShellGetter(BingTranslatorSetup.class, shell, 550, 350, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }

  private void loadConfig() {
    Worker excutor = new Worker() {

      private String error = null;
      private TranslateMode config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/bing.translation.config")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(TranslateMode.class, bytes);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        
        if(config == null) return;
        if(config.getApplicationId() != null) {
          txtApplicationId.setText(config.getApplicationId());
        }
        
        if(config.getFrom() != null) {
          txtFrom.setText(config.getFrom());
        }
        
        if(config.getTo() != null) {
          txtTo.setText(config.getTo());
        }

        butMode[config.getMode()].setSelection(true);
      }
    };
    new ThreadExecutor(excutor, txtApplicationId).start();
  }

  private void saveConfig() {
    butOk.setEnabled(false);
    Worker excutor = new Worker() {

      private String error = null;
      private TranslateMode config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        config = new TranslateMode();
        config.setApplicationId(txtApplicationId.getText());
        config.setFrom(txtFrom.getText());
        config.setTo(txtTo.getText());
        for(int i = 0; i < butMode.length; i++) {
          if(butMode[i].getSelection()) {
            config.setMode((short)i);
            break;
          }
        }
      }

      public void execute() {
        if(config == null) return;
        try {
          Object2XML bean2XML = Object2XML.getInstance();
          String xml = bean2XML.toXMLDocument(config).getTextValue();

          Header [] headers =  new Header[] {
              new BasicHeader("action", "save"),
              new BasicHeader("file", "system/plugin/bing.translation.config")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = xml.getBytes(Application.CHARSET);
          connector.post(URLPath.FILE_HANDLER, bytes, headers);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        shell.close();
      }
    };
    new ThreadExecutor(excutor, txtApplicationId).start();
  }

  
  private void clear() {
    txtApplicationId.setText("A06CAD095F5CD226B6437F62669DBC9F42966F99");
    txtTo.setText("vi");
    txtFrom.setText("");
    butMode[0].setSelection(true);
  }
  
  public static void main(String[] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    
    BingTranslatorSetup searcher = new BingTranslatorSetup(shell);
    
    searcher.shell.addShellListener(new ShellAdapter() {       
      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent e){
        System.exit(0);
      }     
    });
    
    shell.open ();
    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }

}
