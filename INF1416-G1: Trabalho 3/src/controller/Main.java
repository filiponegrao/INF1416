package controller;

import java.text.ParseException;

import view.LoginView;
import view.NavigationView;

public class Main {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		new LoginView("INF1416");

		// Teste: Direto para a sessao de arquivos
//		AuthenticationService.sharedInstance().verifyEmail("admin@inf1416.puc-rio.br");
//		new NavigationView();
//		
		DBManager.insereRegistro(1001);
	}

}
