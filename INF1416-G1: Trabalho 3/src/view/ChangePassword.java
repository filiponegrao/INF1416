package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import controller.AuthenticationService;
import controller.CertifyService;
import controller.DBManager;

public class ChangePassword extends JPanel {

    private JLabel uploadLabel;
    private JLabel uploadPath;
    private JButton uploadButton;
    private JButton backButton;
    private JButton confirmationButton;

    private JPasswordField passwordText;
    private JPasswordField passwordConfirmationText;

    private String path = "";

    private NavigationView navigation;

    public ChangePassword(NavigationView navigation, int x, int y, int w, int h) {
        this.navigation = navigation;
        this.setBounds(x, y, w, h);

        setLayout(null);

        int margin = 20;
        int objectWidth = w-(margin*2);
        int objectHeight = 30;

        // Label username
        this.uploadLabel = new JLabel("Faça o upload do seu certificado");
        this.uploadLabel.setBounds(margin, margin, objectWidth, objectHeight);
        this.add(this.uploadLabel);

        int pathOrigin = margin + objectHeight;

        // Label username
        this.uploadPath = new JLabel("Caminho não especifcado.");
        this.uploadPath.setBounds(margin, pathOrigin, objectWidth, objectHeight);
        this.add(this.uploadPath);

        int uploadOrigin = pathOrigin + objectHeight;

        // Uploado key button
        this.uploadButton = new JButton("Selecionar arquivo");
        this.uploadButton.setBounds(margin, uploadOrigin, objectWidth, objectHeight);
        this.uploadButton.addActionListener(this.selectFile());
        this.add(this.uploadButton);

        int passwordLabelOrigin = uploadOrigin + objectHeight + margin;
        JLabel label1 = new JLabel("<html>Senha deve conter apenas númerais de 0 a 9<br> e deve possuir tamanho entre 6 a 8 caracteres:</html>");
        label1.setBounds(margin, passwordLabelOrigin, objectWidth, objectHeight);
        this.add(label1);

        int passwordOrigin = passwordLabelOrigin + objectHeight + margin;
        this.passwordText = new JPasswordField();
        this.passwordText.setBounds(margin, passwordOrigin, objectWidth, objectHeight);
        this.add(this.passwordText);

        int passwordLabe2lOrigin = passwordOrigin + objectHeight;
        JLabel label2 = new JLabel("Confirme a senha:");
        label2.setBounds(margin, passwordLabe2lOrigin, objectWidth, objectHeight);
        this.add(label2);

        int password2Origin = passwordLabe2lOrigin + objectHeight;
        this.passwordConfirmationText = new JPasswordField();
        this.passwordConfirmationText.setBounds(margin, password2Origin, objectWidth, objectHeight);
        this.add(this.passwordConfirmationText);

        int buttonOrigin = h - objectHeight - margin;
        // Botao de voltar
        this.backButton = new JButton("Voltar");
        this.backButton.setBounds(margin, buttonOrigin, objectWidth/2, objectHeight);
        this.backButton.addActionListener(this.back());
        this.add(this.backButton);

        this.confirmationButton = new JButton("Alterar");
        this.confirmationButton.setBounds(objectWidth/2 + margin, buttonOrigin, objectWidth/2, objectHeight);
        this.confirmationButton.addActionListener(this.checkChangePassowrd());
        this.add(this.confirmationButton);

        this.setBackground(Color.white);

        DBManager.insereRegistro(7001);
    }



    private void cleanPassword() {
        this.passwordText.setText("");
        this.passwordConfirmationText.setText("");
    }

    public ActionListener checkChangePassowrd() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Verifica tamanho da senha
                String text = new String(passwordText.getPassword());
                int size = text.length();
                // Verifica se nao foi alterada
                if (size == 0) {
                    String textConfirmation = new String(passwordText.getPassword());
                    int sizeConfirmation = textConfirmation.length();
                    if (sizeConfirmation != size) {
                        JOptionPane.showMessageDialog(null, "Senhas devem ser preenchidas se forem modificadas.");
                        return;
                    }

                } else if (size < 6 || size > 8) {
                    JOptionPane.showMessageDialog(null, "Senha deve conter de 6 a 8 números.");
                    cleanPassword();
                    return;
                }
                // Verifica se as senhas correspondem
                String original = new String(passwordText.getPassword());
                String confirmation = new String(passwordConfirmationText.getPassword());

                if (!original.equals(confirmation)) {
                    JOptionPane.showMessageDialog(null, "Senhas não correspondem.");
                    cleanPassword();

                    DBManager.insereRegistro(7002);

                    return;
                }

                updateUser(original, path);
            }
        };
    }

    public ActionListener back() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DBManager.insereRegistro(7006);
                navigation.selectMenu();
            }
        };
    }

    public ActionListener selectFile() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("./tokens/Keys/"));

                if(fileChooser.showOpenDialog(uploadButton) == JFileChooser.APPROVE_OPTION) {
                    path = fileChooser.getSelectedFile().getPath();
                    uploadPath.setText(path);
                }
                else {
                    DBManager.insereRegistro(7003);
                }
            }
        };
    }

    private void updateUser(String password, String certPath) {

        X509Certificate certificate = null;
        try {
            if (!certPath.isEmpty()) {
                certificate = CertifyService.sharedInstance().getCertificate(certPath);
            }
            AuthenticationService.sharedInstance().updateUser(password, certificate);
            JOptionPane.showMessageDialog(null, "Usuário atualizado com sucesso!");
            this.cleanAll();

            DBManager.insereRegistro(7004);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao ler o certificado.");
            DBManager.insereRegistro(7005);
        }

        AuthenticationService.sharedInstance().updateUser(password, certificate);

    }

    private void cleanAll() {
        this.passwordConfirmationText.setText("");
        this.passwordText.setText("");
        this.path = "";
        this.uploadPath.setText("");
    }




}
