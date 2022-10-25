package model;

import java.net.InetAddress;

public class Etudiant {
	private String nom;
	private String login;
	private String niveau;
	private boolean isOnline;
	private InetAddress ip;
	private int port;
	
	
	public Etudiant() {};
	
	public Etudiant(String nom, String login, String niveau) {
		super();
		this.nom = nom;
		this.login = login;
		this.niveau = niveau;
	}
	
	public Etudiant(String nom, String login, String niveau, boolean isOnline, InetAddress ip, int port) {
		super();
		this.nom = nom;
		this.login = login;
		this.niveau = niveau;
		this.isOnline = isOnline;
		this.ip = ip;
		this.port = port;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getNiveau() {
		return niveau;
	}
	public void setNiveau(String niveau) {
		this.niveau = niveau;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public InetAddress getIp() {
		return ip;
	}
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
	
}
