package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

import model.*;
import utils.UDPUtils;

public class ClientHandler extends Thread {
	private Etudiant e;
	private boolean isNewUser;
	private InetAddress ip;
	private int port;
	private String login;
	private DatagramSocket socket;
	
	public ClientHandler(DatagramSocket clientSocket, InetAddress ip, int port, String login, boolean isNewUser) {
		this.ip = ip;
		this.port = port;
		this.login = login;
		this.isNewUser = isNewUser;
		this.socket = clientSocket;
	}
	private boolean isValid(String coords) {
		if(!coords.contains(",")) return false;
		String[] nomEtNiveau = coords.split(",");
		for(int i=0; i< nomEtNiveau.length; i++) {
			if(nomEtNiveau[i].isEmpty()) return false;
		}
		return true;
	}
	private Etudiant createProfile(String login, String nom, String niveau, InetAddress ip, int port) {
		return new Etudiant(nom, login, niveau, true, ip, port);
	}
	
	private void disconnect() {
		Server.students.get(Server.students.indexOf(this.e)).setOnline(false);
	}
	
	public void run() {
		String message = socket.getLocalPort() + "";
		UDPUtils.sendMessage(socket, message, ip, port);
		try {
			//Sleep so the client can start it's listener
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if(isNewUser) {
			message = "Bienvenue, " + this.login + " vous êtes nouveau sur le serveur";
			UDPUtils.sendMessage(socket, message, ip, port);
			UDPUtils.sendMessage(socket, "Merci d'indiquer vos coordonnées comme suit [nom, niveau]", ip, port);
			String coords = UDPUtils.recieveMessage(socket);
			while(isValid(coords) == false) {
				UDPUtils.sendMessage(socket, "Données Invalide entrez à nouveau !", ip, port);
				coords = UDPUtils.recieveMessage(socket);
			}
			String[] nomEtNiveau = coords.split(",");
			this.e = createProfile(this.login, nomEtNiveau[0].split("\00")[0], nomEtNiveau[1].split("\00")[0], this.ip, this.port);
			UDPUtils.sendMessage(socket, "Profil créé !", ip, port);
			Server.students.add(this.e);
		}
		else {
			message = new String("Bienvenue de nouveau " + this.login + "!");
			UDPUtils.sendMessage(socket, message, ip, port);
			this.e = Server.students.stream()
					.filter(etd -> (this.login).equals(etd.getLogin()))
					.findAny()
					.orElse(null);		
		}
		while(true) {
			message = UDPUtils.recieveMessage(socket);
			if(message.startsWith("#LISTE_ETDS")) {
				String users = "Etudiants en ligne : \n";
				List<Etudiant> connectedUsers = Server.students
						.stream()
						.filter(etd -> etd.isOnline() == true)
						.collect(Collectors.toList());
				
				for(Etudiant e : connectedUsers) {
					users += e.getNom() + " aka " + e.getLogin() + "@" + e.getNiveau() + "\n";	
					}
				UDPUtils.sendMessage(socket, users, ip, port);
			}else if(message.startsWith("#Histo")){
				String response = "Vos Messages :\n";
				List<Message> messages = Server.messages
						.stream()
						.filter(msg -> login.equals(msg.getReciever().getLogin()))
						.collect(Collectors.toList());
				for(Message msg: messages) {
					response += " - " + msg.getSender().getLogin() + " a dit \"" + msg.getContent() + "\"\n";
				}
				UDPUtils.sendMessage(socket, response, ip, port);
			}else if(message.startsWith("@#")) {
				if(!message.substring(2).contains("@#")) UDPUtils.sendHelp(socket, ip, port);
				else {
					String msgContent = message.substring(2).split("@#")[1].split("\00")[0];
					String userLogin = message.substring(2).split("@#")[0].split("\00")[0];
					Etudiant receiver = Server.students.stream()
							.filter(etd -> userLogin.equals(etd.getLogin()))
							.findAny()
							.orElse(null);
					if(receiver != null) {
						Message msg = new Message(e, receiver, msgContent);
						Server.sendMsgToUser(msg);
						Server.messages.add(msg);
					}else {
						UDPUtils.sendMessage(socket, "Etudiant " + userLogin + " introuvable.", ip, port);
					}
				}
			}else if(message.startsWith("#GROUPS")) {
				String msg = "Groupes disponibles : \n";
				List<ConversationGroup> grps = Server.groupConversations;
				for(ConversationGroup grp : grps) {
					msg += grp.getTitre() + "\n";
				}
				UDPUtils.sendMessage(socket, msg, ip, port);
			}else if(message.startsWith("#GROUP#")) {
				String titreGroup = message.substring(1).split("#")[1].split("\00")[0];
				ConversationGroup grp = new ConversationGroup(titreGroup);
				grp.addStudent(e);
				Server.groupConversations.add(grp);
				UDPUtils.sendMessage(socket, titreGroup + " Créé!", ip, port);
			}else if(message.startsWith("#>")) {
				String titreGroup = message.substring(2).split("\00")[0];
				ConversationGroup grp = Server.groupConversations.stream()
						.filter(g -> (titreGroup).equals(g.getTitre()))
						.findAny()
						.orElse(null);
				if(grp != null) {
					Etudiant membre = grp.getStudents()
							.stream()
							.filter(etd -> (e.getLogin()).equals(etd.getLogin()))
							.findAny()
							.orElse(null);
					if(membre == null) {
						Server.groupConversations.get(Server.groupConversations.indexOf(grp)).addStudent(e);
						UDPUtils.sendMessage(socket, "Vous êtes desormais un membre de " + titreGroup, ip, port);						
					}else {
						UDPUtils.sendMessage(socket, "Vous êtes déja un membre. " + titreGroup, ip, port);
					}
				}else {
					UDPUtils.sendMessage(socket, "Group inéxistant.", ip, port);
				}
			}else if(message.startsWith("#ETDS#")) {
				String titreGroup = message.split("#ETDS#")[1].split("\00")[0];
				String membres = "Les membres de ce group sont : \n";
				ConversationGroup grp = Server.groupConversations
						.stream()
						.filter(gp -> titreGroup.equals(gp.getTitre()))
						.findAny()
						.orElse(null);
				if(grp != null) {
					for(Etudiant e: grp.getStudents()) {
						membres += e.getLogin() + "\n";
					}
					UDPUtils.sendMessage(socket, membres, ip, port);
				}else {
					UDPUtils.sendMessage(socket, "Group inéxistant.", ip, port);
				}
			}else if(message.startsWith("@>")) {
				if(!message.substring(2).contains("@>")) UDPUtils.sendHelp(socket, ip, port);
				else {
					String titreGroup = message.substring(2).split("@>")[0].split("\00")[0];
					String messageContent = message.substring(2).split("@>")[1].split("\00")[0];
					ConversationGroup grp = Server.groupConversations
							.stream()
							.filter(gp -> titreGroup.equals(gp.getTitre()))
							.findAny()
							.orElse(null);
					if(grp != null) {
						Message msg = new Message(e, grp, messageContent);
						Server.sendMsgToGroup(msg);
					}else {
						UDPUtils.sendMessage(socket, "Group inéxistant.", ip, port);
					}
				}
			}else if(message.toLowerCase().startsWith("exit()")){
				disconnect();
				UDPUtils.sendMessage(socket, "Au revoir !", ip, port);
				break;
			}else if(message.toLowerCase().startsWith("help")) {
				UDPUtils.sendHelp(socket, ip, port);
			}else {
				UDPUtils.sendMessage(socket, "Entrée invalide tappez 'help' pour savoir le syntaxe.", ip, port);
			}
		}
	}
	
	
}
