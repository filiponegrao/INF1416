package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import controller.AuthenticationService;
import controller.DBManager;
import model.User;

public class MenuView extends JPanel {

    private InfoView infoView;
    private JButton createUser;
    private JButton changeCredentials;
    private JButton readFiles;
    private JButton exit;

    private NavigationView navigation;

    public MenuView(NavigationView navigation, int x, int y, int w, int h) {
        this.navigation = navigation;

        this.setBounds(x, y, w, h);
        setLayout(null);


        int margin = 20;
        int objectWidth = w-(margin*2);
        int objectHeight = (h - (margin/2*5)) / 5;

        User user = AuthenticationService.sharedInstance().getUser();
        if (user.getGroupName().equals("administrador")) {
            this.createUser = new JButton("1 - Cadastrar novo usário");
            this.createUser.setBounds(margin, margin, objectWidth, objectHeight);
            this.createUser.addActionListener(this.selectCreateUser());
            this.add(this.createUser);
        }

        int credentialsorigin = margin + objectHeight + margin;

        this.changeCredentials = new JButton("2 - Alterar senha pessoal e certificado digital");
        this.changeCredentials.setBounds(margin, credentialsorigin, objectWidth, objectHeight);
        this.changeCredentials.addActionListener(this.selectChangePassword());
        this.add(this.changeCredentials);

        int filesorigin = credentialsorigin + objectHeight + margin;

        this.readFiles = new JButton("3 - Consultar pastas de arquivos");
        this.readFiles.setBounds(margin, filesorigin, objectWidth, objectHeight);
        this.readFiles.addActionListener(this.selectReadFiles());
        this.add(this.readFiles);

        int exitorigin = filesorigin + objectHeight + margin;

        this.exit = new JButton("4 - Sair do sistema");
        this.exit.setBounds(margin, exitorigin, objectWidth, objectHeight);
        this.exit.addActionListener(this.selectExit());
        this.add(this.exit);
    }

    public ActionListener selectCreateUser() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigation.selectCreateUser();
            }
        };
    }

    public ActionListener selectChangePassword() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigation.selectChangePassword();
            }
        };
    }

    public ActionListener selectReadFiles() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigation.selectReadFiles();
            }
        };
    }

    public ActionListener selectExit() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                navigation.selectExit();
            }
        };
    }


}
