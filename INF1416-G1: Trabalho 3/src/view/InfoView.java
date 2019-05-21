package view;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoView extends JPanel {

	private JLabel labelInfo;
	
	public InfoView(int x, int y, int w, int h, String info) {
		this.setBounds(x, y, w, h);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(null);

		int margin = 20;
		int objectWidth = w-(margin*2);
		int objectHeight = h;
		
		this.labelInfo = new JLabel(info);
		this.labelInfo.setBounds(margin, 0, objectWidth, objectHeight);
		this.add(this.labelInfo);
	}
	
	public void setText(String text) {
		this.labelInfo.setText(text);
	}
	
}
