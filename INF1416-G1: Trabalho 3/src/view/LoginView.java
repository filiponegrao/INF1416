package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class LoginView extends JFrame {

	private int width = 400;
	private int height = 500;
		
	public LoginView(String viewTittle) {
		// Define o titulo
		setTitle(viewTittle + ": Login");
		
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
		Container content = getContentPane();
		this.setLayout(null);
		int margin = 20;
		int objectWidth = this.width-(margin*2);
		int objectHeight = 30;
		
		// Label username
		JLabel usernameLabel = new JLabel("username:");
		usernameLabel.setBounds(margin, margin+50, objectWidth, objectHeight);
		content.add(usernameLabel);
		
		// Textfield username
		JTextField usernameText = new JTextField();
		usernameText.setBounds(margin, usernameLabel.getBounds().y + usernameLabel.getBounds().height + margin, objectWidth, objectHeight);
		usernameText.setToolTipText("email@dominio.com");
		content.add(usernameText);

		// Botao de verificacao
		JButton loginButton = new JButton("Verificar credencial");
		loginButton.setBounds(margin, usernameText.getBounds().y + usernameText.getBounds().height + margin, objectWidth, objectHeight);
		loginButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				System.out.println("SHOW");
				dispose();
			}
		});
		content.add(loginButton);
		
		// Mostra
		this.setVisible(true);
	}
	
}
