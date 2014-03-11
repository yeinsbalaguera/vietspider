package org.vietspider.gui.wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.vietspider.gui.creator.SourceEditorUtil;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public class ChannelWizardStep7 extends ChannelWizardComposite {
  
  private Browser browser;
  
  private Text startPageText;
  private List startPageList;
  
  private ImageHyperlink add;
  private Image addNormal, addDown;
  
  public ChannelWizardStep7(ChannelWizard wizard) {
    super(wizard);
    
    Composite composite = new Composite(this, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(2, false);
    composite.setLayout(gridLayout);
    
    browser = ApplicationFactory.createBrowser(this, null);
    gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);
    
    browser.addProgressListener(new ProgressAdapter() {
      @SuppressWarnings("unused")
      public void changed(ProgressEvent event) {
        String url = browser.getUrl();
        if(url != null && url.startsWith("http")) {
          if(!startPageText.isFocusControl()) {
            startPageText.setText(url);
          }
        }
        
        String status = lblStatus.getText().toLowerCase().trim();
        if(status.length() < 1 || status.startsWith("waiting for")) {
          lblStatus.setText("Waiting for "+ browser.getUrl() +" ...");
        }
      }
      
      @SuppressWarnings("unused")
      public void completed(ProgressEvent event) {
        clearMessage();
      }
    });
    
    composite = new Composite(this, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    composite.setLayoutData(gridData);
    
    gridLayout = new GridLayout(3, false);
    composite.setLayout(gridLayout);
    
    Label lblTemplate = new Label(composite, SWT.NONE);
    gridData = new GridData();
    lblTemplate.setFont(UIDATA.FONT_9);
    lblTemplate.setLayoutData(gridData);
    lblTemplate.setText(ChannelWizard.getLabel("step7.start.page"));
    
    startPageText = new Text(composite, SWT.BORDER);
    SourceEditorUtil.setDropTarget(startPageText);
    startPageText.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    startPageText.setLayoutData(gridData);
    startPageText.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if(e.keyCode == SWT.CR) {
          browser.setUrl(startPageText.getText());
        }
      }
    });
    
    /*Button button = new Button(composite, SWT.ARROW | SWT.DOWN);
    button.setToolTipText(ChannelWizard.getLabel("step7.start.page.tip"));
    gridData = new GridData();
    button.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        String url = startPageText.getText();
        if(url.toLowerCase().startsWith("http")) {
          startPageList.add(url);
        } 
      }      
    });
    button.setLayoutData(gridData);*/
    
    ClientRM clientRM = ChannelWizard.getResources();
    ApplicationFactory factory = 
      new ApplicationFactory(wizard, clientRM, getClass().getName());
    
    add = new ImageHyperlink(composite, SWT.CENTER);
    addNormal = factory.loadImage("add2.png");
    addDown = factory.loadImage("add3.png");
    add.setImage(addNormal);
    gridData = new GridData();
    add.setLayoutData(gridData);
    add.addHyperlinkListener(new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        add.setImage(addNormal);
        add.redraw();
      }

      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        add.setImage(addNormal);
        add.redraw();
      }
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        add.setImage(addNormal);
        add.redraw();
      }
    });
    add.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        String url = startPageText.getText();
        if(url.toLowerCase().startsWith("http")) {
          startPageList.add(url);
        } 
      }
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        add.setImage(addDown);
        add.redraw();
      }
    });
    
    startPageList = new List(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    SourceEditorUtil.setDropTarget(startPageList);
    startPageList.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 50;
    startPageList.setLayoutData(gridData);
    
    createButton(new GridData(GridData.FILL_HORIZONTAL));
  }
  
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=IHjGrztKyjU";
    String video = "http://vietspider.org/video/step8.avi";
    wizard.openHelp(youtube, video);
  }

  @Override
  public void show() {
    wizard.setTitle(" 8/10");
    String url = wizard.getSource().getPattern();
    int pos = url.indexOf('/', 10);
    if(pos > 0) {
      url = url.substring(0, pos); 
    } else {
      pos = url.indexOf('?', 10);
      if(pos > 0) url = url.substring(0, pos);
    }
    browser.setUrl(url);
    startPageText.setText(url);
    startPageText.setFocus();
  }
  
  @Override
  public boolean validateNext() {
    return true;
  }

  @Override
  public void next() {
    String[] items = startPageList.getItems();
    if(items == null || items.length < 1) {
      String url = startPageText.getText().trim();
      if(url.startsWith("http")) {
        items = new String[]{url};
      } else {
        items = new String[]{browser.getUrl()};
      }
    }
    wizard.getSource().setHome(items);
    showNext(wizard);
  }
  
  public void reset() {
    startPageList.removeAll();
    browser.setUrl("about:blank");
    startPageText.setText("");
  }
  

}
