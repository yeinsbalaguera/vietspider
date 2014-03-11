/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.gui.source;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.RemoteSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.source.SourcesViewer.AddSourceListener;
import org.vietspider.gui.source.SourcesViewer.CrawlSourceEvent;
import org.vietspider.model.Group;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

public class RemoteSourceSearcher2 extends Composite{ 

  private Text txtSearch; 
  private List listSources; 
  private Label lblStatus;
  private Combo cboGroupType;
  private String addSuccessful = "";
  private AddSourceListener listener;
  
  private SourcesHandler handlerViewer;
  private RemoteSourceClientHandler sourcesHandler = null;
  
  private String localGroup;
  private String localCategory;


  public RemoteSourceSearcher2(Composite parent, SourcesHandler handlerViewer_) {
    super(parent,SWT.TITLE);
    
    this.handlerViewer = handlerViewer_;
    
    try {
      sourcesHandler = new RemoteSourceClientHandler();
    } catch (Exception e) {
      ClientLog.getInstance().setException(this, e);
      sourcesHandler = null;
    }

    ApplicationFactory factory = new ApplicationFactory(this, "RemoteSources", getClass().getName());

    addSuccessful = factory.getLabel("add.successful");

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;    
    this.setLayout(gridLayout);

    Composite HeadComposite = new Composite(this,SWT.NONE);

    HeadComposite.setLayout(new GridLayout(2,false));
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    HeadComposite.setLayoutData(gridData);
    factory.setComposite(HeadComposite);

    factory.createLabel("search.lblGroupType");

    cboGroupType = factory.createCombo(SWT.DROP_DOWN | SWT.READ_ONLY);
    cboGroupType.setFont(UIDATA.FONT_9);

    cboGroupType.setVisibleItemCount(10);
    cboGroupType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cboGroupType.setEnabled(false);

    cboGroupType.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        listSources.removeAll();
      }
    });

    Composite SearchComposite = new Composite(this,SWT.NONE);
    SearchComposite.setLayout(new GridLayout(2,false));
    GridData griData = new GridData(GridData.FILL_HORIZONTAL);
    SearchComposite.setLayoutData(griData);
    factory.setComposite(SearchComposite);
    txtSearch = new Text(SearchComposite, SWT.BORDER | SWT.SINGLE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtSearch.setLayoutData(gridData);   

    txtSearch.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent evt) {
        if(evt.keyCode == 13)  search();
      }
    });
    txtSearch.setFont(UIDATA.FONT_9);

    factory.createButton("butSearch",new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt){
        search();
      }
    });

    listSources = factory.createList(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    listSources.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_BOTH);
    listSources.setLayoutData(gridData);

    //---------Tao composite cho hai nut button---------

    Composite buttonComposite = new Composite(this,SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    buttonComposite.setLayoutData(gridData);

    RowLayout rowlayout = new RowLayout();
    buttonComposite.setLayout(rowlayout);
    rowlayout.justify = true;

    factory.setComposite(buttonComposite);
    factory.createButton("butAdd", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addSource();
      }
    },factory.loadImage("butAdd.png"));   

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblStatus = new Label(this,SWT.BORDER);
    lblStatus.setFont(UIDATA.FONT_8T);
    lblStatus.setLayoutData(gridData);
  }

  private void addSource() {
    Worker excutor = new Worker() {

      private String [] selectedSources;
      private String error;
      private String group;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        selectedSources = listSources.getSelection();

        int idxGroup = cboGroupType.getSelectionIndex();
        group = cboGroupType.getItem(idxGroup);
      }

      public void execute() {
        if(group == null || selectedSources == null 
            || selectedSources.length < 1) return;

        try {
          for(String selectedSource : selectedSources) {
            String [] elements  = selectedSource.split("\\.");
            String name  = elements[1];
            String category  = elements[0];
            Source source = sourcesHandler.loadSource(group, category, name);
            if(localGroup != null) {
              source.setGroup(localGroup);
            } else {
              source.setGroup(group);
            }
            
            if(localCategory != null) {
              source.setCategory(localCategory);
            } else {
              source.setCategory(category);
            }
            new SourcesClientHandler(source.getGroup()).saveSource(source);
          }
        } catch (Exception exp) {
          error = exp.toString();
        }
      }

      public void after() {
        if(error != null && error.length() > 0) {
          lblStatus.setText(error);
          return;
        }
        
        if(group == null || selectedSources == null 
            || selectedSources.length < 1) return;
        StringBuilder builder  = new StringBuilder();
        builder.append('.').append(selectedSources[0]).append('.').append(group);
        String text = cutText(builder);
        lblStatus.setText(addSuccessful.replace("$1", "\""+text+"\""));
        if(handlerViewer != null) handlerViewer.update();
      }
    };

    WaitLoading loading = new WaitLoading(this, excutor);
    loading.open();
  }

  private void search() {
    Worker excutor = new Worker() {

      private String value;
      private String group;
      private String [] items;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        listSources.removeAll();
        value = txtSearch.getText();
        int idxGroup = cboGroupType.getSelectionIndex();
        group = cboGroupType.getItem(idxGroup);
      }

      public void execute() {
        value = value.trim().toLowerCase();
        try { 
          items = sourcesHandler.searchSourceByHost(group, value);
        } catch (Exception e) {
        }
      }

      public void after() {
        if(items != null) listSources.setItems(items);
      }
    };
    WaitLoading loading = new WaitLoading(listSources, excutor);
    loading.open();
  }

  public void loadGroups( final String defaultGroup) {
    Worker excutor = new Worker() {

      private Group [] groups; 
      private Exception exp;

      public void abort() {
        sourcesHandler.abort();
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        cboGroupType.removeAll();
      }

      public void execute() {
        try {
          groups = sourcesHandler.loadGroups().getGroups();
        }catch (Exception e) {
          exp = e;
        }
      }

      public void after() {
        if(exp != null) {
          ClientLog.getInstance().setMessage(getShell(), exp);
          return;
        }
        if(groups == null) return;
        
        for(Group groupEle : groups) {
          if(groupEle.getType().equals(Group.DUSTBIN)) continue;
          cboGroupType.add(groupEle.getType());
        }
        
        if(cboGroupType.getItemCount() < 1) return;
        cboGroupType.select(0);
        
        if(defaultGroup == null ) return;
        for(int i = 0; i < cboGroupType.getItemCount(); i++) {
          if(cboGroupType.getItem(i).equals(defaultGroup)) {
            cboGroupType.select(i);
            return;
          }
        }
      }
    };
    WaitLoading loading = new WaitLoading(cboGroupType, excutor);
    loading.open();
  }

  private String cutText(CharSequence seq){
    int i = 0;
    while(i < Math.min(25, seq.length())) {
      i++;
    }
    return seq.subSequence(0, i).toString()+"...";
  }

  public void fireAddEvent(CrawlSourceEvent e) {
    if(listener == null) return;
    listener.add(e);
  }

  public void addAddListener(AddSourceListener action) {      
    this.listener = action;
  }
  
  public void setLocalGroup(String localGroup) {
    if(sourcesHandler == null) return;
    this.localGroup = localGroup;
    loadGroups(localGroup);
  }

  public void setLocalCategory(String localCategory) {  
    this.localCategory = localCategory; 
  }

}

