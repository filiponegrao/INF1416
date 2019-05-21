package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import controller.AuthenticationService;
import controller.DBManager;
import model.User;

public class KeyView extends JFrame {
	
	private int width = 400;
	private int height = 500;
		
	private String viewTittle;
	
	// View elements
	private JLabel uploadLabel;
	private JLabel uploadPath;
	private JButton uploadButton;
	private JLabel passwordLabel;
	private JTextField passwordText;
	private JButton loginButton;
	
	public KeyView(String viewTittle) {
		this.viewTittle = viewTittle;
		// Define o titulo
		setTitle(this.viewTittle + ": Verificacao de credencial");
		
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
		this.uploadLabel = new JLabel("Faça o upload da sua chave privada");
		this.uploadLabel.setBounds(margin, margin, objectWidth, objectHeight);
		content.add(this.uploadLabel);
		
		int pathOrigin = margin + objectHeight;
		
		// Label username
		this.uploadPath = new JLabel("Caminho não especifcado.");
		this.uploadPath.setBounds(margin, pathOrigin, objectWidth, objectHeight);
		content.add(this.uploadPath);
		
		int buttonOrigin = pathOrigin + objectHeight + margin;
		
		// Uploado key button
		this.uploadButton = new JButton("Selecionar arquivo");
		this.uploadButton.setBounds(margin, buttonOrigin, objectWidth, objectHeight);
		this.uploadButton.addActionListener(this.selectFile());
		content.add(this.uploadButton);
		
		
		int labelOrigin = buttonOrigin + objectHeight + margin;
		
		this.passwordLabel = new JLabel("Frase secreta:");
		this.passwordLabel .setBounds(margin, labelOrigin, objectWidth, objectHeight);
		content.add(this.passwordLabel);
		
		int textOrigin = labelOrigin + objectHeight + margin;
		
		// Textfield username
		this.passwordText = new JTextField();
		this.passwordText.setBounds(margin, textOrigin, objectWidth, objectHeight);
		content.add(this.passwordText);
		
		int loginOrigin = textOrigin + objectHeight + margin;
		
		this.loginButton = new JButton("Confirmar");
		this.loginButton.setBounds(margin, loginOrigin, objectWidth, objectHeight);
		this.loginButton.addActionListener(this.loginClicked());
		content.add(loginButton);
		
		// Mostra
		this.setVisible(true);
	}
	
	public ActionListener selectFile() {
		 
		 return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JFileChooser fileChooser = new JFileChooser();
				 if(fileChooser.showOpenDialog(uploadButton) == JFileChooser.APPROVE_OPTION) {
						String path = fileChooser.getSelectedFile().getPath();
					 	uploadPath.setText(path);
				 }	 				
			}
		};
	}
	
	public ActionListener loginClicked() {
		return new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				int size = passwordText.getText().length();
				if (size == 0) {
					JOptionPane.showMessageDialog(null, "Frase secreta não pode ser vazia");
					return;
				}
				
				try {
					if (AuthenticationService.sharedInstance().isPrivateKeyValid(passwordText.getText(), uploadPath.getText()) ) {
						
						// Adiciona um acesso ao usuario
						User user = AuthenticationService.sharedInstance().getUser();
						DBManager.addUserAccess(user);
						new NavigationView();
						dispose();

					} else {
						JOptionPane.showMessageDialog(null, "Arquivo binário incorreto!");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Problema na leitura do arquivo");
					e1.printStackTrace();
				}
			}
		};
	}

}
