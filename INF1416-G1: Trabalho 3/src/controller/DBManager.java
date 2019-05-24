package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import model.User;


public class DBManager {

	public static Connection connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			return DriverManager.getConnection("jdbc:sqlite:db.sqlite");
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		return null;
	}
	
	
	// MARK: User
	
	public static boolean createUser(String name, String email, String group, String salt, String password, String certDig) throws Exception {
		// Verifica se ja nao existe um usuario com esse email
		User existentUser = DBManager.getUser(email);
		if (existentUser != null) {
			throw new Exception("Já existe um usuário com este e-mail.");
		}
		
		return insertIntoDb(String.format("INSERT INTO user VALUES "
				+ "('%s', '%s', '%s', '%s', '%s', null, 0, 0, '%s')"
				, name, email, group, salt, password, certDig));
	}
	
	public static User getUser(String email) throws ClassNotFoundException, ParseException {
		List<HashMap<String, Object>> mapList = selectFromDb(String.format("SELECT * FROM user WHERE email = '%s'", email));
		User user = null;
		if (mapList.size() > 0) {
			HashMap<String, Object> map = mapList.get(0);
			user = User.fromMap(map);
		}
		return user;
	}
	
	public static void changeUserPassword(String novaSenha, String email) {
		updateDb(String.format("UPDATE user SET password = '%s' WHERE email = '%s'", novaSenha, email));
	}
	
	public static void changeUserCert(String certificado, String email) {
		updateDb(String.format("UPDATE user SET certificate = '%s' WHERE email = '%s'", certificado, email));
	}
	
	public static int getUsersCount() {
		return selectFromDb(String.format("SELECT * FROM user")).size();
	}
	
	public static User blockUserAccess(User user) throws ClassNotFoundException, ParseException {
		String email = user.getEmail();
		
		Locale locale = new Locale("pt", "BR");			
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
		String dateString = format.format(new Date());
		String query = "update user set bloquedAt = '" + dateString + "' where email = '" + email + "';";
		updateDb(query);
		
		return getUser(user.getEmail());
	}
	
	public static void addUserAccess(User user) {
		String email = user.getEmail();
		updateDb(String.format("UPDATE user SET totalAccesses = totalAccesses+1 WHERE email = '%s'", email));

	}
	
	// MARK: Groups

	public static List getLog() {
		return selectFromDb("select Registro.id, email, texto from Registro JOIN Mensagem ON Mensagem.id = Registro.messageId order by Registro.id, created;");
	}



	public static boolean insereRegistro(int idMsg) {
		User user = AuthenticationService.sharedInstance().getUser();
		if (user != null) {
			return insereRegistro(idMsg, user.getEmail());

		} else {
			return insereRegistro(idMsg, null, null);
		}
	}

	public static boolean insereRegistro(int idMsg, String email) {
		return insereRegistro(idMsg, email, null);
	}

	public static boolean insereRegistro(int idMsg, String email, String arquivo) {
		User user = AuthenticationService.sharedInstance().getUser();
		String emailUser = email;
		if (emailUser == null || emailUser.isEmpty()) {
			if (user != null) {
				emailUser = user.getEmail();
			}
		}
		
		return insertIntoDb(String.format("INSERT INTO register (messageId, email, filename) VALUES ('%d', '%s', '%s')", idMsg, emailUser, arquivo));
	}





	




	public static void incrementaNumChavePrivadaErrada(String email) {
		updateDb(String.format("UPDATE user SET numChavePrivadaErrada = numChavePrivadaErrada + 1 WHERE email = '%s'", email));
	}

	public static void zeraNumChavePrivadaErrada(String email) {
		updateDb(String.format("UPDATE user SET numChavePrivadaErrada = 0 WHERE email = '%s'", email));
	}	

	public static void incrementaTotalAcessos(String email) {
		updateDb(String.format("UPDATE user SET totalAcessos = totalAcessos + 1 WHERE email = '%s'", email));
	}


	// MARK: Private methods

	private static boolean insertIntoDb(String query) {
		Connection conn = connect();
		try {
			Statement stat = conn.createStatement();
			stat.setQueryTimeout(30);
			stat.executeUpdate(query);
			stat.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			closeConn(conn);
			return false;
		}
		closeConn(conn);
		return true;
	}

	private static void updateDb(String query) {
		Connection conn = connect();
		try {
			Statement stat = conn.createStatement();
			stat.setQueryTimeout(30);
			stat.executeUpdate(query);
			stat.close();			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		closeConn(conn);
	}

	private static List<HashMap<String,Object>> selectFromDb(String query) {
		Connection conn = connect();
		try {
			Statement stat = conn.createStatement();
			stat.setQueryTimeout(30);
			ResultSet res = stat.executeQuery(query);
			List<HashMap<String,Object>> lst = convertResultSetToList(res);
			stat.close();
			closeConn(conn);
			return lst;

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			closeConn(conn);
			return null;
		}
	}

	private static boolean closeConn(Connection conn) {
		try {
			if (conn != null) 
				conn.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			return false;
		}
		return true;
	}

	private static List<HashMap<String,Object>> convertResultSetToList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

		while (rs.next()) {
			HashMap<String,Object> row = new HashMap<String, Object>(columns);
			for(int i=1; i<=columns; ++i) {
				row.put(md.getColumnName(i),rs.getObject(i));
			}
			list.add(row);
		}

		return list;
	}

}
