package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.AuthenticationService;
import controller.DBManager;

public class ExitView  extends JPanel {

    private NavigationView navigation;

    private JLabel label;

    private JButton backButton;
    private JButton confirmationButton;


    public ExitView(NavigationView navigation, int x, int y, int w, int h) {
        this.navigation = navigation;
        this.setBounds(x, y, w, h);

        setLayout(null);

        int margin = 20;
        int objectWidth = w-(margin*2);
        int objectHeight = 30;

        this.label = new JLabel("Tem certeza que deseja sair?");
        this.label.setBounds(margin, margin, objectWidth, objectHeight);
        this.add(this.label);


        int buttonOrigin = margin*2 + objectHeight ;
        // Botao de voltar
        this.backButton = new JButton("Voltar");
        this.backButton.setBounds(margin, buttonOrigin, objectWidth/2, objectHeight);
        this.backButton.addActionListener(this.back());
        this.add(this.backButton);

        this.confirmationButton = new JButton("Sair");
        this.confirmationButton.setBounds(objectWidth/2 + margin, buttonOrigin, objectWidth/2, objectHeight);
        this.confirmationButton.addActionListener(this.quit());
        this.add(this.confirmationButton);

        this.setBackground(Color.white);

        DBManager.insereRegistro(9001);
    }

    public ActionListener back() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DBManager.insereRegistro(9004);

                navigation.selectMenu();
            }
        };
    }

    public ActionListener quit() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DBManager.insereRegistro(9003);

                AuthenticationService.sharedInstance().loggout();
                new LoginView("INF1416");
                navigation.dispose();
                
            }
        };
    }
}
