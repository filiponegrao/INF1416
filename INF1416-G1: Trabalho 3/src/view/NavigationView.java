package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.ParseException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.AuthenticationService;
import controller.DBManager;
import model.User;

public class NavigationView extends JFrame {

    Container container;

    private int width = 0;
    private int height = 0;

    // Static views
    HeaderView headerView;
    InfoView infoView;
    JPanel contentView;

    // Dynamic views
    MenuView menuPanel;
    CreateUserView createUserPanel;
    ChangePassword changePassword;
    FilesPanel filesPanel;
    ExitView exitPanel;


    private String infoString = "";
    private User user;

    // Medidas
    int contentX = 0;
    int contentY = 0;
    int contentW = 0;
    int contentH = 0;

    public NavigationView() {
        setTitle("INF1416 - Sistema de arquivos");

        this.container = this.getContentPane();

        this.user = AuthenticationService.sharedInstance().getUser();
        // Atualiza o usuario
        try {
            this.user = AuthenticationService.sharedInstance().verifyEmail(this.user.getEmail());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Define o quit pelo botao
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Define as dimensoes do frame
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.width = (int) dimension.getWidth()/2;
        this.height = (int) (this.width);
        this.setSize (this.width, this.height);
        this.setResizable(false);

        // Define a posicao
        int x = (int) ((dimension.getWidth() - this.width) / 2);
        int y = (int) ((dimension.getHeight() - this.height) / 2);
        this.setLocation(x, y);

        // Define variaveis visuais
        Container content = this.getContentPane();
        this.setLayout(null);
        int margin = 20;
        int objectWidth = this.width-(margin*2);
        int objectHeight = 30;

        // CONFIGURA A HEADER
        int headerHeight = objectHeight*4;
        this.headerView = new HeaderView(margin, margin, objectWidth, headerHeight);
        content.add(this.headerView);

        // CONFIGURA A INFO VIEW
        int infoOrigin = margin + headerHeight;
        int infoHeight = objectHeight;
        this.infoString = "Total de acessos: " + Integer.toString(user.getTotalAccesses());
        this.infoView = new InfoView(margin, infoOrigin, objectWidth, infoHeight, infoString);
        content.add(this.infoView);

        int contentHeight = this.height - (margin + headerHeight) - (infoHeight + margin);

        int menuorigin = margin + headerHeight + infoHeight;

        this.contentX = margin;
        this.contentY = menuorigin;
        this.contentW = objectWidth;
        this.contentH = contentHeight;

        this.menuPanel = new MenuView(this, this.contentX, this.contentY, this.contentW, this.contentH);
        this.contentView = this.menuPanel;
        content.add(this.contentView);

        this.setVisible(true);

        DBManager.insereRegistro(5001);
    }

    public void selectMenu() {
        // Cria o menu
        this.menuPanel = new MenuView(this, this.contentX, this.contentY, this.contentW, this.contentH);
        this.remove(this.contentView);
        this.contentView = this.menuPanel;
        this.container.add(this.contentView);

        // Atualiza a info view
        String userCount = "Total de acessos: " + Integer.toString(DBManager.getUsersCount());
        this.infoView.setText(userCount);

        this.repaint();
    }

    public void selectCreateUser() {
        DBManager.insereRegistro(5002);

        // Cria o novo painel
        this.createUserPanel = new CreateUserView(this, this.contentX, this.contentY, this.contentW, this.contentH);
        // Remove o antigo
        this.remove(this.contentView);
        // Atualiza
        this.contentView = this.createUserPanel;
        // Adiciona novamente
        this.container.add(this.contentView);

        // Atualiza a info view
        String userCount = "Total de usuários do sistema: " + Integer.toString(DBManager.getUsersCount());
        this.infoView.setText(userCount);
        // Pinta
        this.repaint();

    }

    public void selectChangePassword() {
        DBManager.insereRegistro(5003);

        // Cria o novo painel
        this.changePassword = new ChangePassword(this, this.contentX, this.contentY, this.contentW, this.contentH);
        // Remove o antigo
        this.remove(this.contentView);
        // Atualiza
        this.contentView = this.changePassword;
        // Adiciona novamente
        this.container.add(this.contentView);
        // Pinta
        this.repaint();
    }

    public void selectReadFiles() {
        DBManager.insereRegistro(5004);

        // Cria o novo painel
        this.filesPanel = new FilesPanel(this, this.contentX, this.contentY, this.contentW, this.contentH);
        // Remove o antigo
        this.remove(this.contentView);
        // Atualiza
        this.contentView = this.filesPanel;
        // Adiciona novamente
        this.container.add(this.contentView);
        // Pinta
        this.repaint();
    }

    public void selectExit() {
        DBManager.insereRegistro(5005);

        // Cria o novo painel
        this.exitPanel = new ExitView(this, this.contentX, this.contentY, this.contentW, this.contentH);
        // Remove o antigo
        this.remove(this.contentView);
        // Atualiza
        this.contentView = this.exitPanel;
        // Adiciona novamente
        this.container.add(this.contentView);
        // Pinta
        this.repaint();
    }

    public void quit() {
        dispose();
    }
}
