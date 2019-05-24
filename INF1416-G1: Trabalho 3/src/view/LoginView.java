package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
//import javax.swing.text.PasswordView;
import javax.swing.JTextPane;

import controller.AuthenticationService;
import controller.DBManager;
import model.User;

public class LoginView extends JFrame {

    private int width = 400;
    private int height = 500;

    private String viewTittle;

    public LoginView(String viewTittle) {
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

        // email temporario
        JTextPane email = new JTextPane();
        email.setText("admin@inf1416.puc-rio.br");
        email.setBounds(margin, margin, objectWidth, objectHeight);
        email.setEnabled(true);
        //              content.add(email);

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
        loginButton.addActionListener(this.checkCredentials(usernameText));
        content.add(loginButton);

        // Mostra
        this.setVisible(true);
    }

    private ActionListener checkCredentials(JTextField usernameText) {
        return new ActionListener () {
            public void actionPerformed (ActionEvent e) {

                String text = usernameText.getText();
                
                DBManager.insereRegistro(2001, text);

                // Assertivas
                if (text.equals("")) {
                    JOptionPane.showMessageDialog(null, "E-mail não pode ser vazio.");
                    return;
                } else if (!text.contains("@")) {
                    JOptionPane.showMessageDialog(null, "E-mail inválido");
                    return;
                } else if (text.contains("\"") || text.contains("'")) {
                    JOptionPane.showMessageDialog(null, "Caracteres suspeitos! Sistema anti-sql-injection");
                    return;
                }

                User user = null;
                try {
                    user = AuthenticationService.sharedInstance().verifyEmail(text);
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (user == null) {
                    JOptionPane.showMessageDialog(null, "Usuário não encontrado");

                    DBManager.insereRegistro(2005, text);

                    return;
                }

                // Verifica se o usuario esta bloqueado
                if (user.isBlocked()) {
                    JOptionPane.showMessageDialog(null, "Usuário bloqueado por tentavias incorretas.");

                    return;
                }

                DBManager.insereRegistro(2003);

                PasswordView passwordView = new PasswordView(viewTittle + ": Verificação de senha", user);
                passwordView.setVisible(true);

                dispose();
                DBManager.insereRegistro(2002);
            }
        };
    }

}
