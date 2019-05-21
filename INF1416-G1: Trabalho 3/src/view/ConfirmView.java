package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ConfirmView extends JFrame {

	private int width = 0;
	private int height = 0;
	
	private JLabel label;

	private CreateUserView createUserView;
	
	public ConfirmView(CreateUserView createUserView, String text) {
		this.createUserView = createUserView;
		setTitle("Confirma os dados?");
		
		// Define o quit pelo botao
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Define as dimensoes do frame
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = (int) dimension.getWidth()/5;
		this.height = this.width;
		this.setSize (this.width, this.height);
		this.setResizable(false);

		// Define a posicao
		int x = (int) ((dimension.getWidth() - this.width) / 2);
		int y = (int) ((dimension.getHeight() - this.height) / 2);
		this.setLocation(x, y);
		
		// Define variaveis visuais
		Container content = this.getContentPane();
		this.setLayout(null);
		int margin = 20;
		int objectWidth = this.width-(margin*2);
		int objectHeight = 30;
		
		int buttonOrigin = this.height - objectHeight - margin*2;
		
		JButton cancelButton = new JButton("Cancelar");
		cancelButton.setBounds(margin, buttonOrigin, objectWidth/2, objectHeight);
		cancelButton.addActionListener(this.cancel());
		content.add(cancelButton);
		
		JButton okButton = new JButton("Ok");
		okButton.setBounds(objectWidth/2, buttonOrigin, objectWidth/2, objectHeight);
		okButton.addActionListener(this.ok());
		content.add(okButton);
		
		int labelHeight = this.height - margin*2 - objectHeight;
		
		this.label = new JLabel(text);
		this.label.setBounds(margin, margin, objectWidth, labelHeight);
		content.add(this.label);

		this.setVisible(true);
	}
	
	public ActionListener ok() {
		 return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createUserView.createUser();
				dispose();		
			}
		};
	}
	
	public ActionListener cancel() {
		 return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();		
			}
		};
	}
}
