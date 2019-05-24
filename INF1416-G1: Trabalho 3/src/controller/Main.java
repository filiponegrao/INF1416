package controller;

import java.text.ParseException;

import view.LoginView;
import view.NavigationView;

public class Main {

    public static void main(String[] args) throws ParseException {
        DBManager.insereRegistro(1001);
        new LoginView("INF1416");

        DBManager.insereRegistro(1002);
    }

}
