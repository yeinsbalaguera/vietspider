package org.vietspider.common.util;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GridBagLayoutDemo extends JFrame {

	private static final long serialVersionUID = 1L;

	JButton bt1,bt2,bt3,bt4,bt5,bt6;
	JPanel mainPanel;
	Container container;
	GridBagLayout gbl;
	GridBagConstraints gbc;

	public GridBagLayoutDemo(){
		this.initComponent();
		this.setFrameOption("GridBagLayout Demo", 400, 300, true);
	}

	public void initComponent(){
		this.bt1 = new JButton("Button 1");
		this.bt2 = new JButton("Button 2");
		this.bt3 = new JButton("Button 3");
		this.bt4 = new JButton("Button 4");
		this.bt5 = new JButton("Button 5");
		this.bt6 = new JButton("Button 6");

		this.mainPanel = new JPanel();
		this.container = this.getContentPane();

		this.gbl = new GridBagLayout();
		this.gbc = new GridBagConstraints();

		this.mainPanel.setLayout(gbl);
    
		this.addComponent(this.bt1, 0, 0, 2, 1, GridBagConstraints.REMAINDER);
		this.gbc.weightx = 5.0;
		this.addComponent(this.bt2, 1, 1, 2, 1, GridBagConstraints.REMAINDER);
//		this.gbc.weightx = 1.0;
		this.addComponent(this.bt3, 2, 2, 2, 1, GridBagConstraints.REMAINDER);
//		this.gbc.weightx = 0.0;
		this.addComponent(this.bt4, 3, 3, 2, 1, GridBagConstraints.REMAINDER);
//		this.gbc.weightx = 1.0;
		this.addComponent(this.bt5, 4, 4, 2, 1, GridBagConstraints.REMAINDER);
//		this.addComponent(this.bt6, 9, 5, 1, 1, GridBagConstraints.REMAINDER);

		this.container.add(this.mainPanel);
		

	}

	public void addComponent(Component comp, int gx, int gy, int gwidth, int gheight, int fill) {
//	  this.gbc.fill = fill; 
		this.gbc.gridx = gx;
		
		this.gbc.gridy = gy;

		this.gbc.gridwidth = gwidth;
		this.gbc.gridheight= gheight;

		this.gbl.setConstraints(comp, gbc);
		this.mainPanel.add(comp);
	}

	public void setFrameOption(String title, int fwidth, int fheight, boolean visible) {
		this.setTitle(title);
		this.setSize(fwidth, fheight);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public static void main(String[] ben){
		new GridBagLayoutDemo();
	}
}
