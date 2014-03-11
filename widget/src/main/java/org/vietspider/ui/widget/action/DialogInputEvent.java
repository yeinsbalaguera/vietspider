package org.vietspider.ui.widget.action;

public class DialogInputEvent {
	private String text;
	
	public DialogInputEvent(String d){
		this.text = d;
	}
	public String getText(){
		return this.text;
	}
	public void setDomain(String d){
		this.text = d;
	}
}
