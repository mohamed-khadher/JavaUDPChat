package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import utils.UDPUtils;

public class Client {
	private static InetAddress serverIp;
	private static int serverPort;
	private static DatagramSocket socket;
	
	public static void main(String args[]) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		serverIp = InetAddress.getLocalHost();
		serverPort = 4080;
		socket = new DatagramSocket();
		
		
		String message;
		do {
			System.out.println("Pour se connecter au serveur tappez ##[votre pseudo]");
			message = reader.readLine();
		}while(!message.startsWith("##"));
		
		byte[] buffer = new byte[512];
		buffer = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverIp, serverPort);
		socket.send(packet);
		
		//receive the first response to adjust to the new socket port given by the server
		byte[] responseBuffer = new byte[512];
		DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
		socket.receive(responsePacket);
		
		serverPort = responsePacket.getPort();
		Sender sender = new Sender(socket, serverIp, serverPort);
		Receiver receiver = new Receiver(socket);
		sender.start(); receiver.start();
		
	}
	
}

class Sender extends Thread{
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private InetAddress ip;
	private int port;
	private DatagramSocket socket;
	
	public Sender(DatagramSocket socket, InetAddress serverIp, int serverPort) {
		this.ip = serverIp;
		this.port = serverPort;
		this.socket = socket;
	}
	
	public void run() {
		String toBeSent = new String();
		while(true) {
			try {
				toBeSent = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(toBeSent.isEmpty()) {
				System.out.println("Entrez une commande valide pour communiquer.");
			}else {
				UDPUtils.sendMessage(socket, toBeSent, ip, port);
			}
		}
	}
}

class Receiver extends Thread{
	private DatagramSocket socket;
	
	public Receiver(DatagramSocket socket) {
		this.socket = socket;
	}
	
	public void run() {
		while(true) {
			String receivedMessage = UDPUtils.recieveMessage(socket);
			System.out.println(receivedMessage);
		}
	}
}