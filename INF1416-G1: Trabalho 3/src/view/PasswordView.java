package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import model.User;

public class PasswordView extends JFrame {

	private int width = 400;
	private int height = 500;

	private User user;

		private Node root = new Node("");
	private int numCliques = 0;

	public PasswordView(String viewTittle, User user) {
		this.user = user;
		//		DBManager.insereRegistro(3001, (String) user.get("email"));

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

		JLabel senhaLabel = new JLabel("Senha:");
		senhaLabel.setBounds(30, 100, 200, 50);
		content.add(senhaLabel);

		JTextField passwordField = new JTextField();
		passwordField.setEnabled(false);
		passwordField.setBounds(30, 150, 200, 50);
		content.add(passwordField);

		List<List<String>> opcoes = generateRandomOptions(2);

		List<JButton> listaButtons = new ArrayList<JButton>();

		for (int i=0; i<5; i++) {
			JButton senhaButton = new JButton(String.join(" ", opcoes.get(i)));
			senhaButton.setBounds(30 + (i * 65), 300, 60, 60);
			senhaButton.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					if (numCliques == 8) {
						System.out.println("Senha tem no max 8 numeros");
						return;
					}
					numCliques++;
					passwordField.setText(passwordField.getText() + "*");
					insereNosFolhas(root, ((JButton)e.getSource()).getText());

					generateRandomButtons(listaButtons);
				}
			});
			listaButtons.add(senhaButton);
			content.add(senhaButton);
		}		
		JButton loginButton = new JButton("Login");
		loginButton.setBounds(60, 400, 300, 50);
		content.add(loginButton);

		loginButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
	}	

	private void insereNosFolhas(Node root, String opcoes) {
//		if (root.dir == null && root.esq == null) {
//			root.esq = new Node(""+opcoes.charAt(0));
//			root.dir = new Node(""+opcoes.charAt(2));
//			return;
//		}
//		insereNosFolhas(root.dir, opcoes);
//		insereNosFolhas(root.esq, opcoes);
	}

	private void generateRandomButtons(List<JButton> buttons) {
		List<List<String>> options = generateRandomOptions(2);

		for (int i=0; i<buttons.size(); i++) {
			JButton button = buttons.get(i);
			List<String> option = options.get(i);
			String optionValue = String.join("\n", option);
			button.setText(optionValue);
		}
	}

	private List<List<String>> generateRandomOptions(int clusteringCount) {
		List<List<String>> options = new ArrayList<List<String>>();
		String numbers = "0123456789";

		for (int i=0; i < numbers.length()/clusteringCount; i++) {

			Random rand = new Random();
			List<String> option = new ArrayList<String>();

			// Gera o primeiro indice aleatorio
			int index = rand.nextInt(numbers.length());
			char selectedChar = numbers.charAt(index);
			String stringChar = String.valueOf(selectedChar);
			// Adiciona 
			option.add(stringChar);
			// Remove o selecionado dos possiveis
			numbers = numbers.replaceAll(stringChar, "");

			// Gera o segundo indice aleatorio
			index = rand.nextInt(numbers.length());
			selectedChar = numbers.charAt(index);
			stringChar = String.valueOf(selectedChar);
			// Adiciona 
			option.add(stringChar);

			// Remove o selecionado dos possiveis
			numbers = numbers.replaceAll(stringChar, "");

			options.add(option);
		}
		return options;
	}

}
