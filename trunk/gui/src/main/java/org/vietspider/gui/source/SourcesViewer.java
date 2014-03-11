package org.vietspider.gui.source;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.client.common.source.LocalSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.source.SourcesExplorer.SourceFilter;
import org.vietspider.gui.source.UISourcesExplorer.SelectAllGroup;
import org.vietspider.model.Group;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

public class SourcesViewer extends Composite implements SelectAllGroup {

  private SourcesExplorer sourceExplorer;
  private AddSourceListener listener;
  private Label lblStatus;
  private String addSuccessful = "";

  public SourcesViewer(Composite parent){
    this(parent, null);
  }
  
  public SourcesViewer(Composite parent, SourceFilter sourceFilter){
    super(parent, SWT.TITLE);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;		
    this.setLayout(gridLayout);

    ApplicationFactory factory = new ApplicationFactory(this, "Sources", getClass().getName());

    addSuccessful = factory.getLabel("add.successful");

    LocalSourceClientHandler sourcesHandler = new LocalSourceClientHandler();
    sourceExplorer = new SourcesExplorer(factory, 
        this, new int[]{170, 250}, sourcesHandler, sourceFilter);

    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 3;
    sourceExplorer.setLayoutData(gridData);
    sourceExplorer.setSelectAllGroup(this);
    sourceExplorer.loadGroups(null);

    Composite buttonComposite = new Composite(this, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL); 

    buttonComposite.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    buttonComposite.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(buttonComposite);

    factory.createButton("butAdd", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        NameConverter converter = new NameConverter();
        String [] sources = sourceExplorer.getSelectedSources();
        if(sources == null || sources.length < 1) {
          String[] categories = sourceExplorer.getCategories();
          for(String category : categories) {
            addCategory(category);
          }
        } else {
          addSource(sourceExplorer.getCategory(), sources);
        }
      }   
    }, factory.loadImage("butAdd.png"));   

    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        close();
      }   
    }, factory.loadImage("butClose.png"));   

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    lblStatus = new Label(this, SWT.BORDER);
    lblStatus.setFont(UIDATA.FONT_8T);
    lblStatus.setLayoutData(gridData);
  }

  private void close (){this.getShell().close();}

  private void addCategory(final String category) {
    Worker excutor = new Worker() {

      private String group;
      private String error;
      private String sourceValue;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        group = sourceExplorer.getGroup();
      }

      public void execute() {
        CrawlerClientHandler client = new CrawlerClientHandler();
        StringBuilder builder  = new StringBuilder();
        try {
          SourcesClientHandler sourceClient = new SourcesClientHandler(group);
          String [] sources = sourceClient.loadSources(category);
          for(int i = 0 ; i < sources.length; i++) {
            int idx = sources[i].indexOf('.');
            if(idx > -1) sources[i] = sources[i].substring(idx+1);
            if(sources[i].isEmpty()) continue;
            if(builder.length() > 0) builder.append('\n');      
            builder.append(sources[i]).append('.').append(group).append('.').append(category);
          }
          if(builder.length() < 1) return;
          sourceValue = builder.toString().trim();
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
        SourceFilter filter = sourceExplorer.getSourceFilter();
        if(filter != null) sourceExplorer.removeSelectedCategories();
        fireAddEvent(new CrawlSourceEvent(sourceValue));
        String text = group+"."+ category +"...";
        lblStatus.setText(addSuccessful.replace("$1", "\""+text+"\""));
      }
    };
    WaitLoading loading = new WaitLoading(sourceExplorer, excutor);
    loading.open();
  }

  public void selectAllGroup(final String [] groups) {
    Worker excutor = new Worker() {

      private String error;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        CrawlerClientHandler client = new CrawlerClientHandler();
        StringBuilder builder  = new StringBuilder();
        try {
          for(String group : groups) {
            if(group.equals(Group.DUSTBIN)) continue;
            SourcesClientHandler handler = new SourcesClientHandler(group);
            String [] categories = handler.loadCategories();

            for(String category : categories) {
              String [] sources = handler.loadSources(category);
              builder.setLength(0);
              for(int i = 0 ; i < sources.length; i++) {
                int idx = -1;
                if((idx  = sources[i].indexOf('.')) > -1) {
                  sources[i] = sources[i].substring(idx+1);
                }
                if(sources[i].isEmpty()) continue;
                if(builder.length() > 0) builder.append('\n');      
                builder.append(sources[i]).append('.').append(group).append('.').append(category);
              }
              if(builder.length() < 1) continue;
              client.addSourcesToLoader(builder.toString().trim(), true);
            }
          }
          client.addSourcesToLoader("-1", true);
        } catch (Exception exp) {
          error = exp.toString();
        }
      }

      public void after() {     
        if(error != null && error.length() > 0) {
          lblStatus.setText(error);
          return;
        }
        fireAddEvent(new CrawlSourceEvent("*"));
        lblStatus.setText(addSuccessful.replace("$1", ""));
      }
    };
    WaitLoading loading = new WaitLoading(sourceExplorer, excutor);
    loading.open();
  }


  private void addSource(final String category, final String [] sources) {
    Worker excutor = new Worker() {

      private String sourceValue;
      private String error;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        StringBuilder builder  = new StringBuilder();
        String group = sourceExplorer.getGroup();
        for(int i = 0 ; i < sources.length; i++) {
          int idx = -1;
          if((idx  = sources[i].indexOf('.')) > -1) sources[i] = sources[i].substring(idx+1);
          if(builder.length() > 0) builder.append('\n');      
          builder.append(sources[i]).append('.').append(group).append('.').append(category);
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
        SourceFilter filter = sourceExplorer.getSourceFilter(); 
        if(filter != null) sourceExplorer.removeSelectedSources();

        fireAddEvent(new CrawlSourceEvent(sourceValue));
        String text =  cutText(sourceValue);
        lblStatus.setText(addSuccessful.replace("$1", "\""+text+"\""));
      }
    };

    WaitLoading loading = new WaitLoading(sourceExplorer, excutor);
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

  public static class CrawlSourceEvent {      

    private String value;

    public CrawlSourceEvent(String value) {
      this.value = value;
    }

    public String getValue() { return value; }

  }

  public static interface AddSourceListener {
    public void add(CrawlSourceEvent event);
  }

}
