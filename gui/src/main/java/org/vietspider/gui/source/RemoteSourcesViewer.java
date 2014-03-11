package org.vietspider.gui.source;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.RemoteSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

public class RemoteSourcesViewer  {

  private Shell shell;
  private SourcesExplorer list;
  private Label lblStatus;
  private String addSuccessful = "";
  
  private String localGroup;
  private String localCategory;
  
  private SourcesHandler handlerViewer;
  private RemoteSourceClientHandler sourcesHandler = null;
  
  public RemoteSourcesViewer(Shell parent) {
    this(parent, null);
  }

  public RemoteSourcesViewer(Shell parent, SourcesHandler handlerViewer_) {
    this.handlerViewer = handlerViewer_;
    shell = new org.eclipse.swt.widgets.Shell(parent, SWT.TITLE);
    shell.setLocation( parent.getLocation().x+430, parent.getLocation().y+100);   
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;		
    shell.setLayout(gridLayout);

    ApplicationFactory factory = new ApplicationFactory(shell, "Sources", getClass().getName());
    
    addSuccessful = factory.getLabel("add.successful");

    try {
      sourcesHandler = new RemoteSourceClientHandler();
    } catch (Exception e) {
      ClientLog.getInstance().setException(shell, e);
      sourcesHandler = null;
    }
    
    list = new SourcesExplorer(factory, shell, new int[]{170, 250}, sourcesHandler);
    list.getListSource().addMouseListener(new MouseAdapter(){
      @SuppressWarnings("unused")
      public void mouseDoubleClick(MouseEvent e){
        try {
        } catch( Exception exp) {
          ClientLog.getInstance().setException(shell, exp);
        }
      }
    });
    list.disableAddAll();
    list.disableMenu();
    
    list.getListDomain().addMouseListener(new MouseAdapter(){
      @SuppressWarnings("unused")
      public void mouseDoubleClick(MouseEvent e){
        add() ;
      }
    });
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 3;
    list.setLayoutData(gridData);

    Composite buttonComposite = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);    
    buttonComposite.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    buttonComposite.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(buttonComposite);

    Button button = factory.createButton("butAdd", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        add();
      }   
    }, factory.loadImage("butAdd.png"));   
    button.setEnabled(sourcesHandler != null);

    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        shell.close();
        shell.dispose();
      }   
    }, factory.loadImage("butClose.png"));   
    
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    lblStatus = new Label(shell, SWT.BORDER);
    lblStatus.setFont(UIDATA.FONT_8T);
    lblStatus.setLayoutData(gridData);

    shell.setSize(400, 420);		
    shell.open();
  }
  
  private void add() {
    String [] sources = list.getSelectedSources();
    if(sources == null || sources.length < 1) {
      String [] categories = list.getCategories();
      for(String category : categories) {
        addCategory(category);
      }
    } else {
      String category = list.getCategory();
      addSource(category, sources);
    }
  }
  
  private void addCategory(final String category) {
    Worker excutor = new Worker() {

      private String group;
      private String error;
      private String [] sources;

      public void abort() {
        sourcesHandler.abort();
      }

      public void before() {
        group = list.getGroup();
      }

      public void execute() {
        try {
          sources = sourcesHandler.loadSources(group, category);
        } catch (Exception exp) {
          error = exp.toString();
        }
      }

      public void after() {
        if(error != null && error.length() > 0) {
          lblStatus.setText(error);
          return;
        }
        if(sources == null || sources.length < 1) return;
        addSource(category, sources);
      }
    };
    WaitLoading loading = new WaitLoading(list, excutor);
    loading.open();
  }

  
  private void addSource(final String category, final String [] names) {
    Worker excutor = new Worker() {

      private StringBuilder builder  = new StringBuilder();
      private String error;
      private String group;

      public void abort() {
        sourcesHandler.abort();
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        group = list.getGroup();
      }

      public void execute() {
        if(group == null || category == null 
            || names == null || names.length < 1) return;
        
        try {
          for(String name : names) {
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
        if(category == null || group == null || names == null) return;
        if(names.length > 0) builder.append(names[0]);
        builder.append('.').append(category).append('.').append(group);
        String text = cutText(builder);
        lblStatus.setText(addSuccessful.replace("$1", "\""+text+"\""));
        if(handlerViewer != null) handlerViewer.update();
      }
    };
    WaitLoading loading = new WaitLoading(list, excutor);
    loading.open();
  }
  
  private String cutText(CharSequence seq){
    int i = 0;
    while(i < Math.min(25, seq.length())) {
      i++;
    }
    return seq.subSequence(0, i).toString()+"...";
  }


  public void setLocalGroup(String localGroup) {
    if(sourcesHandler == null) return;
    this.localGroup = localGroup;
    list.selectGroup(localGroup);
    list.disableGroupType();
  }

  public void setLocalCategory(String localCategory) {  
    this.localCategory = localCategory; 
  }

}
