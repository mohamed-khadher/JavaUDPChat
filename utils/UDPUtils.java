package utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPUtils {
	public static void sendMessage(DatagramSocket socket, String message, InetAddress ip, int port) {
		byte[] buffer = (new String(message)).getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ip, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String recieveMessage(DatagramSocket socket) {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(packet);
			return new String(packet.getData());
		} catch (IOException e) {
			e.printStackTrace();
			return "Une erreur s'est produite ...";
		}
	}
	public static void sendHelp(DatagramSocket socket, InetAddress ip, int port) {
		sendMessage(socket, "Syntaxe : \n - #LISTE_ETDS : liste les etudiants en ligne.\n - #Histo : historique de vos messages", ip, port);
		sendMessage(socket, " - @#login@#message : pour envoyer un message � un �tudiant\n - #GROUPS : afficher les groupes priv�s)", ip, port);
		sendMessage(socket, " - #Group#titreGroupe : cr�er un group priv�\n - #>titreGroupe : pour joindre un group\n - #ETDS#titreGroup : affiche les etudiants d'un groupe priv�)", ip, port);
		sendMessage(socket, " - @>titreGroupe@>message : envoyer un message � tous les etudiants d'un group\n - exit() : pour se d�connecter", ip, port);
	}
}
