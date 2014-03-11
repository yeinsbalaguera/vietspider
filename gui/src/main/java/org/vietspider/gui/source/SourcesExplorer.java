package org.vietspider.gui.source;

import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.vietspider.client.common.ISourceHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.waiter.WaitLoading;

public class SourcesExplorer extends UISourcesExplorer {
  
  private SourceFilter sourceFilter;

//  private boolean autoSelect = true;

//  public SourcesExplorer(ApplicationFactory factory, 
//      Composite parent, int[] sashSplit, /* boolean auto,*/ ISourceHandler iSourceHandler) {
//    super(factory, parent, sashSplit, iSourceHandler);
////    autoSelect = auto;
//  }
  
  public SourcesExplorer(ApplicationFactory factory,
      Composite parent, int[] sashSplit, ISourceHandler iSourceHandler) {
   this(factory, parent, sashSplit, iSourceHandler, null); 
  }

  public SourcesExplorer(ApplicationFactory factory,
      Composite parent, int[] sashSplit, ISourceHandler iSourceHandler, SourceFilter sourceFilter) {
    super(factory, parent, sashSplit, iSourceHandler);
    this.sourceFilter = sourceFilter;
//    loadCategory(0);
  }

  public void loadCategory(final int time){
    final String group_ = group;
    Worker excutor = new Worker() {

      private String [] categories; 
      private Exception exp = null;

      public void abort() { iSourceHandler.abort(); }
      public void before() {}

      public void execute() {
        try {
          categories = iSourceHandler.loadCategories(group_);
        } catch (Exception e) {
          exp = e;
        }
      }

      public void after() {
//        System.out.println(" ================= >"+time);
        if(exp != null) {
          if(time < 3) {
            loadCategory(time+1);
            return;
          }
          ClientLog.getInstance().setMessage(uiSources.getShell(), exp);
          return;
        }

        try {
          if(categories == null) categories = new String[0];
          setDomain(categories);
        } catch(Exception exp1) {
          ClientLog.getInstance().setException(getShell(), exp1);
        }
        int selectedIndex = -1;
        
        int idxGroup = cboGroupType.getSelectionIndex();
        if(idxGroup > -1 && idxGroup < cboGroupType.getItemCount()) {
          String selectedGroup = cboGroupType.getItem(idxGroup);
          if(!selectedGroup.equals(group_)) {
            group = selectedGroup;
            loadCategory(0);
            return;
          }
        }
        
        Preferences prefs = Preferences.userNodeForPackage(UISourcesExplorer.class);
        try {
          selectedIndex = Integer.parseInt(prefs.get("selectedDomain", "0"));
          if(selectedIndex < 0 && selectedIndex >= uiCategories.getItemCount()) return;
          uiCategories.select(selectedIndex);
          loadSources(0);
        } catch (Exception e)  {
          //e.printStackTrace();
        }
      }
    };
    WaitLoading loading = new WaitLoading(this, excutor);
    loading.open();
  }	

  public void setDomain(String [] categories) {		
//    int iselect = uiCategories.getSelectionIndex();
    uiCategories.removeAll();
    for( int i=0; i<categories.length; i++){
      if(categories[i] == null) continue;
      uiCategories.add(categories[i]);
    }
//    if(autoSelect && (iselect >= categories.length || iselect < 0)) iselect = 0;
//    uiCategories.select(iselect);
  }

  void removeSource(){
    MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    ClientRM resource = new ClientRM("Sources");
    msg.setMessage(resource.getLabel(getClass().getName()+".msgAlertDeleteAgent"));
    if(msg.open() != SWT.YES) return ;		   
    try{
      iSourceHandler.deleteSources(group, getCategory(), uiSources.getSelection());
      loadSources(0);
    }catch(Exception exp){
      ClientLog.getInstance().setException(getShell(), exp);
    }		
  }

  void removeCategory(){
    MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    ClientRM resource = new ClientRM("Sources");
    msg.setMessage(resource.getLabel( this.getClass().getName()+".msgAlertDeleteCategory"));
    if(msg.open() != SWT.YES) return;		
    try{
      iSourceHandler.deleteCategories(group, uiCategories.getSelection());
      loadCategory(0);
    }catch(Exception exp){
      ClientLog.getInstance().setException(getShell(), exp);
    }

  }

  public void setSource(String [] sources) {
    if(sources == null) return;
    uiSources.removeAll();
    for(String ele : sources){
      if(ele.indexOf('.') < 0) continue;  
      if(sourceFilter != null) {
        if(!sourceFilter.accept(group + "." + ele)) continue;
      }
      uiSources.add(ele.substring(ele.indexOf('.')+1));      
    }
  }

  public void selectSource(String name){
    uiSources.setSelection(new String[]{name});
  }

  public void selectDomain(String domain, String source){
    for(int i = 0; i<uiCategories.getItemCount(); i++){
      if(uiCategories.getItem(i).equals(domain)){
        uiCategories.setSelection(i);
        break;
      }
    }
    loadSources(0);
//  int idx = uiSources.getSelectionIndex();
    for(int i = 0; i<uiSources.getItemCount(); i++){
      if(uiSources.getItem(i).equals(source)){
        uiSources.setSelection(i);
        break;
      }
    }
    selectSource();
  }

  public void loadSource(final String category, final int time){    
    Worker excutor = new Worker() {

      private String [] sources; 
      private Exception exp = null;

      public void abort() { iSourceHandler.abort(); }
      public void before() {}

      public void execute() {
        try {
          sources = iSourceHandler.loadSources(group, category);
        }catch (Exception e) {
          exp = e;
        }
      }

      public void after() {
        if(exp != null) {
          if(time < 3) {
            loadSource(category, time+1);
            return;
          }
          ClientLog.getInstance().setMessage(uiSources.getShell(), exp);
          return;
        }
        if(sources == null) {
          if(time < 3) {
            loadSource(category, time+1);
            return;
          }
          return;
        }
        setSource(sources);
        if(uiSources.getItemCount() < 1) uiCategories.remove(category);
      }
    };
    WaitLoading loading = new WaitLoading(this, excutor);
    loading.open();
  }

  void loadSources(int time){
    if(uiCategories.getSelectionIndex() > -1) {
      loadSource(uiCategories.getSelection()[0], time);
    }
  }
  
  public static abstract class SourceFilter {
    @SuppressWarnings("unused")
    public boolean accept(String group, String category, String name) {
      return true;
    }
    @SuppressWarnings("unused")
    public boolean accept(String fullName) {
      return true;
    }
  }

  public SourceFilter getSourceFilter() { return sourceFilter; }

}
