/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.homepage;

import static org.vietspider.link.pattern.LinkPatternFactory.createPatterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vietspider.gui.creator.SendSource;
import org.vietspider.gui.creator.action.ListHomepage;
import org.vietspider.gui.creator.action.LoadHomepage;
import org.vietspider.gui.creator.action.SaveHomepage;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.link.pattern.LinkPatternExtractor;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 21, 2008  
 */
public class URLHomepageDialog {
  
  protected Shell shell;
  
  private Text txtDialog;
  
  private Spinner spnPage;
  private Label lblPage;
  
  private String group;
  private String category;
  private String name;
  
  private String template;
  
  public URLHomepageDialog(Shell parent, String t, String g, String c, String n) {
    shell = new Shell(parent, SWT.RESIZE | SWT.MODELESS | SWT.TITLE);
    shell.setImage(parent.getImage());
    
    this.template = t;
    
    this.group = g;
    this.category = c;
    this.name = n;
    
    shell.setLayout(new GridLayout(1, true));
    shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    
    shell.addShellListener(new ShellAdapter(){
      @SuppressWarnings("unused")
      public void shellDeactivated(ShellEvent e) {
        saveShellProperties();
      }
    });
    
    parent.addDisposeListener(new DisposeListener() {
      @SuppressWarnings("unused")
      public void widgetDisposed(DisposeEvent arg0) {
        shell.dispose();
      }
    });
    
    loadShellProperties();
    
    txtDialog = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    txtDialog.setLayoutData(gridData);
    txtDialog.addKeyListener(new KeyAdapter() {
      
      private boolean paste = false;
      
      public void keyReleased(KeyEvent event) {
        if(event.keyCode == SWT.MOD1) paste = false;
      }
      
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.MOD1) {
          paste = true;
          return;
        }
        if(!paste || event.keyCode != 'v') return ;
        final int time = 200;
        Runnable timer = new Runnable () {
          public void run () {
            paste();
          }
        };
        txtDialog.getDisplay().timerExec (time, timer);
      }
      
    });
    
    ClientRM resources = new ClientRM("Creator");
    
    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = 700;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    
    /*Button butRepair  = new Button(bottom, SWT.BORDER);
    butRepair.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        repair();
      }      
    });
    butRepair.setText("Repair");*/
    
    Composite composite = new Composite(bottom, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    
    lblPage = new Label(composite, SWT.LEFT);
    lblPage.setText(resources.getLabel("homepage.page")); 
    spnPage = new Spinner(composite, SWT.BORDER);
    spnPage.setFont(UIDATA.FONT_8V);
    gridData = new GridData();
    gridData.widthHint = 50;
    spnPage.setMinimum(0);
    spnPage.setIncrement(1);
    spnPage.setLayoutData(gridData);
    spnPage.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent event) {
        new LoadHomepage(URLHomepageDialog.this);
      }
    });
    /*cboPage = new Combo(composite, SWT.BORDER);
    cboPage.setFont(UIDATA.FONT_8V);
    cboPage.setVisibleItemCount(15);
    cboPage.add("1");
    gridData = new GridData();
    gridData.widthHint = 50;
    cboPage.setLayoutData(gridData);
    cboPage.select(0);
    cboPage.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new LoadHomepage(URLHomepageDialog.this);
      }      
    });*/
    
    Button butSend  = new Button(bottom, SWT.BORDER);
    butSend.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        sendHomepage();
      }      
    });
    butSend.setText(resources.getLabel("homepage.send")); 
    
    Label label = new Label(bottom, SWT.SEPARATOR);
    label.setLayoutData(new RowData(10, 25));

    Button butExport  = new Button(bottom, SWT.BORDER);
    butExport.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ExportHomepageDialog(getShell(), group, category, name);
      }      
    });
    butExport.setText(resources.getLabel("homepage.export")); 
    
    Button butImport  = new Button(bottom, SWT.BORDER);
    butImport.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ImportHomepageDialog(getShell(), URLHomepageDialog.this);
      }      
    });
    butImport.setText(resources.getLabel("homepage.import")); 
    
    label = new Label(bottom, SWT.SEPARATOR);
    label.setLayoutData(new RowData(10, 25));
    
    Button butDelete  = new Button(bottom, SWT.BORDER);
    butDelete.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        txtDialog.setText("");
      }      
    });
    butDelete.setText("Clear");
    
    Button butFilter  = new Button(bottom, SWT.BORDER);
    butFilter.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        filter();
      }      
    });
    butFilter.setText(resources.getLabel("homepage.filter")); 
    
    Button butSave  = new Button(bottom, SWT.BORDER);
    butSave.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        filter();
        String text = txtDialog.getText().trim();
        if(text.isEmpty()) return;
        new SaveHomepage(URLHomepageDialog.this);
      }      
    });
    butSave.setText(resources.getLabel("homepage.save")); 
    
    Button butOk  = new Button(bottom, SWT.BORDER);
    butOk.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        saveShellProperties();
        shell.dispose();
      }      
    });
    butOk.setText(resources.getLabel("homepage.ok"));
    
    
    new ListHomepage(this);
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();
  }
  
  private void paste() {
    Clipboard clipboard = new Clipboard(txtDialog.getDisplay());
    HTMLTransfer transfer = HTMLTransfer.getInstance();
    String text = (String)clipboard.getContents(transfer);
    
    if(text == null || text.trim().isEmpty()) {
      TextTransfer transfer2 = TextTransfer.getInstance();
      text = (String)clipboard.getContents(transfer2);
    }
    
    if(text == null || text.trim().isEmpty()) return;
    
    if(text.indexOf('<') > -1 && text.indexOf('>') > -1) {
      try {
        HTMLDocument document = new HTMLParser2().createDocument(text);
        HyperLinkUtil linkUtil = new HyperLinkUtil();
        List<String> collection = linkUtil.scanSiteLink(document.getRoot());
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < collection.size(); i++) {
          builder.append(collection.get(i).replaceAll("&amp;", "&")).append('\n'); 
        }
        txtDialog.setText(builder.toString());
      } catch (Exception e) {
      }
    }
  }
  
  protected void saveShellProperties() {
    Preferences prefs2 = Preferences.userNodeForPackage(getClass());
    Point point = shell.getLocation();
    Point size = shell.getSize();
    try {
      prefs2.put("URLHomepageDialog_location_x", String.valueOf(point.x));
      prefs2.put("URLHomepageDialog_location_y", String.valueOf(point.y));
      prefs2.put("URLHomepageDialog_size_w", String.valueOf(size.x));
      prefs2.put("URLHomepageDialog_size_h", String.valueOf(size.y));
    } catch (Exception e) {
    }
  }
  
  private void filter() {
    String [] templates = template.split("\n");
    
    LinkPatternExtractor patterns = createPatterns(LinkPatternExtractor.class, templates);
    List<String> list = new ArrayList<String>();
    patterns.extract(txtDialog.getText(), list);
    
    Collections.sort(list);
    
    StringBuilder builder = new StringBuilder();
    for(String ele : list) {
      builder.append(ele).append('\n');
    }
    txtDialog.setText(builder.toString().trim());
  }
  
  protected void loadShellProperties() {
    Shell parent = shell.getShell();
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    int x,y;
    int width, height;
    try {
      x = Integer.parseInt(prefs.get("URLHomepageDialog_location_x", ""));
    } catch (Exception e) {
      x = parent.getLocation().x + 300;
    }
    
    try {
      width = Integer.parseInt(prefs.get("URLHomepageDialog_size_w", ""));
    } catch (Exception e) {
      width = 600;
    }
    
    try {
      y = Integer.parseInt(prefs.get("URLHomepageDialog_location_y", ""));
    } catch (Exception e) {
      y = parent.getLocation().y + 100;
    }
    
    try {
      height = Integer.parseInt(prefs.get("URLHomepageDialog_size_h", ""));
    } catch (Exception e) {
      height = 450;
    }
    
    shell.setLocation(x, y);
    shell.setSize(width, height);
  }
  
  protected void sendHomepage() {
    Shell window = new Shell(getShell(), SWT.TITLE | SWT.APPLICATION_MODAL);
    window.setLayout(new FillLayout());
    new SendSource(window, new SendHomepageWorker(group, category, name));
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 200)/ 2;
    window.setLocation(x, y);
    window.pack();
    window.open();
  }
  
  public Text getTxtDialog() { return txtDialog; }

  public Spinner getSpinnerPage() {  return spnPage;  }

  public String getGroup() { return group; }

  public String getCategory() { return category; }

  public String getName() { return name; }

  public String getTemplate() { return template; }

  public Shell getShell() { return shell; }
  
  public Label getLabelPage() { return lblPage; }
 
  /*public void repair() {
    Worker excutor = new Worker() {
      
      public void abort() {
        ClientConnector.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        try {
          String value  = group+"."+category+"."+name;
          new HomepagesClientHandler().repairHomepages(value);
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {}
    };
    new ThreadExecutor(excutor, shell).start();
  }*/
}
