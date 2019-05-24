package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import controller.AuthenticationService;
import controller.DBManager;
import model.TreeNode;
import model.User;

public class PasswordView extends JFrame {

    private int width;
    private int height;

    private User user;

    private TreeNode root = new TreeNode("");

    // View elements
    private JLabel passwordLabel;
    private JTextField passwordText;
    private List<JButton> buttonList;

    // Control elements
    private int tries = 0;
    private String viewTittle = "";

    public PasswordView(String viewTittle, User user) {

        this.viewTittle = viewTittle;
        this.user = user;

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

        this.passwordLabel = new JLabel("Senha:");
        this.passwordLabel.setBounds(margin, margin, objectWidth, objectHeight);
        content.add(this.passwordLabel);

        int limitLabel = margin + objectHeight;

        this.passwordText = new JTextField();
        this.passwordText.setEnabled(false);
        this.passwordText.setBounds(margin, limitLabel, objectWidth, objectHeight);
        this.passwordText.setDisabledTextColor(Color.black);
        content.add(this.passwordText);

        int limitNumbers = margin*2 + objectHeight*2;
        int padding = 5;
        int numberWidth = (this.width - margin*2)/5 - padding;

        List<List<String>> opcoes = generateRandomOptions(2);

        buttonList = new ArrayList<JButton>();

        for (int i=0; i<5; i++) {
            JButton senhaButton = new JButton(String.join(" ", opcoes.get(i)));
            senhaButton.setBounds(margin + (i * (numberWidth + padding)), limitNumbers, numberWidth, numberWidth);
            senhaButton.addActionListener(this.numberClicked());
            buttonList.add(senhaButton);
            content.add(senhaButton);
        }

        int limitButton = margin*4 + objectHeight*3;

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(margin, limitButton, objectWidth, objectHeight);
        loginButton.addActionListener(this.loginClicked());
        content.add(loginButton);
    }

    public ActionListener numberClicked() {
        return new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                if (passwordText.getText().length() >= 8) {
                    JOptionPane.showMessageDialog(null, "Senha deve conter até no máximo 8 números.");
                    return;
                }
                passwordText.setText(passwordText.getText() + "*");

                root.insertLeafs(((JButton)e.getSource()).getText());

                generateRandomButtons(buttonList);
            }
        };
    }

    public ActionListener loginClicked() {

        return new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                DBManager.insereRegistro(3001);

                // Verifica se o usuario esta bloqueado
                if (user.isBlocked()) {
                    JOptionPane.showMessageDialog(null, "Usuário bloqueado por tentavias incorretas.");
                    return;
                }

                // Verifica tamanho da senha
                int size = passwordText.getText().length();
                if (size < 6 || size > 8) {
                    JOptionPane.showMessageDialog(null, "Senha deve conter de 6 a 8 números.");
                    cleanPassword();
                    return;
                }
                if (AuthenticationService.sharedInstance().checkPasswordTree(root, user, "")) {
                    DBManager.insereRegistro(3003);

                    new KeyView(viewTittle);

                    dispose();

                    DBManager.insereRegistro(3002);
                } else {
                    handleWrongTry();
                }


            }
        };
    }

    private void handleWrongTry() {
    	
        String triesString = Integer.toString(3 - this.tries);
        JOptionPane.showMessageDialog(null, "Senha incorreta!" + triesString + " Tentativas restantes.");

        // Incrementa numero de tentativas erradas
        this.tries += 1;

        if(this.tries == 1) {
            DBManager.insereRegistro(3004);
        }
        else if(this.tries == 2) {
            DBManager.insereRegistro(3005);
        }
        // Se for 3 ou mais bloqueia usuario por 2 minutos
        else if (this.tries >= 3) {
            DBManager.insereRegistro(3006);
            JOptionPane.showMessageDialog(null, "Senha incorreta! Terceira tentativa mal sucessedida, acesso bloqueado por 2 minutos.");
            try {
                user = DBManager.blockUserAccess(user);

                DBManager.insereRegistro(3007);
            } catch (ClassNotFoundException | ParseException e1) {
                e1.printStackTrace();
            }
            this.tries = 0;

            new LoginView("INF1416");
            dispose();

        } else {
   
        }
        cleanPassword();
    }

    private void cleanPassword() {
        this.root = new TreeNode("");
        this.passwordText.setText("");
    }

    private void generateRandomButtons(List<JButton> buttons) {
        List<List<String>> options = generateRandomOptions(2);

        for (int i=0; i<buttons.size(); i++) {
            JButton button = buttons.get(i);
            List<String> option = options.get(i);
            String optionValue = String.join("\n", option.get(0) + " " + option.get(1));
            button.setText(optionValue);
        }
    }

    private List<List<String>> generateRandomOptions(int clusteringCount) {
        List<List<String>> options = new ArrayList<List<String>>();
        String numbers = "0123456789";

        int numbersCount = numbers.length();
        for (int i=0; i < numbersCount/clusteringCount; i++) {

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
