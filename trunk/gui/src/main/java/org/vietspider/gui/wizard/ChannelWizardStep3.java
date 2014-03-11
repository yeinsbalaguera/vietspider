package org.vietspider.gui.wizard;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.SourceEditor;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.model.ExtractType;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.ui.htmlexplorer.AutoSelectDataNode2;
import org.vietspider.ui.htmlexplorer.HTMLExplorer;
import org.vietspider.ui.htmlexplorer.TreeHandler;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public class ChannelWizardStep3 extends HTMLExplorer implements IChannelWizardStep {

  private ChannelWizard wizard;

  public ChannelWizardStep3(ChannelWizard wizard) {
    super(wizard, HTMLExplorer.NONE);

    this.wizard = wizard;

    /*tree.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
      }
    });*/

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    createButton(gridData);
  }

  protected void createButton(GridData gridData) {
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(gridData);

    GridLayout gridLayout = new GridLayout(5, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);

    wizard.createToolBarButton(composite);

    /*butRemoveAll = new Button(composite, SWT.PUSH);
    butRemoveAll.setFont(UIDATA.FONT_9);
    butRemoveAll.setText(ChannelWizard.getLabel("step3.remove.all"));
    butRemoveAll.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM clientRM = new ClientRM("HTMLExplorer");
        msg.setMessage(clientRM.getLabel("remove.all.message"));
        if(msg.open() != SWT.YES) return ;
        box.removeAll();
      }
    });
    butRemoveAll.setFont(UIDATA.FONT_9);

    butUp = new Button(composite, SWT.PUSH);
    butUp.setFont(UIDATA.FONT_9);
    butUp.setText(ChannelWizard.getLabel("step3.up"));
    butUp.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        box.up();
      }
    });
    butUp.setVisible(false);
    butUp.setFont(UIDATA.FONT_9);

    butDown = new Button(composite, SWT.PUSH);
    butDown.setFont(UIDATA.FONT_9);
    butDown.setText(ChannelWizard.getLabel("step3.down"));
    butDown.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        box.down();
      }
    });
    butDown.setVisible(false);
    butDown.setFont(UIDATA.FONT_9);*/
    
    Composite removeComposite = new Composite(composite, SWT.NONE);
    composite.setBackground(getBackground());
    removeComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    removeComposite.setLayoutData(gridData);

    lblStatus = new Label(removeComposite, SWT.NONE);
    lblStatus.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblStatus.setLayoutData(gridData);
    lblStatus.setFont(UIDATA.FONT_10);
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));

    butRemovePath = new Button(removeComposite, SWT.PUSH);
    butRemovePath.setFont(UIDATA.FONT_9);
    butRemovePath.setText(ChannelWizard.getLabel("step3.remove"));
    butRemovePath.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(errorPath != null) {
          box.removePath(errorPath);
          clearInformation();
        }
        showErrorPath(null);
      }
    });
    butRemovePath.setVisible(false);
    gridData = new GridData();
    butRemovePath.setLayoutData(gridData);
    butRemovePath.setFont(UIDATA.FONT_9);
    
    Button butHelp = new Button(composite, SWT.PUSH);
    butHelp.setFont(UIDATA.FONT_9);
    butHelp.setText("Help");//ChannelWizard.getLabel("previous"));
    butHelp.addSelectionListener( new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        openHelp();
      }
    });
    gridData = new GridData();
    butHelp.setLayoutData(gridData);

    Button butPrevious = new Button(composite, SWT.PUSH);
    butPrevious.setFont(UIDATA.FONT_9);
    butPrevious.setText(ChannelWizard.getLabel("previous"));
    butPrevious.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        ChannelWizardComposite.showPrevious(wizard);
      }
    });
    gridData = new GridData();
    butPrevious.setLayoutData(gridData);

    Button butNext = new Button(composite, SWT.PUSH);
    butNext.setFont(UIDATA.FONT_9);
    butNext.setText(ChannelWizard.getLabel("next"));
    butNext.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        if(!validateNext()) return;
        next();
      }
    });
    gridData = new GridData();
    butNext.setLayoutData(gridData);
  }
  
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=RLs4faCjUnk";
    String video = "http://vietspider.org/video/step3.avi";
    wizard.openHelp(youtube, video);
  }

  private boolean validateNext() {
    String [] paths = box.getItems();
    if(paths == null || paths.length < 1) {
      showError(ChannelWizard.getLabel("step3.no.path"));
      return false;
    }
    return true;
  }

  public void show() {
    if(this.webClient == null) {
      Creator creator = wizard.getTemp("creator");
      SourceEditor editor = creator.getInfoControl().getSourceEditor();
      this.webClient = editor.getWebClient();
    }
    wizard.setTitle(" 3/10");
    clearInformation();
    
    if(document != null) return;
    try {
      HTMLParserDetector detector = new HTMLParserDetector();
      byte[] bytes = wizard.getTemp("page.data.content");
      String text = new String(bytes, wizard.getSource().getEncoding());
      document = detector.createDocument(text);
    } catch (Exception e) {
      showError(e.toString());
    }
    if(document == null) return;

    wizard.putTemp("document", document);

    tree.removeAll();
    if(handler == null) handler = new TreeHandler();
    handler.createTreeItem(tree, document);

    try {
      byte[] bytes = wizard.getTemp("page.data.content");
      browser.setText(new String(bytes, wizard.getSource().getEncoding()));
    } catch (Exception e) {
      showError(e.toString());
    }

    String address = wizard.getSource().getPattern();
    new AutoSelectDataNode2(this, document, address, handler, tree);

    autoSelect();
  }

  private void autoSelect() {
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();
    HyperLinkUtil hyperlinkUtil = new HyperLinkUtil();
    HTMLNode header = null;
    HTMLNode body = null;
    try {
      NodePath nodePath  = pathParser.toPath("HEAD");
      header = extractor.lookNode(document.getRoot(), nodePath);
      nodePath  = pathParser.toPath("BODY");
      body = extractor.lookNode(document.getRoot(), nodePath);
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
    }

    if(header == null || body == null) return;

    try {
      String address = wizard.getSource().getPattern();
      URL home = new URL(address);
      hyperlinkUtil.createFullNormalLink(document.getRoot(), home);
      hyperlinkUtil.createFullImageLink(document.getRoot(), home);
      Map<String,String> map = new HashMap<String,String>(); 
      map.put("link","href");
      map.put("script","src");
      hyperlinkUtil.createFullLink(header, map, home, null);
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
    }

    searchRenderer.viewDocument(header, body, wizard.getSource().getPattern());
  }

  private void next() {
    Worker excutor = new Worker(new Worker[0]) {

      HTMLDocument doc = null;
      String message = null;

      public void abort() {
      }

      public void before() {
        Region[] detachPaths = new Region[2];
        detachPaths[0] = new Region(Region.EXTRACT, box.getItems());
        wizard.getSource().setExtractRegion(detachPaths);
        wizard.putTemp("extract.path", box.getItems());
      }

      public void execute() {
        try {
          Source source = wizard.getSource();
          NodePathParser pathParser = new NodePathParser();
          Region [] detachPaths = source.getExtractRegion();
          HTMLExtractor extractor = new HTMLExtractor();

          NodePath[] extractPaths = null;
          if(detachPaths != null 
              && detachPaths.length > 0 
              && detachPaths[0] != null)  {
            extractPaths = pathParser.toNodePath(detachPaths[0].getPaths());
          }

          if(extractPaths != null && extractPaths.length > 0) {
            if(source.getExtractType() == ExtractType.ROW) {
              doc = extractor.extractFirst(document, extractPaths);
            } else {
              doc = extractor.extract(document, extractPaths);
            }
          }
        } catch (Exception e) {
          ClientLog.getInstance().setThrowable(null, e);
          this.message = e.toString();
        }
      }

      public void after() {
        if(message != null) {
          error(message);
          return;
        }
        clearMessage();
        if(doc != null) wizard.putTemp("extract.doc", doc.getTextValue());
        ChannelWizardComposite.showNext(wizard);
      }
    };

    WaitLoading waitLoading = new WaitLoading(this, excutor);
    waitLoading.open();
  }

  private void error(String error) {
    showError(error);
  }

  private void clearMessage() {
    clearInformation();
  }

}
