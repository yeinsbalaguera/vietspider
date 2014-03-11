package org.vietspider.ui.widget;

/*
 * Created on Dec 31, 2004
 *
 * TODO To change the template for this generated file go to
 */

import java.util.Properties;
import java.util.Vector;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.widget.action.DialogInputEvent;
import org.vietspider.ui.widget.action.DialogInputListener;

/**
 * @author thuannd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InputViewer {

  protected Text txtName = null;
  private org.eclipse.swt.widgets.Shell sShell;

  private Vector<DialogInputListener> listeners;	

  public InputViewer (Shell parent, Properties properties, String resources){		
    sShell = new org.eclipse.swt.widgets.Shell( parent);	
    sShell.setLocation( parent.getLocation().x+200, parent.getLocation().y+150);
    sShell.setSize(new org.eclipse.swt.graphics.Point(471,146));
    sShell.setLayout( null);

    listeners = new Vector<DialogInputListener>();

    ApplicationFactory factory = new ApplicationFactory(sShell, resources, getClass().getName());

    factory.createLabel( "lbl").setBounds(15, 15, 100, 20);		

    txtName = factory.createText();	
    txtName.setBounds( 120,15,280,20);
    txtName.addKeyListener( new KeyAdapter(){
      public void keyPressed(KeyEvent e){       
        if(e.keyCode != 13) return;   
        enter();
      }     
    }); 
    factory.createButton("but", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        enter();
      }
    }).setBounds( 180,50,90,20);

    sShell.setSize( 420, 120);		
    sShell.open();    
  }	

  public Shell getShell(){
    return this.sShell;
  }
//event
  public void fireEvent( DialogInputEvent e) {
    synchronized(listeners)  {
      for(int i = 0; i < listeners.size(); i++){
        try{
          DialogInputListener listener = listeners.get(i);         
          listener.entered(e);       
          break;
        }catch(Exception exp){               	  
        }       
      }
    }
  }
  public void addListener( DialogInputListener listener) {    	
    synchronized(listeners) {
      listeners.remove(listener);
      listeners.add(listener);
    }
  }
  public void removeListener( DialogInputListener listener) {
    synchronized(listeners) {
      listeners.remove(listener);
    }
  }
  public void selected( DialogInputEvent ev) {
    if(ev.getText() == null || ev.getText().length() == 0) return;    
    fireEvent(ev);	   
  }	
  private void enter(){    
    DialogInputEvent event = new DialogInputEvent(txtName.getText());
    sShell.dispose();
    fireEvent(event);   	
  }	
}
