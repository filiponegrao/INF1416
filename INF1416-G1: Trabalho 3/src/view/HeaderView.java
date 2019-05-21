package view;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import controller.AuthenticationService;
import model.User;

public class HeaderView extends JPanel {
	
	private JLabel labelEmail;
	private JLabel labelGroup;
	private JLabel labelName;

	public HeaderView(int x, int y, int w, int h) {
		this.setBounds(x, y, w, h);

		User user = AuthenticationService.sharedInstance().getUser();
		
		setLayout(null);

		int margin = 20;
		int objectWidth = w-(margin*2);
		int objectHeight = 30;
		
		this.labelEmail = new JLabel("Login: " + user.getEmail());
		this.labelEmail.setBounds(margin, margin, objectWidth, objectHeight);

		this.add(this.labelEmail);
		
		int grouporigin = margin + objectHeight;
		
		this.labelGroup = new JLabel("Grupo: " + user.getGroupName());
		this.labelGroup.setBounds(margin, grouporigin, objectWidth, objectHeight);
		this.add(this.labelGroup);
		
		int nameorigin = grouporigin + objectHeight;

		this.labelName = new JLabel("Nome: " + user.getName());
		this.labelName.setBounds(margin, nameorigin, objectWidth, objectHeight);
		this.add(this.labelName);
		
		this.setBackground(Color.LIGHT_GRAY);
	}
	
}
