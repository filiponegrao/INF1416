package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
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

    private JPasswordField passwordText;
    private JButton loginButton;

    private int tries = 0;

    private User user;

    public KeyView(String viewTittle) {

        this.viewTittle = viewTittle;
        // Define o titulo
        setTitle(this.viewTittle + ": Verificacao de credencial");

        user = AuthenticationService.sharedInstance().getUser();

        // Define o quit pelo botao
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Define as dimensoes do frame
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.width = (int) dimension.getWidth()/5;
        this.height = (int) (this.width * 1.2);
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
        this.passwordText = new JPasswordField();
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

    public void openFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./tokens/Keys/"));
        if(fileChooser.showOpenDialog(uploadButton) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getPath();
            uploadPath.setText(path);
        }
    }

    public ActionListener selectFile() {

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFolder();
            }
        };
    }

    public ActionListener loginClicked() {
        return new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                DBManager.insereRegistro(4001);

                // Verifica se o usuario esta bloqueado
                if (user.isBlocked()) {
                    JOptionPane.showMessageDialog(null, "Usuário bloqueado por tentavias incorretas.");
                    new LoginView("INF1416");
                    dispose();

                    DBManager.insereRegistro(4007);
                }

                String pass = new String(passwordText.getPassword());
                int size = pass.length();
                if (size == 0) {
                    JOptionPane.showMessageDialog(null, "Frase secreta não pode ser vazia");

                    DBManager.insereRegistro(4005);

                    return;
                }

                try {

                    AuthenticationService.sharedInstance().isPrivateKeyValid(pass, uploadPath.getText());

                    // Adiciona um acesso ao usuario
                    User user = AuthenticationService.sharedInstance().getUser();
                    DBManager.addUserAccess(user);

                    DBManager.insereRegistro(4003);

                    new NavigationView();
                    dispose();
                    DBManager.insereRegistro(4002);

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    String message = e1.getLocalizedMessage();
                    JOptionPane.showMessageDialog(null, message);
                    e1.printStackTrace();

                    handleWrongTry();
                }
            }
        };
    }

    private void handleWrongTry() {
        // Incrementa numero de tentativas erradas
        this.tries += 1;
        // Se for 3 ou mais bloqueia usuario por 2 minutos
        if (this.tries >= 3) {
            JOptionPane.showMessageDialog(null, "Senha incorreta! Terceira tentativa mal sucessedida, acesso bloqueado por 2 minutos.");
            try {
                user = DBManager.blockUserAccess(user);
            } catch (ClassNotFoundException | ParseException e1) {
                e1.printStackTrace();
            }
            this.tries = 0;

            new LoginView("INF1416");
            dispose();

        } else {
            String triesString = Integer.toString(3 - this.tries);
            JOptionPane.showMessageDialog(null, "Senha incorreta!" + triesString + " Tentativas restantes.");
        }
    }


}
