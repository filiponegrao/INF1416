package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import controller.DBManager;

public class User {
	
	private String name;
	private String email;
	private String groupName;
	private String salt;
	private String password;
	private Date bloquedAt;
	private String certificate;
	private int totalAccesses;
	private int totalReads;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String username) {
		this.email = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getBloquedAt() {
		return bloquedAt;
	}
	
	public void setBloquedAt(Date bloquedAt) {
		this.bloquedAt = bloquedAt;
	}
	
	public String getCertificate() {
		return certificate;
	}
	
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public int getTotalAccesses() {
		return totalAccesses;
	}
	
	public void setTotalAccesses(int totalAccesses) {
		this.totalAccesses = totalAccesses;
	}
	
	public int getTotalReads() {
		return totalReads;
	}
	
	public void setTotalReads(int totalReads) {
		this.totalReads = totalReads;
	}

	public static User fromMap(HashMap<String, Object> map) throws ParseException {
		User user = new User();
		String name = (String) map.get("name");
		String email = (String) map.get("email");
		String group = (String) map.get("groupName");
		String password = (String) map.get("password");
		String salt = (String) map.get("salt");
		String dateString = (String) map.get("bloquedAt");
		String certificate = (String) map.get("certificate");
		int totalAccesses = (int) map.get("totalAccesses");
		int totalReads = (int) map.get("totalReads");

		if (name != null) {
			user.setName(name);
		}
		if (email != null) {
			user.setEmail(email);
		}
		if (group != null) {
			user.setGroupName(group);
		}
		if (password != null) {
			user.setPassword(password);
		}
		if (salt != null) {
			user.setSalt(salt);
		}
		if (dateString != null) {
			Locale locale = new Locale("pt", "BR");			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
			Date date = format.parse(dateString);
			user.bloquedAt = date;
		}
		if (certificate != null) {
			user.setCertificate(certificate);
		}
		
		user.setTotalAccesses(totalAccesses);
		user.setTotalReads(totalReads);
		
		return user;
	}
	

	
	public boolean isBlocked() {
		if (this.getBloquedAt() != null) {
			Date date = this.getBloquedAt();
			Date now = new Date();
			long milseconds = now.getTime() - date.getTime();
			long seconds = milseconds/1000;
			long twoMinutes = 60 * 2;
			System.out.println(seconds);
			if (seconds <= twoMinutes) {
				DBManager.insereRegistro(2004);

				return true;
			}
		}
		DBManager.insereRegistro(2003);
		
		return false;
	}
	
	@Override
	public String toString() {		
		return this.email + ", " + this.password + ", " + this.salt;
	}






}
