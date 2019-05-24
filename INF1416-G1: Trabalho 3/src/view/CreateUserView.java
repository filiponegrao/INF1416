package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import controller.AuthenticationService;
import controller.CertifyService;
import controller.DBManager;
import model.TreeNode;



public class CreateUserView extends JPanel {

    private JLabel uploadLabel;
    private JLabel uploadPath;
    private JButton uploadButton;
    private JButton backButton;
    private JButton confirmationButton;


    private JComboBox selectGroup;
    private JPasswordField passwordText;
    private JPasswordField passwordConfirmationText;

    private NavigationView navigation;

    // Info
    private String groupNone = "Selecione um grupo";
    private String[] groups = {groupNone, "administrador", "usuario"};
    private String path = "";

    X509Certificate certificate;

    public CreateUserView(NavigationView navigation, int x, int y, int w, int h) {
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

        int selectOrigin = uploadOrigin + objectHeight + margin;
        this.selectGroup = new JComboBox<String>(this.groups);
        this.selectGroup.setBounds(margin, selectOrigin, objectWidth, objectHeight);
        this.add(this.selectGroup);

        int passwordLabelOrigin = selectOrigin + objectHeight + margin;
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

        this.confirmationButton = new JButton("Criar");
        this.confirmationButton.setBounds(objectWidth/2 + margin, buttonOrigin, objectWidth/2, objectHeight);
        this.confirmationButton.addActionListener(this.create());
        this.add(this.confirmationButton);

        this.setBackground(Color.white);

        DBManager.insereRegistro(6001);
    }

    public ActionListener create() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DBManager.insereRegistro(6002);

                // Verifica caminho especificado
                if (path.equals("")) {
                    JOptionPane.showMessageDialog(null, "Caminho do certificado não especificado.");
                    return;
                }
                // Verifica o grupo
                if (selectGroup.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Necessário definir um grupo para o usuário.");
                    return;
                }
                String group = groups[selectGroup.getSelectedIndex()];
                // Verifica tamanho da senha
                String text = new String(passwordText.getPassword());
                int size = text.length();
                if (size < 6 || size > 8) {
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

                    DBManager.insereRegistro(6003);

                    return;
                }

                DBManager.insereRegistro(6005);

                confirm(path, original, group);
            }
        };
    }


    private void confirm(String pathString, String password, String group) {
        try {
            certificate = CertifyService.sharedInstance().getCertificate(pathString);

            String ceriticateText = "<html>Versão: " + certificate.getVersion();
            ceriticateText += "<br>Série: " + certificate.getSerialNumber();
            ceriticateText += "<br>Validade: " + certificate.getNotAfter();
            ceriticateText += "<br>Tipo de Assinatura: " + certificate.getSigAlgName();
            ceriticateText += "<br>Emissor: " + getNameFromStringNameCert(certificate.getIssuerDN().getName());
            ceriticateText += "<br>Usuário: " + getNameFromStringNameCert(certificate.getSubjectDN().getName());
            ceriticateText += "<br>Email: " + getEmailFromStringNameCert(certificate.getSubjectDN().getName());

            ceriticateText += "</html>";

            new ConfirmView(this, ceriticateText);

        } catch (Exception e) {
            String message = e.getLocalizedMessage();
            JOptionPane.showMessageDialog(null, "Erro ao ler certificado: " + message);

            DBManager.insereRegistro(6006);
        }
    }

    private String getEmailFromStringNameCert(String cert) {
        String[] parts = cert.split(",");
        String part = parts[0];
        if (part.contains("=")) {
            parts = part.split("=");
            if (parts.length > 1) {
                return parts[1];
            } else {
                return part;
            }
        } else {
            return part;
        }
    }

    private String getNameFromStringNameCert(String cert) {
        String[] parts = cert.split(",");
        String part = parts[1];
        if (part.contains("=")) {
            parts = part.split("=");
            if (parts.length > 1) {
                return parts[1];
            } else {
                return part;
            }
        } else {
            return part;
        }
    }
    public void createUser() {
        String password = new String(passwordText.getPassword());
        String group = groups[selectGroup.getSelectedIndex()];

        try {
            if (AuthenticationService.sharedInstance().createUser(group, password, certificate)) {
                JOptionPane.showMessageDialog(null, "Usuario criado com sucesso!");
                this.cleanAll();
            }
        } catch (Exception e) {
            String message = e.getLocalizedMessage();
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private void cleanPassword() {
        this.passwordText.setText("");
        this.passwordConfirmationText.setText("");
    }

    public ActionListener back() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DBManager.insereRegistro(6007);

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
                    DBManager.insereRegistro(6004);
                }
            }
        };
    }

    private void cleanAll() {
        this.passwordConfirmationText.setText("");
        this.passwordText.setText("");
        this.path = "";
        this.uploadPath.setText("");
    }


}
