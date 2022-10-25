package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.*;
import utils.UDPUtils;
public class Server {
	public static List<Etudiant> students = new ArrayList<Etudiant>();
	public static List<ConversationGroup> groupConversations = new ArrayList<ConversationGroup>();
	public static List<Message> messages = new ArrayList<Message>();
	public static int port = 4080;
	public static DatagramSocket serverSocket;
	
	private static boolean isNewUser(String login) {
		List<Etudiant> results = students.stream().filter(e -> (e.getLogin()).equals(login) == true)
				.collect(Collectors.toList());
		return results.size() == 0;
	}
	
	public static void sendMsgToUser(Message msg) {
		Etudiant receiver = msg.getReciever();
		InetAddress ip = receiver.getIp();
		int port = receiver.getPort();
		String senderLogin = msg.getSender().getLogin();
		UDPUtils.sendMessage(serverSocket, senderLogin + "# " + msg.getContent(), ip, port);
	}
	
	public static void sendMsgToGroup(Message msg) {
		List<Etudiant> receivers = msg.getRecieverGroup().getStudents();
		InetAddress ip;
		int port;
		String msgContent = msg.getContent();
		String toBeSent = msg.getSender().getLogin() + "@" + msg.getRecieverGroup().getTitre() + "# " + msgContent;
		for(Etudiant e: receivers) {
			ip = e.getIp();
			port = e.getPort();
			UDPUtils.sendMessage(serverSocket, toBeSent, ip, port);
		}
	}
	
	public static void main(String args[]) throws IOException, InterruptedException {
		serverSocket = new DatagramSocket(port);
		System.out.println("Server up and running");
		while(true){
			byte[] buffer = new byte[256];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(packet);
			String message = new String(packet.getData());
			if(!message.startsWith("##")) continue;
			else {
				String login = message.split("##")[1].split("\00")[0];
				ClientHandler clientHandler;
				DatagramSocket clientSocket = new DatagramSocket(0);
				clientHandler = new ClientHandler(clientSocket, packet.getAddress(), packet.getPort(), login, isNewUser(login));
				clientHandler.start();
			}
			
		}
		
	}
	
	
}
