package org.vietspider.gui.source;

import java.util.prefs.Preferences;

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
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.client.common.ISourceHandler;
import org.vietspider.client.common.source.LocalSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.source.SourcesViewer.AddSourceListener;
import org.vietspider.gui.source.SourcesViewer.CrawlSourceEvent;
import org.vietspider.model.Group;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

public class SourceSearcher2 extends Composite{ 

  private Text txtSearch;	
  private List listSources;	
  private Label lblStatus;
  private Combo cboGroupType;
  protected ISourceHandler iSourceHandler;
  private String addSuccessful = "";
  private AddSourceListener listener;

  public SourceSearcher2(Composite parent) {
    super(parent,SWT.TITLE);

    ApplicationFactory factory = new ApplicationFactory(this, "Sources", getClass().getName());

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
    if(Application.LICENSE == Install.PERSONAL) listSources.setEnabled(false);
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

    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        closed();
      }   
    }, factory.loadImage("butClose.png"));   

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblStatus = new Label(this,SWT.BORDER);
    lblStatus.setFont(UIDATA.FONT_8T);
    lblStatus.setLayoutData(gridData);

    iSourceHandler = new LocalSourceClientHandler();	    
    loadGroups(null);
  }

  private void closed(){this.getShell().close();}


  private void addSource() {
    Worker excutor = new Worker() {

      private String sourceValue;
      private String error;
      private String group;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        StringBuilder builder = new StringBuilder();
        String [] selectedSource = listSources.getSelection();

        int idxGroup = cboGroupType.getSelectionIndex();
        group = cboGroupType.getItem(idxGroup);

        for (int i =0; i < selectedSource.length; i++){
          if(builder.length() >0) builder.append('\n');
          String [] elements = selectedSource[i].split("\\."); 
          builder.append(elements[1]).append(".").append(group).append(".").append(elements[0]);
        }
        sourceValue = builder.toString().trim();
      }

      public void execute() {
        try {
          CrawlerClientHandler client = new CrawlerClientHandler();
          client.addSourcesToLoader(sourceValue, false);
        } catch (Exception exp) {
          error = exp.toString();
        }
      }

      public void after() {
        if(error != null && error.length() > 0) {
          lblStatus.setText(error);
          return;
        }

        fireAddEvent( new CrawlSourceEvent(sourceValue));
        String text =  cutText(sourceValue);
        lblStatus.setText(addSuccessful.replace("$1", "\""+text+"\""));
      }
    };

    WaitLoading loading = new WaitLoading(this, excutor);
    loading.open();
  }

  private void search() {
    listSources.removeAll();
    Worker excutor = new Worker() {

      private String value;
      private String group;
      private String [] items;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        value = txtSearch.getText();
        int idxGroup = cboGroupType.getSelectionIndex();
        group = cboGroupType.getItem(idxGroup);
      }

      public void execute() {
        value = value.trim().toLowerCase();
        try { 
          items = new SourcesClientHandler(group).searchSourceByHost(value);
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
        iSourceHandler.abort();
      }

      public void before() {
        cboGroupType.removeAll();
      }

      public void execute() {
        try {
          groups = iSourceHandler.loadGroups().getGroups();
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
        if(defaultGroup != null ) {
          for(int i = 0; i < cboGroupType.getItemCount(); i++) {
            if(cboGroupType.getItem(i).equals(null)) {
              cboGroupType.select(i);
              return;
            }
          }
        } else {
          Preferences prefs = Preferences.userNodeForPackage(SourcesExplorer.class);
          int selectedGroup = 0;
          
          try {
            String value  = prefs.get("selectedGroup", "1");
            if(value == null || value.trim().isEmpty()) value = "1";
            selectedGroup = Integer.parseInt(value);
          } catch (Exception e) {
            selectedGroup = 0;
//            ClientLog.getInstance().setMessage(cboGroupType.getShell(), e);
          }
          
          try {
            if(selectedGroup > -1 && selectedGroup < cboGroupType.getItemCount()) {
              cboGroupType.select(selectedGroup);
            } else if(cboGroupType.getItemCount() > 0) {
              cboGroupType.select(0);
            }
          } catch (Exception e) {
            ClientLog.getInstance().setMessage(cboGroupType.getShell(), e);
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


}

