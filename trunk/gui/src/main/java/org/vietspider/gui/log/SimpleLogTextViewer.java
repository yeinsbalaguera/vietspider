/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.ClientPlugin;
import org.vietspider.common.Application;
import org.vietspider.common.io.CommonFileFilter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.gui.browser.StatusBar;
import org.vietspider.gui.log.action.LoadPageLogHandler;
import org.vietspider.gui.log.action.ViewLogHandler;
import org.vietspider.gui.monitor.DataMonitor;
import org.vietspider.gui.monitor.MonitorStatusBar;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.images.ToolbarResource;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 15, 2010  
 */
public class SimpleLogTextViewer extends DataMonitor implements ITextViewer {

  protected Text rtext;

  protected Combo cboFilter;

  protected StatusBar statusBar;

  protected String folder;

  public SimpleLogTextViewer(Composite parent,
      Workspace workspace, String folder) {
    super(parent, workspace);
    this.folder = folder;
    initUI();

  }

  public String getFolder() { return folder; }

  public String getFileName() {
    if(listDate.getSelectionCount() < 1) return null;
    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
    try {
      Date dateInstance = dateFormat.parse(listDate.getSelection()[0]);
      return CalendarUtils.getFolderFormat().format(dateInstance);
    } catch (Exception e) {
      ClientLog.getInstance().setThrowable(null, e);
    }
    return null;
  }


  protected void initUI() {
    String clazzName  = "org.vietspider.gui.monitor.Monitor";
    ApplicationFactory factory = new ApplicationFactory(this, "Monitor", clazzName);

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    SashForm sashMain = new SashForm(this, SWT.HORIZONTAL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    sashMain.setLayoutData(gridData);
    sashMain.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

    Composite leftComposite = new Composite(sashMain, SWT.NONE);
    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 5;
    gridLayout.marginWidth = 0;
    leftComposite.setLayout(gridLayout);

    Menu menu1 = new Menu(getShell(), SWT.POP_UP);
    factory.createMenuItem(menu1,"menuMonitor",new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt){
        loadDate(SimpleLogTextViewer.this.folder, new CommonFileFilter.OnlyFile());
      }
    });

    listDate = factory.createList(leftComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL, new String[]{});  
    listDate.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        spinPage.setSelection(1);
        viewLog(true);
      }
    }); 
    gridData = new GridData(GridData.FILL_BOTH);
    listDate.setLayoutData(gridData);
    listDate.setMenu(menu1);

    initViewer(factory, sashMain);

    sashMain.setWeights(new int[]{10, 90});

    loadDate(folder, new CommonFileFilter.OnlyFile());
  }

  protected void initViewer(ApplicationFactory factory, Composite parent) {
    Composite mainComposite = new Composite(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    mainComposite.setLayout(gridLayout);

    Composite topComposite = new Composite(mainComposite, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
    topComposite.setLayoutData(gridData);

    gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    topComposite.setLayout(gridLayout);

    factory.setComposite(topComposite);

    cboFilter = factory.createCombo(SWT.BORDER);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = 250;
    cboFilter.setLayoutData(gridData);
    cboFilter.addKeyListener( new KeyAdapter(){
      @Override
      public void keyPressed(KeyEvent e){ 
        if(e.keyCode == SWT.CR) viewLog(false);
      }
    });

    cboFilter.setItems(new String[]{"USER", "SOURCE", "APPLICATION", "SOURCE&ERROR"});

    Composite pageComposite = new Composite(topComposite, SWT.NONE);
    factory.setComposite(pageComposite);

    gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    pageComposite.setLayout(gridLayout);

    factory.createLabel("lblPage");

    spinPage = factory.createSpinner(SWT.BORDER);
    spinPage.setCursor(new Cursor(spinPage.getDisplay(), SWT.CURSOR_HAND));
    spinPage.setIncrement(1);
    gridData = new GridData();
    gridData.widthHint = 30;
    gridData.heightHint = 16;
    spinPage.setLayoutData(gridData);
    spinPage.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent event) {
        new ViewLogHandler(SimpleLogTextViewer.this, spinPage.getSelection());
      }
    });

    factory.setComposite(topComposite);

    final ToolbarResource resources = ToolbarResource.getInstance();
    butGo = resources.createIcon(topComposite, 
        resources.getImageGo(), resources.getTextGo(), new HyperlinkAdapter(){ 
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
    }); 
    butGo.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        viewLog(false);
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butGo.setImage(resources.getImageGo1());
        butGo.redraw();
      }
    });

    factory.setComposite(mainComposite);
    rtext = factory.createText(SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
    rtext.setFont(UIDATA.FONT_11);
    rtext.setLayoutData(new GridData(GridData.FILL_BOTH));
    rtext.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

    statusBar = new MonitorStatusBar(workspace, this, ClientPlugin.LOG);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    statusBar.setLayoutData(gridData); 
    statusBar.createPluginItems();
    statusBar.setComponent(this);
  }

  public void setPages(File file, String labelPage) {
    File [] files = file.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return !pathname.getName().endsWith(".temp");
      }
    });
    int total = files == null ? 0 :  (files.length);
    if(total > 1) {
      spinPage.setEnabled(true);
      spinPage.setMinimum(1);
      spinPage.setMaximum(total);
      spinPage.setToolTipText(String.valueOf(total) + labelPage);
    } else {
      spinPage.setEnabled(false);
    }
  }

  public void setFile(File file) {
    StringBuilder buf = new StringBuilder();
    try {
      byte [] bytes = RWData.getInstance().load(file);
      buf.append(new String(bytes, Application.CHARSET));
    } catch (Exception e) {
      buf.append(e.toString());
    }
    
    String text = buf.toString();
//    text = text.replaceAll("<br/>", "");
//    text = text.replace("<p></p>", "\n");
//    text = new String(new RefsDecoder().decode(text.toCharArray()));
    try {
      rtext.setText(text);
    } catch (Exception e) {
      System.err.println(e.toString());
      System.out.println(buf.toString());
    }
    
    statusBar.viewPlugins();
  }

 /* protected void editSource(String source) {
    source = source.substring(1);
    try {
      final Creator creator = 
        (Creator)workspace.getTab().createTool(Creator.class, true, SWT.CLOSE);
      String [] values = CharsUtil.split2Array(source, '.');
      if(values.length < 3) return;
      
      creator.selectData(new Worker[] {
          new ProcessLinkWorker(creator, null, 0)
      }, values[0], values[1], values[2]);
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(null, e);
    }
  }*/

  public Combo getCboFilter() { return cboFilter; }
  
  public void viewLog(boolean update) {
    new LoadPageLogHandler(SimpleLogTextViewer.this).load(update);
  }
  
  public boolean isHTML() { return false; }

}
